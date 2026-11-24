
package edu.fscj.cen3024c.taskmanager.services;

import edu.fscj.cen3024c.taskmanager.dto.TaskDTO;
import edu.fscj.cen3024c.taskmanager.entities.Task;
import edu.fscj.cen3024c.taskmanager.exceptions.TaskNotFoundException;
import edu.fscj.cen3024c.taskmanager.repositories.TaskRepository;
import edu.fscj.cen3024c.taskmanager.repositories.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    // Tests findAll()
    @Test
    void findAll_ShouldReturnTaskList() {

        // Given
        Task task1 = new Task();
        task1.setId(1);
        task1.setTitle("Task 1");

        Task task2 = new Task();
        task2.setId(2);
        task2.setTitle("Task 2");

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        var result = taskService.findAll();

        // Then
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Task 2");

        verify(taskRepository, times(1)).findAll();
    }

    // Tests save()
    @Test
    void save_ShouldPersistTaskEntity() {

        // Given
        Task taskToSave = new Task();
        taskToSave.setTitle("New Task");
        taskToSave.setStatus("Pending");

        Task savedTask = new Task();
        savedTask.setId(1);
        savedTask.setTitle("New Task");
        savedTask.setStatus("Pending");

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        Task result = taskService.save(taskToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("New Task");

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // Tests findById()
    @Test
    void findById_ShouldReturnTaskDTO_WhenTaskExists() {

        // Given
        Integer taskId = 1;

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Existing Task");
        task.setStatus("Pending");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When
        TaskDTO result = taskService.findById(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Existing Task");

        verify(taskRepository, times(1)).findById(taskId);
    }

    // Tests findById() when task does not exist
    @Test
    void findById_ShouldThrow_WhenTaskDoesNotExist() {

        // Given
        Integer taskId = 99;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> taskService.findById(taskId))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, times(1)).findById(taskId);
    }

    // Tests deleteById()
    @Test
    void deleteById_ShouldDeleteTask() {

        // Given
        Integer taskId = 1;

        when(taskRepository.existsById(taskId)).thenReturn(true);

        // When
        taskService.deleteById(taskId);

        // Then
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    // Tests deleteById() when task does not exist
    @Test
    void deleteById_ShouldThrow_WhenTaskDoesNotExist() {

        // Given
        Integer taskId = 99;

        when(taskRepository.existsById(taskId)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> taskService.deleteById(taskId))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).deleteById(taskId);
    }
}
