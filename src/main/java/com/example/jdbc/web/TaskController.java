package com.example.jdbc.web;

import com.example.jdbc.domain.Task;
import com.example.jdbc.domain.TaskPriority;
import com.example.jdbc.domain.TaskStatus;
import com.example.jdbc.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks") // БАЗОВЫЙ ПУТЬ: всё ниже /tasks/...
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET /tasks
    @GetMapping
    public String listTasks(@RequestParam(required = false) TaskStatus status,
                            @RequestParam(required = false) TaskPriority priority,
                            Model model,
                            @ModelAttribute("message") String message) {

        List<Task> tasks = taskService.getAll();

        if (status != null) {
            tasks = tasks.stream().filter(t -> t.getStatus() == status).toList();
        }
        if (priority != null) {
            TaskPriority p = priority;
            tasks = tasks.stream().filter(t -> t.getPriority() == p).toList();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("allStatuses", TaskStatus.values());
        model.addAttribute("allPriorities", TaskPriority.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);

        return "list"; // templates/list.html
    }

    // GET /tasks/new
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        TaskForm form = new TaskForm();
        model.addAttribute("taskForm", form);
        model.addAttribute("allStatuses", TaskStatus.values());
        model.addAttribute("allPriorities", TaskPriority.values());
        model.addAttribute("isNew", true);
        return "form"; // templates/form.html
    }

    // POST /tasks
    @PostMapping
    public String createTask(@Valid @ModelAttribute("taskForm") TaskForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allStatuses", TaskStatus.values());
            model.addAttribute("allPriorities", TaskPriority.values());
            model.addAttribute("isNew", true);
            return "form";
        }

        taskService.createTask(
                form.getTitle(),
                form.getDescription(),
                form.getPriority(),
                form.getDueDate()
        );

        redirectAttributes.addFlashAttribute("message", "Задача создана");
        return "redirect:/tasks";
    }

    // GET /tasks/{id}/edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена: " + id));

        TaskForm form = new TaskForm();
        form.setId(task.getId());
        form.setTitle(task.getTitle());
        form.setDescription(task.getDescription());
        form.setPriority(task.getPriority());
        form.setStatus(task.getStatus());
        form.setDueDate(task.getDueDate());

        model.addAttribute("taskForm", form);
        model.addAttribute("allStatuses", TaskStatus.values());
        model.addAttribute("allPriorities", TaskPriority.values());
        model.addAttribute("isNew", false);
        return "form";
    }

    // POST /tasks/{id}/edit
    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("taskForm") TaskForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allStatuses", TaskStatus.values());
            model.addAttribute("allPriorities", TaskPriority.values());
            model.addAttribute("isNew", false);
            return "form";
        }

        taskService.updateTask(
                id,
                form.getTitle(),
                form.getDescription(),
                form.getPriority(),
                form.getDueDate(),
                form.getStatus()
        );

        redirectAttributes.addFlashAttribute("message", "Задача обновлена");
        return "redirect:/tasks";
    }

    // POST /tasks/{id}/delete
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("message", "Задача удалена");
        return "redirect:/tasks";
    }

    // POST /tasks/{id}/done
    @PostMapping("/{id}/done")
    public String markDone(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        taskService.markDone(id);
        redirectAttributes.addFlashAttribute("message", "Задача закрыта");
        return "redirect:/tasks";
    }
}
