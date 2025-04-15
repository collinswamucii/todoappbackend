package com.app.todoapp.services;

import com.app.todoapp.models.Priority;
import com.app.todoapp.models.Status;
import com.app.todoapp.models.Task;
import com.app.todoapp.repository.TasksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TasksRepository tasksRepository;

    // Get the authenticated user's username and role from SecurityContextHolder
    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // Get all tasks (admins see all tasks, users see only their own)
    public List<Task> getAllTasks() {
        if (isAdmin()) {
            return tasksRepository.findAll();
        }
        String username = getAuthenticatedUsername();
        return tasksRepository.findByOwnerUsername(username);
    }

    // Create a new task (admins can specify ownerUsername, users cannot)
    public Task createTask(Task task, String ownerUsername) {
        if (task.getCompleted() == null) {
            task.setCompleted(false); // Default completed status if not provided
        }
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO); // Default status if not provided
        }
        if (isAdmin() && ownerUsername != null) {
            // Admins can specify the ownerUsername
            task.setOwnerUsername(ownerUsername);
        } else {
            // Non-admins can only create tasks for themselves
            task.setOwnerUsername(getAuthenticatedUsername());
        }
        return tasksRepository.save(task);
    }

    // Delete a task by ID
    public void deleteTask(Long id) {
        if (isAdmin()) {
            if (!tasksRepository.existsById(id)) {
                throw new IllegalArgumentException("Task not found with ID: " + id);
            }
        } else {
            String username = getAuthenticatedUsername();
            if (!tasksRepository.existsByIdAndOwnerUsername(id, username)) {
                throw new IllegalArgumentException("Task not found with ID: " + id + " for user: " + username);
            }
        }
        tasksRepository.deleteById(id);
    }

    // Toggle task completion
    public Task toggleTask(Long id) {
        Task task = fetchTask(id);
        // Handle Boolean type, checking for null before toggling
        task.setCompleted(task.getCompleted() == null || !task.getCompleted());
        return tasksRepository.save(task);
    }

    // Get a task by ID
    public Task getTaskById(Long id) {
        return fetchTask(id);
    }

    // Update a task (PUT)
    public Task updateTask(Long id, Task updatedTask) {
        Task existing = fetchTask(id);
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setDueDate(updatedTask.getDueDate());
        existing.setPriority(updatedTask.getPriority());
        existing.setCompleted(updatedTask.getCompleted());
        existing.setStatus(updatedTask.getStatus());
        existing.setAttachmentBase64(updatedTask.getAttachmentBase64());
        if (isAdmin()) {
            // Admins can reassign tasks to other users
            if (updatedTask.getOwnerUsername() != null) {
                existing.setOwnerUsername(updatedTask.getOwnerUsername());
            }
        }
        return tasksRepository.save(existing);
    }

    public List<Task> findByStatus(String status) {
        try {
            Status statusEnum = Status.valueOf(status.toUpperCase());
            if (isAdmin()) {
                return tasksRepository.findByStatus(statusEnum);
            }
            String username = getAuthenticatedUsername();
            return tasksRepository.findByStatusAndOwnerUsername(statusEnum, username);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    public List<Task> findByPriority(String priority) {
        try {
            Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
            if (isAdmin()) {
                return tasksRepository.findByPriority(priorityEnum);
            }
            String username = getAuthenticatedUsername();
            return tasksRepository.findByPriorityAndOwnerUsername(priorityEnum, username);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid priority value: " + priority);
        }
    }

    public List<Task> findByDueDateBefore(LocalDate date) {
        if (isAdmin()) {
            return tasksRepository.findByDueDateBefore(date);
        }
        String username = getAuthenticatedUsername();
        return tasksRepository.findByDueDateBeforeAndOwnerUsername(date, username);
    }

    public List<Task> findByDueDateAfter(LocalDate date) {
        if (isAdmin()) {
            return tasksRepository.findByDueDateAfter(date);
        }
        String username = getAuthenticatedUsername();
        return tasksRepository.findByDueDateAfterAndOwnerUsername(date, username);
    }

    public List<Task> findByStatusAndPriority(String status, String priority) {
        try {
            Status statusEnum = Status.valueOf(status.toUpperCase());
            Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
            if (isAdmin()) {
                return tasksRepository.findByStatusAndPriority(statusEnum, priorityEnum);
            }
            String username = getAuthenticatedUsername();
            return tasksRepository.findByStatusAndPriorityAndOwnerUsername(statusEnum, priorityEnum, username);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status or priority value: status=" + status + ", priority=" + priority);
        }
    }

    // Helper method to fetch a task with role-based access control
    private Task fetchTask(Long id) {
        if (isAdmin()) {
            return tasksRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));
        }
        String username = getAuthenticatedUsername();
        return tasksRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id + " for user: " + username));
    }
}