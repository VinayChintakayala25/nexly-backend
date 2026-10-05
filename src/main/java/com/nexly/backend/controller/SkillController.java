package com.nexly.backend.controller;

import com.nexly.backend.dto.SkillRequest;
import com.nexly.backend.entity.Skill;
import com.nexly.backend.entity.StudentSkill;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.SkillRepository;
import com.nexly.backend.repository.StudentSkillRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/skills")
@CrossOrigin(origins = "http://localhost:5173")
public class SkillController {

    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final UserRepository userRepository;

    public SkillController(
            SkillRepository skillRepository,
            StudentSkillRepository studentSkillRepository,
            UserRepository userRepository) {

        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> addSkill(
            Authentication authentication,
            @RequestBody SkillRequest request) {

        User student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Skill skill = skillRepository
                .findByNameIgnoreCase(request.getSkillName())
                .orElseGet(() ->
                        skillRepository.save(
                                new Skill(request.getSkillName())
                        ));

        StudentSkill studentSkill = new StudentSkill();

        studentSkill.setStudent(student);
        studentSkill.setSkill(skill);
        studentSkill.setLevel(request.getLevel());

        return ResponseEntity.ok(
                studentSkillRepository.save(studentSkill)
        );
    }

    @GetMapping
    public ResponseEntity<List<StudentSkill>> getSkills(
            Authentication authentication) {

        User student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(
                studentSkillRepository.findByStudent(student)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSkill(
            Authentication authentication,
            @PathVariable Long id) {

        User student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        StudentSkill studentSkill =
                studentSkillRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Skill not found"));

        if (!studentSkill.getStudent()
                .getId()
                .equals(student.getId())) {

            return ResponseEntity
                    .status(403)
                    .body("You cannot delete this skill");
        }

        studentSkillRepository.delete(studentSkill);

        return ResponseEntity.ok(
                "Skill deleted successfully"
        );
    }
}