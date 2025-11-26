package com.example.jdbc.service;

import com.example.jdbc.dao.TaskDao;
import com.example.jdbc.domain.Task;
import com.example.jdbc.domain.TaskPriority;
import com.example.jdbc.domain.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Transactional(readOnly = true)
    public List<Task> getAll() {
        return taskDao.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Task> getById(Long id) {
        return taskDao.findById(id);
    }

    @Transactional
    public Task createTask(String title,
                           String description,
                           TaskPriority priority,
                           LocalDate dueDate) {

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(TaskStatus.NEW);
        task.setDueDate(dueDate);

        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        return taskDao.save(task);
    }

    @Transactional
    public Task updateTask(Long id,
                           String newTitle,
                           String newDescription,
                           TaskPriority priority,
                           LocalDate dueDate,
                           TaskStatus status) {

        Task task = taskDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена: id=" + id));

        if (newTitle != null && !newTitle.isBlank()) {
            task.setTitle(newTitle);
        }
        if (newDescription != null && !newDescription.isBlank()) {
            task.setDescription(newDescription);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        task.setStatus(status);
        task.setDueDate(dueDate);
        task.setUpdatedAt(LocalDateTime.now());

        return taskDao.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        taskDao.deleteById(id);
    }

    @Transactional
    public void markDone(Long id) {
        Task task = taskDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена: id=" + id));
        task.setStatus(TaskStatus.DONE);
        task.setUpdatedAt(LocalDateTime.now());
        taskDao.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getByStatus(TaskStatus status) {
        return taskDao.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Task> getByPriority(TaskPriority priority) {
        return taskDao.findByPriority(priority);
    }

    @Transactional(readOnly = true)
    public List<Task> getOverdue() {
        return taskDao.findOverdue(LocalDate.now());
    }
}
