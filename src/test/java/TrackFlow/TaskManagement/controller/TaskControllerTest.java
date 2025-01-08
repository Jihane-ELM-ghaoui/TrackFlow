//package TrackFlow.TaskManagement.controller;
//
//import TrackFlow.TaskManagement.model.Task;
//import TrackFlow.TaskManagement.service.TaskService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class TaskControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private TaskService taskService;
//
//    @InjectMocks
//    private TaskController taskController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
//    }
//
//    @Test
//    void testCreateTask() throws Exception {
//        Task task = new Task();
//        task.setTaskId(1L);
//        task.setTaskName("Test Task");
//        task.setTaskDescription("Test Description");
//
//        when(taskService.createTask(any(Task.class))).thenReturn(task);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"taskName\":\"Test Task\",\"taskDescription\":\"Test Description\"}"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Test Task"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskDescription").value("Test Description"));
//
//        verify(taskService, times(1)).createTask(any(Task.class));
//    }
//
//    @Test
//    void testUpdateTask() throws Exception {
//        Task task = new Task();
//        task.setTaskId(1L);
//        task.setTaskName("Updated Task");
//        task.setTaskDescription("Updated Description");
//
//        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(task);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"taskName\":\"Updated Task\",\"taskDescription\":\"Updated Description\"}"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Updated Task"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskDescription").value("Updated Description"));
//
//        verify(taskService, times(1)).updateTask(eq(1L), any(Task.class));
//    }
//
//    @Test
//    void testDeleteTask() throws Exception {
//        doNothing().when(taskService).deleteTask(anyLong());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/1"))
//                .andExpect(status().isNoContent());
//
//        verify(taskService, times(1)).deleteTask(eq(1L));
//    }
//
//    @Test
//    void testGetTaskById() throws Exception {
//        Task task = new Task();
//        task.setTaskId(1L);
//        task.setTaskName("Test Task");
//
//        when(taskService.getTaskById(anyLong())).thenReturn(task);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Test Task"));
//
//        verify(taskService, times(1)).getTaskById(eq(1L));
//    }
//
//    @Test
//    void testGetTaskByIdNotFound() throws Exception {
//        when(taskService.getTaskById(anyLong())).thenReturn(null);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/999"))
//                .andExpect(status().isNotFound());
//
//        verify(taskService, times(1)).getTaskById(eq(999L));
//    }
//
//    @Test
//    void testGetTasksByProjectId() throws Exception {
//        Task task1 = new Task();
//        task1.setTaskId(1L);
//        task1.setTaskName("Task 1");
//
//        Task task2 = new Task();
//        task2.setTaskId(2L);
//        task2.setTaskName("Task 2");
//
//        when(taskService.getTasksByProjectId(anyLong())).thenReturn(List.of(task1, task2));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/project/1"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].taskName").value("Task 1"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[1].taskName").value("Task 2"));
//
//        verify(taskService, times(1)).getTasksByProjectId(eq(1L));
//    }
//
//    @Test
//    void testCountTasksByUserId() throws Exception {
//        Map<String, Object> response = Map.of(
//                "userId", "user1",
//                "allTasks", 10,
//                "completedTasks", 5,
//                "incompleteTasks", 5
//        );
//
//        when(taskService.countAllTasksByUserId(anyString())).thenReturn(response);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/users")
//                        .param("userId", "user1"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user1"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.allTasks").value(10))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.completedTasks").value(5));
//
//        verify(taskService, times(1)).countAllTasksByUserId(eq("user1"));
//    }
//
//    @Test
//    void testCountParameters() throws Exception {
//        Map<String, Object> response = Map.of(
//                "projectId", 1L,
//                "tasksByProjectId", 5,
//                "onTimeTasks", 3,
//                "completedTaskByProjectId", 4
//        );
//
//        when(taskService.countParameters(anyLong())).thenReturn(response);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/project")
//                        .param("projectId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.projectId").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.tasksByProjectId").value(5))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.onTimeTasks").value(3));
//
//        verify(taskService, times(1)).countParameters(eq(1L));
//    }
//}
