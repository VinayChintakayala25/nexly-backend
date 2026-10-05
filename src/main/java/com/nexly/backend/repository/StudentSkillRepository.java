package com.nexly.backend.repository;

import com.nexly.backend.entity.StudentSkill;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSkillRepository
        extends JpaRepository<StudentSkill, Long> {

    List<StudentSkill> findByStudent(User student);

    void deleteByStudent(User student);
}