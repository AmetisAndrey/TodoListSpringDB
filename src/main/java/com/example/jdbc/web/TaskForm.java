package com.example.jdbc.web;

import com.example.jdbc.domain.TaskPriority;
import com.example.jdbc.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TaskForm {

    private Long id;

    @NotBlank(message = "Название обязательно")
    private String title;

    private String description;

    @NotNull(message = "Приоритет обязателен")
    private TaskPriority priority = TaskPriority.MEDIUM;

    @NotNull(message = "Статус обязателен")
    private TaskStatus status = TaskStatus.NEW;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
