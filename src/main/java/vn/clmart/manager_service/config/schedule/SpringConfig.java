package vn.clmart.manager_service.config.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import vn.clmart.manager_service.dto.TokenFirseBaseDTO;
import vn.clmart.manager_service.model.TokenFireBase;
import vn.clmart.manager_service.repository.ExportWareHouseRepository;
import vn.clmart.manager_service.repository.ImportWareHouseRepository;
import vn.clmart.manager_service.repository.OrderRepositorry;
import vn.clmart.manager_service.repository.TokenFireBaseRepository;
import vn.clmart.manager_service.service.NotificationService;
import vn.clmart.manager_service.untils.Constants;

import java.io.IOException;
import java.util.*;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenFireBaseRepository tokenFireBaseRepository;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    ImportWareHouseRepository importWareHouseRepository;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Scheduled(cron = "00 15 22 ? * *")
    public void scheduleFixedDelayTask() throws IOException {
        System.out.println(
                "Fixed delay task - " + System.currentTimeMillis() / 1000);

        // gui thong bao cho quan ly chi nhanh
        TokenFirseBaseDTO tokenFirseBaseDTO = new TokenFirseBaseDTO();
        tokenFirseBaseDTO.setPriority("high");
        String[] token = new String[10];
        Map<String, String> notification = new HashMap<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        Integer sumOrder = orderRepositorry.getCountByDate(137l, Constants.DELETE_FLG.NON_DELETE, calendar.getTime());
        Integer sumImport = importWareHouseRepository.getCountByDate(137l, Constants.DELETE_FLG.NON_DELETE, calendar.getTime());
        Integer sumExport = exportWareHouseRepository.getCountByDate(137l, Constants.DELETE_FLG.NON_DELETE, calendar.getTime());
        notification.put("body",  calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) + ": " + sumOrder + " đơn hàng " +
                sumImport + " nhập " + sumExport + " xuất");
        notification.put("title", "Thông báo");
        tokenFirseBaseDTO.setNotification(notification);
        TokenFireBase tokenFireBase = tokenFireBaseRepository.findByDeleteFlgAndUserId(Constants.DELETE_FLG.NON_DELETE, "4f749b41-7c02-41c0-9a16-4786197a18ef").orElse(null);
        if(tokenFireBase != null){
            token[0] = tokenFireBase.getToken();
        }
        tokenFirseBaseDTO.setRegistration_ids(token);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(tokenFirseBaseDTO);

        notificationService.sendNotificationToUser(json);
    }

}
