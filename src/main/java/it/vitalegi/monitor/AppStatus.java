package it.vitalegi.monitor;

import java.lang.Thread.State;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppStatus {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${server.tomcat.threads.min-spare}")
	int min;
	@Value("${server.tomcat.threads.max}")
	int max;

	@Scheduled(fixedDelay = 1000)
	public void threadsReport() {
		Map<String, List<State>> stats = Thread.getAllStackTraces().keySet().stream()
				.filter(t -> t.getName().startsWith("http-nio-8080-exec")) //
				.map(Thread::getState)//
				.collect(Collectors.groupingBy(State::name));

		logger.info("THREADS: min={}, max={}, {}", min, max, Arrays.stream(State.values()).map(state -> {
			int count = stats.getOrDefault(state.name(), new ArrayList<>()).size();
			return state + "=" + count;
		}).collect(Collectors.joining(", ")));
	}

	@Scheduled(fixedDelay = 1000)
	public void ramReport() {
		Runtime rt = Runtime.getRuntime();
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		long used = total - free;
		logger.info("RAM: total_byte={}, free_byte={}, used_byte={}, total_mb={}, free_mb={}, used_mb={}", total, free,
				used, asMegaByte(total), asMegaByte(free), asMegaByte(used));
	}

	protected String asMegaByte(long bytes) {
		BigDecimal value = BigDecimal.valueOf(bytes).divide(BigDecimal.valueOf(1024 * 1024));
		return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
}
