package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.CustomerDto;
import vn.clmart.manager_service.model.Customer;
import vn.clmart.manager_service.repository.CustomerRepo;
import vn.clmart.manager_service.utils.Constants;

@Service
@Transactional
public class CustomerService {

    @Autowired
    CustomerRepo customerRepo;

    public Customer create(CustomerDto CustomerDto, Long cid, String uid){
        try {
            Customer customer = Customer.of(CustomerDto, cid, uid);
            return customerRepo.save(customer);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Customer update(CustomerDto CustomerDto, Long cid, String uid, Long id){
        try {
            Customer customer = customerRepo.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            customer.setUpdateBy(uid);
            customer.setAddress(CustomerDto.getAddress());
            customer.setName(CustomerDto.getName());
            customer.setTel(CustomerDto.getTel());
            return customerRepo.save(customer);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Customer getById(Long cid, String uid, Long id){
        try {
            return customerRepo.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Customer> search(Long cid, Pageable pageable){
        try {
            Page<Customer> pageSearch = customerRepo.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Customer delete(Long cid, String uid, Long id){
        try {
            Customer customer = customerRepo.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            customer.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            customer.setUpdateBy(uid);
            return customerRepo.save(customer);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
