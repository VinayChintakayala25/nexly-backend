package com.nexly.backend.controller;

import com.nexly.backend.dto.ResumeResponse;
import com.nexly.backend.entity.Resume;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.ResumeRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/student/resumes")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    private final Path uploadDirectory =
            Paths.get("uploads", "resumes");

    public ResumeController(
            ResumeRepository resumeRepository,
            UserRepository userRepository) {

        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // UPLOAD RESUME
    // =====================================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {

            // Check file
            if (file == null || file.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Please select a resume file.");
            }

            // Check file size
            if (file.getSize() > 5 * 1024 * 1024) {

                return ResponseEntity
                        .badRequest()
                        .body("Resume must be smaller than 5 MB.");
            }

            // Check file type
            String contentType =
                    file.getContentType();

            boolean validType =
                    "application/pdf".equals(contentType)
                            ||
                            "application/msword".equals(contentType)
                            ||
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                    .equals(contentType);

            if (!validType) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Only PDF, DOC and DOCX files are allowed."
                        );
            }

            // Get logged-in student
            User student =
                    userRepository
                            .findByEmail(
                                    authentication.getName()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Student not found"
                                    )
                            );

            // Create upload folder
            Files.createDirectories(
                    uploadDirectory
            );

            // Original filename
            String originalName =
                    file.getOriginalFilename();

            if (originalName == null ||
                    originalName.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Invalid file name.");
            }

            // Remove unsafe path information
            String cleanFileName =
                    Paths.get(originalName)
                            .getFileName()
                            .toString();

            // Create unique stored filename
            String storedFileName =
                    System.currentTimeMillis()
                            + "_"
                            + cleanFileName;

            Path filePath =
                    uploadDirectory.resolve(
                            storedFileName
                    );

            // Save file
            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Save database record
            Resume resume =
                    new Resume();

            resume.setStudent(student);
            resume.setFileName(cleanFileName);
            resume.setFileUrl(
                    "/uploads/resumes/"
                            + storedFileName
            );
            resume.setFileType(contentType);

            Resume savedResume =
                    resumeRepository.save(resume);

            return ResponseEntity.ok(
                    convertToResponse(savedResume)
            );

        } catch (IOException exception) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Failed to upload resume."
                    );
        }
    }

    // =====================================================
    // GET MY RESUMES
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ResumeResponse>>
    getMyResumes(
            Authentication authentication) {

        User student =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        List<Resume> resumes =
                resumeRepository
                        .findByStudentOrderByUploadedAtDesc(
                                student
                        );

        List<ResumeResponse> response =
                resumes.stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // DOWNLOAD / VIEW RESUME
    // =====================================================

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource>
    downloadResume(
            @PathVariable Long id,
            Authentication authentication) {

        User student =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        Resume resume =
                resumeRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resume not found"
                                )
                        );

        // Ownership check
        if (!resume.getStudent()
                .getId()
                .equals(student.getId())) {

            throw new RuntimeException(
                    "You cannot access this resume."
            );
        }

        try {

            String storedFileName =
                    Paths.get(
                                    resume.getFileUrl()
                            )
                            .getFileName()
                            .toString();

            Path filePath =
                    uploadDirectory.resolve(
                            storedFileName
                    );

            Resource resource =
                    new UrlResource(
                            filePath
                                    .toUri()
                    );

            if (!resource.exists() ||
                    !resource.isReadable()) {

                throw new RuntimeException(
                        "Resume file not found."
                );
            }

            MediaType mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;

            if ("application/pdf".equals(
                    resume.getFileType())) {

                mediaType =
                        MediaType.APPLICATION_PDF;
            }

            ContentDisposition disposition =
                    ContentDisposition
                            .inline()
                            .filename(
                                    resume.getFileName()
                            )
                            .build();

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            disposition.toString()
                    )
                    .body(resource);

        } catch (MalformedURLException exception) {

            throw new RuntimeException(
                    "Unable to open resume."
            );
        }
    }

    // =====================================================
    // DELETE RESUME
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteResume(
            @PathVariable Long id,
            Authentication authentication) {

        User student =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );

        Resume resume =
                resumeRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resume not found"
                                )
                        );

        // Ownership check
        if (!resume.getStudent()
                .getId()
                .equals(student.getId())) {

            return ResponseEntity
                    .status(403)
                    .body(
                            "You can delete only your own resume."
                    );
        }

        try {

            if (resume.getFileUrl() != null) {

                String storedFileName =
                        Paths.get(
                                        resume.getFileUrl()
                                )
                                .getFileName()
                                .toString();

                Path filePath =
                        uploadDirectory.resolve(
                                storedFileName
                        );

                Files.deleteIfExists(
                        filePath
                );
            }

            resumeRepository.delete(resume);

            return ResponseEntity.ok(
                    "Resume deleted successfully."
            );

        } catch (IOException exception) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Failed to delete resume file."
                    );
        }
    }

    // =====================================================
    // CONVERT ENTITY → SAFE RESPONSE
    // =====================================================

    private ResumeResponse convertToResponse(
            Resume resume) {

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                resume.getUploadedAt()
        );
    }
}