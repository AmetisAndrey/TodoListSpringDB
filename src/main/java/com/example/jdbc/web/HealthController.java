package com.example.jdbc.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public String health(Model model) {
        boolean dbOk = true;
        String dbMessage = "База данных доступна";

        try {
            jdbcTemplate.queryForObject("select 1", Integer.class);
        } catch (Exception ex) {
            dbOk = false;
            dbMessage = "Ошибка подключения к базе данных";
        }

        model.addAttribute("appName", "Task Manager");
        model.addAttribute("dbOk", dbOk);
        model.addAttribute("dbMessage", dbMessage);
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("javaVersion", System.getProperty("java.version"));

        return "health";
    }
}
