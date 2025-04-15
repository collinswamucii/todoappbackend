package com.app.todoapp.repository;

import com.app.todoapp.models.Priority;
import com.app.todoapp.models.Status;
import com.app.todoapp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TasksRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerUsername(String ownerUsername);

    Optional<Task> findByIdAndOwnerUsername(Long id, String ownerUsername);

    boolean existsByIdAndOwnerUsername(Long id, String ownerUsername);

    // For admin: find tasks by status (all users)
    List<Task> findByStatus(Status status);

    // For regular users: find tasks by status and owner
    List<Task> findByStatusAndOwnerUsername(Status status, String ownerUsername);

    // For admin: find tasks by priority (all users)
    List<Task> findByPriority(Priority priority);

    // For regular users: find tasks by priority and owner
    List<Task> findByPriorityAndOwnerUsername(Priority priority, String ownerUsername);

    // For admin: find tasks by due date before (all users)
    List<Task> findByDueDateBefore(LocalDate date);

    // For regular users: find tasks by due date before and owner
    List<Task> findByDueDateBeforeAndOwnerUsername(LocalDate date, String ownerUsername);

    // For admin: find tasks by due date after (all users)
    List<Task> findByDueDateAfter(LocalDate date);

    // For regular users: find tasks by due date after and owner
    List<Task> findByDueDateAfterAndOwnerUsername(LocalDate date, String ownerUsername);

    // For admin: find tasks by status and priority (all users)
    List<Task> findByStatusAndPriority(Status status, Priority priority);

    // For regular users: find tasks by status, priority, and owner
    List<Task> findByStatusAndPriorityAndOwnerUsername(Status status, Priority priority, String ownerUsername);
}