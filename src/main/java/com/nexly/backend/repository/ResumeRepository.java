package com.nexly.backend.repository;

import com.nexly.backend.entity.Resume;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByStudentOrderByUploadedAtDesc(User student);
}