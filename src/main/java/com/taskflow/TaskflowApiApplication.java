package com.taskflow;

import com.taskflow.entity.Task;
import com.taskflow.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class TaskflowApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskflowApiApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDemoTasks(TaskRepository taskRepository) {
		return args -> {
			if (taskRepository.count() != 0) {
				return;
			}

			LocalDate today = LocalDate.now();
			taskRepository.saveAll(List.of(
					new Task(null, "Buy groceries", "Pick up eggs, milk, bread, and vegetables",
							"MEDIUM", today.plusDays(1), false),
					new Task(null, "Pay electricity bill", "Check the bill and make payment",
							"HIGH", today.plusDays(2), false),
					new Task(null, "Do the laundry", "Wash clothes, hang them to dry, and fold them",
							"LOW", today, true)));
		};
	}

}
