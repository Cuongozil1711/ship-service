package vn.clmart.manager_service.config.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.service.UserService;
import java.io.IOException;


@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    TokenFireBaseRepo tokenFireBaseRepository;

    @Autowired
    OrderRepo orderRepositorry;

    @Autowired
    UserService userService;

    @Scheduled(cron = "00 15 22 ? * *")
    public void scheduleFixedDelayTask() throws IOException {
        System.out.println(
                "Fixed delay task - " + System.currentTimeMillis() / 1000);
    }

}
