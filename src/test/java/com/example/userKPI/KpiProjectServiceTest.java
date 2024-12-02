package com.example.userKPI;

import com.example.userKPI.DTO.KpiProjectResponse;
import com.example.userKPI.repo.TaskRepo;
import com.example.userKPI.service.KpiProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KpiProjectServiceTest {

    private final TaskRepo taskRepo = Mockito.mock(TaskRepo.class);
    private final SimpMessagingTemplate messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
    private final KpiProjectService service = new KpiProjectService(taskRepo, messagingTemplate);

    @Test
    void testCalculateOnTimeCompletionRate() {
        Long projectId = 1L;
        when(taskRepo.countCompletedTasksByProjectId(projectId)).thenReturn(10L);
        when(taskRepo.countOnTimeTasksByProjectId(projectId)).thenReturn(7L);

        double rate = service.calculateOnTimeCompletionRate(projectId);

        assertEquals(70.0, rate, 0.01);
        verify(taskRepo).countCompletedTasksByProjectId(projectId);
        verify(taskRepo).countOnTimeTasksByProjectId(projectId);
    }

    @Test
    void testFindCycleTimesForCompletedTasks() {
        Long projectId = 1L;
        List<Object[]> mockResults = Arrays.asList(
                new Object[]{1L, 5L},
                new Object[]{2L, 8L}
        );
        when(taskRepo.findTaskIdAndCycleTimeByProjectId(projectId)).thenReturn(mockResults);

        List<KpiProjectResponse.TaskCycleTime> cycleTimes = service.findCycleTimesForCompletedTasks(projectId);

        assertEquals(2, cycleTimes.size());
        assertEquals(1L, cycleTimes.get(0).getTaskId());
        assertEquals(5L, cycleTimes.get(0).getCycleTime());
    }
}

