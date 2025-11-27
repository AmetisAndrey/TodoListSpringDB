package com.example.jdbc.dao;

import com.example.jdbc.domain.Task;
import com.example.jdbc.domain.TaskPriority;
import com.example.jdbc.domain.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskDao {

    List<Task> findAll();

    Optional<Task> findById(Long id);

    Task save(Task task);

    void deleteById(Long id);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findOverdue(LocalDate date);

    List<Task> findAllForOwner(String owner);
    List<Task> findByStatusForOwner(TaskStatus status, String owner);
    List<Task> findByPriorityForOwner(TaskPriority priority, String owner);

}
