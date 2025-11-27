package com.example.jdbc.service;

import com.example.jdbc.dao.TaskDao;
import com.example.jdbc.domain.Task;
import com.example.jdbc.domain.TaskPriority;
import com.example.jdbc.domain.TaskStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null ? auth.getName() : "anonymous");
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }


    public List<Task> getTasks(TaskStatus status, TaskPriority priority) {
        String username = currentUsername();

        if (isCurrentUserAdmin()) {
            // Админ видит все задачи
            if (status != null) {
                return taskDao.findByStatus(status);
            }
            if (priority != null) {
                return taskDao.findByPriority(priority);
            }
            return taskDao.findAll();
        } else {
            if (status != null) {
                return taskDao.findByStatusForOwner(status, username);
            }
            if (priority != null) {
                return taskDao.findByPriorityForOwner(priority, username);
            }
            return taskDao.findAllForOwner(username);
        }
    }

    public List<Task> getOverdueTasks(LocalDate date) {
        if (isCurrentUserAdmin()) {
            return taskDao.findOverdue(date);
        } else {
            return taskDao.findOverdue(date).stream()
                    .filter(t -> t.getOwner().equals(currentUsername()))
                    .toList();
        }
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

        task.setOwner(currentUsername());

        return taskDao.save(task);
    }


    @Transactional
    public Task updateTask(Long id,
                           String title,
                           String description,
                           TaskStatus status,
                           TaskPriority priority,
                           LocalDate dueDate) {

        Task task = taskDao.findById(id).orElseThrow();

        if (!isCurrentUserAdmin() && !task.getOwner().equals(currentUsername())) {
            throw new IllegalStateException("Нет прав на изменение этой задачи");
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setUpdatedAt(LocalDateTime.now());

        return taskDao.save(task);
    }


    @Transactional
    public void markDone(Long id) {
        Task task = taskDao.findById(id).orElseThrow();

        if (!isCurrentUserAdmin() && !task.getOwner().equals(currentUsername())) {
            throw new IllegalStateException("Нет прав на изменение этой задачи");
        }

        task.setStatus(TaskStatus.DONE);
        task.setUpdatedAt(LocalDateTime.now());
        taskDao.save(task);
    }


    @Transactional
    public void deleteTask(Long id) {
        Task task = taskDao.findById(id).orElseThrow();

        if (!isCurrentUserAdmin()) {
            throw new IllegalStateException("Удаление разрешено только администратору");
        }

        taskDao.deleteById(id);
    }

    public Task getById(Long id) {
        return taskDao.findById(id).orElseThrow();
    }
}
