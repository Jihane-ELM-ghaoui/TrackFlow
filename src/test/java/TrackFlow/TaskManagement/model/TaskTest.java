package TrackFlow.TaskManagement.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testTaskConstructorAndGettersSetters() {
        Task task = new Task();

        task.setTaskId(1L);
        task.setTaskName("Test Task");
        task.setTaskDescription("Task Description");
        task.setTaskPriority(1);
        task.setProjectId(100L);
        task.setAssignedUsers(Arrays.asList("user1", "user2"));
        task.setTaskStartDate(new Date());
        task.setTaskEndDate(new Date());
        task.setCreatedBy("admin");
        task.setStatus(Task.Status.NOT_STARTED);
        task.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, task.getTaskId());
        assertEquals("Test Task", task.getTaskName());
        assertEquals("Task Description", task.getTaskDescription());
        assertEquals(1, task.getTaskPriority());
        assertEquals(100L, task.getProjectId());
        assertTrue(task.getAssignedUsers().contains("user1"));
        assertNotNull(task.getTaskStartDate());
        assertNotNull(task.getTaskEndDate());
        assertEquals("admin", task.getCreatedBy());
        assertEquals(Task.Status.NOT_STARTED, task.getStatus());
        assertNotNull(task.getCreatedAt());
    }
    
}
