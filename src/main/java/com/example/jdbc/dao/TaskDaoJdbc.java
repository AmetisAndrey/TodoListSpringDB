package com.example.jdbc.dao;

import com.example.jdbc.domain.Task;
import com.example.jdbc.domain.TaskPriority;
import com.example.jdbc.domain.TaskStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TaskDaoJdbc implements TaskDao {

    private final JdbcTemplate jdbc;

    public TaskDaoJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task t = new Task();
        t.setId(rs.getLong("id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setStatus(TaskStatus.valueOf(rs.getString("status")));
        t.setPriority(TaskPriority.valueOf(rs.getString("priority")));
        t.setDueDate(rs.getObject("due_date", LocalDate.class));
        t.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        t.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        t.setOwner(rs.getString("owner")); // НОВОЕ
        return t;
    };

    @Override
    public List<Task> findAll() {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                order by due_date nulls last, priority desc
                """;
        return jdbc.query(sql, taskRowMapper);
    }

    @Override
    public Optional<Task> findById(Long id) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where id = ?
                """;
        List<Task> list = jdbc.query(sql, taskRowMapper, id);
        return list.stream().findFirst();
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            return insert(task);
        } else {
            return update(task);
        }
    }

    private Task insert(Task task) {
        String sql = """
                insert into tasks (title, description, status, priority, due_date, created_at, updated_at, owner)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getPriority().name());
            ps.setObject(5, task.getDueDate());
            ps.setObject(6, task.getCreatedAt());
            ps.setObject(7, task.getUpdatedAt());
            ps.setString(8, task.getOwner());
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null) {
            Object idValue = keyHolder.getKeys().get("id");
            if (idValue instanceof Number) {
                task.setId(((Number) idValue).longValue());
            }
        }

        return task;
    }

    private Task update(Task task) {
        String sql = """
                update tasks
                set title = ?, description = ?, status = ?, priority = ?, due_date = ?, updated_at = ?, owner = ?
                where id = ?
                """;

        jdbc.update(sql,
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getPriority().name(),
                task.getDueDate(),
                task.getUpdatedAt(),
                task.getOwner(),
                task.getId());

        return task;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("delete from tasks where id = ?", id);
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where status = ?
                order by due_date nulls last
                """;
        return jdbc.query(sql, taskRowMapper, status.name());
    }

    @Override
    public List<Task> findByPriority(TaskPriority priority) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where priority = ?
                order by due_date nulls last
                """;
        return jdbc.query(sql, taskRowMapper, priority.name());
    }

    @Override
    public List<Task> findOverdue(LocalDate date) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where due_date < ?
                  and status <> 'DONE'
                order by due_date
                """;
        return jdbc.query(sql, taskRowMapper, date);
    }

    @Override
    public List<Task> findAllForOwner(String owner) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where owner = ?
                order by due_date nulls last, priority desc
                """;
        return jdbc.query(sql, taskRowMapper, owner);
    }

    @Override
    public List<Task> findByStatusForOwner(TaskStatus status, String owner) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where status = ?
                  and owner = ?
                order by due_date nulls last
                """;
        return jdbc.query(sql, taskRowMapper, status.name(), owner);
    }

    @Override
    public List<Task> findByPriorityForOwner(TaskPriority priority, String owner) {
        String sql = """
                select id, title, description, status, priority, due_date, created_at, updated_at, owner
                from tasks
                where priority = ?
                  and owner = ?
                order by due_date nulls last
                """;
        return jdbc.query(sql, taskRowMapper, priority.name(), owner);
    }
}
