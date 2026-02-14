package intellijP.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// nécessaire pour que les @Async dans SimulationService marchent
@Configuration
@EnableAsync
public class AsyncConfig {
}
