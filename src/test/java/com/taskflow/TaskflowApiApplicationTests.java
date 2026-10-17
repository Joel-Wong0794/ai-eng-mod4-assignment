package com.taskflow;

import com.taskflow.entity.Task;
import com.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "TASKFLOW_FRONTEND_ORIGIN=https://taskflow-demo.netlify.app")
@AutoConfigureMockMvc
class TaskflowApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private CommandLineRunner seedDemoTasks;

	@Test
	void contextLoads() {
		assertThat(taskRepository.findAll())
				.extracting(Task::getTitle)
				.containsExactlyInAnyOrder("Buy groceries", "Pay electricity bill", "Do the laundry");
		assertThat(taskRepository.findAll())
				.extracting(Task::getCompleted)
				.contains(false, true);
	}

	@Test
	void runningSeedAgainDoesNotDuplicateTasks() throws Exception {
		seedDemoTasks.run();
		assertThat(taskRepository.count()).isEqualTo(3);
	}

	@Test
	@Transactional
	void seedLeavesExistingTasksUnchanged() throws Exception {
		taskRepository.deleteAll();
		Task existing = taskRepository.save(new Task(null, "My own task", "Keep this task",
				"LOW", null, false));

		seedDemoTasks.run();

		assertThat(taskRepository.findAll())
				.extracting(Task::getId)
				.containsExactly(existing.getId());
	}

	@Test
	void allowsConfiguredFrontendToReadTasks() throws Exception {
		mockMvc.perform(get("/api/tasks")
				.header("Origin", "https://taskflow-demo.netlify.app"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-demo.netlify.app"));
	}

	@Test
	void allowsJsonPostPreflightFromConfiguredFrontend() throws Exception {
		mockMvc.perform(options("/api/tasks")
				.header("Origin", "https://taskflow-demo.netlify.app")
				.header("Access-Control-Request-Method", "POST")
				.header("Access-Control-Request-Headers", "content-type"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-demo.netlify.app"))
				.andExpect(header().string("Access-Control-Allow-Methods", "GET,POST"))
				.andExpect(header().string("Access-Control-Allow-Headers", "content-type"));
	}

	@Test
	void rejectsUnconfiguredOrigin() throws Exception {
		mockMvc.perform(options("/api/tasks")
				.header("Origin", "https://untrusted.example")
				.header("Access-Control-Request-Method", "POST"))
				.andExpect(status().isForbidden())
				.andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
	}
}
