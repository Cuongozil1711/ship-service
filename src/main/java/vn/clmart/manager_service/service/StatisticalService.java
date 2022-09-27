package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.request.StatisticalResponseDTO;
import vn.clmart.manager_service.repository.CustomerRepository;
import vn.clmart.manager_service.repository.OrderRepositorry;
import vn.clmart.manager_service.untils.Constants;

import java.util.*;
@Service
@Transactional
public class StatisticalService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    OrderService orderService;

    public Map<String, StatisticalResponseDTO> statistical(Long cid, String uid){
        try {
            Map<String, StatisticalResponseDTO> statisticaList = new HashMap<>();
            // thống kê khách hàng
            StatisticalResponseDTO statisticalCustomer = new StatisticalResponseDTO();
            statisticalCustomer.setSumQuality(customerRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE).size());
            statisticalCustomer.setSumNow(customerRepository.getCount(cid, Constants.DELETE_FLG.NON_DELETE));
            statisticaList.put("statisticalCustomer", statisticalCustomer);
            // thống kê đơn hàng
            StatisticalResponseDTO statisticalOrder = new StatisticalResponseDTO();
            statisticalOrder.setSumQuality(orderRepositorry.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE).size());
            statisticalOrder.setSumNow(orderRepositorry.getCount(cid, Constants.DELETE_FLG.NON_DELETE));
            statisticaList.put("statisticalOrder", statisticalOrder);
            // thống kê doanh thu Revenue
            StatisticalResponseDTO statisticalRevenue = new StatisticalResponseDTO();
            statisticalRevenue.setSumPriceNow(orderService.getRevenueNow(cid, uid));
            statisticalRevenue.setSumPriceAfter(orderService.getRevenueAfter(cid, uid));
            statisticaList.put("statisticalRevenue", statisticalRevenue);
            return statisticaList;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
