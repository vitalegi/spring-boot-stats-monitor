package it.vitalegi.monitor;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/employees")
	List<String> all(@RequestParam("sleep") int sleep) throws InterruptedException {
		String id = Thread.currentThread().getName() + "_" + UUID.randomUUID();
		logger.info("Start {}", id);
		scheduleFixedDelayTask();
		// Thread.sleep(sleep);
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < sleep)
			;

		logger.info("End   {}", id);
		scheduleFixedDelayTask();
		return Arrays.asList("aaa", "bbb");
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduleFixedDelayTask() {

		boolean printDetails = false;

		List<Thread> threads = Thread.getAllStackTraces().keySet().stream()
				.filter(t -> t.getName().startsWith("http-nio-8080-exec"))
				.sorted((a, b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList());
		for (Thread t : threads) {
			if (printDetails) {
				logger.info("name={}, state={}, priority={}, daemon={}", t.getName(), t.getState(), t.getPriority(),
						t.isDaemon());
			}
		}
	}
}
