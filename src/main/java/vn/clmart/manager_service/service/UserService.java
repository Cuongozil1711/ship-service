package vn.clmart.manager_service.service;

import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.config.security.JwtTokenProvider;
import vn.clmart.manager_service.dto.EmployeeDto;
import vn.clmart.manager_service.dto.LoginDto;
import vn.clmart.manager_service.dto.UserLoginDto;
import vn.clmart.manager_service.model.Address;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.FullName;
import vn.clmart.manager_service.model.User;
import vn.clmart.manager_service.repository.AddressRepository;
import vn.clmart.manager_service.repository.EmployeeRepository;
import vn.clmart.manager_service.repository.FullNameRepository;
import vn.clmart.manager_service.repository.UserRepository;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.ResponseAPI;

import java.util.Date;

@Service
@Log4j
public class UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    FullNameRepository fullNameRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    EmployeeRepository employeeRepository;



    private static final Logger logger = LogManager.getLogger(UserService.class);

    public LoginDto authenticateUserHandler(UserLoginDto userLoginDto, Long cid) {
        LoginDto result = new LoginDto(null, null);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
            // Nếu không xảy ra exception tức là thông tin hợp lệ
            // Set thông tin authentication vào Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            org.springframework.security.core.userdetails.User authenUser = (org.springframework.security.core.userdetails.User) authentication .getPrincipal();
            User user = userRepository.findUserByUsernameAndCompanyIdAndDeleteFlg(authenUser.getUsername(), cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            // Trả về jwt cho người dùng.
            String jwt = tokenProvider.generateToken(user);
            logger.info("Login on: " + new Date() + " username: " + userLoginDto.getUsername());
            result = new LoginDto(jwt, user.getCompanyId());
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new BadCredentialsException(ex.getMessage(), ex);
        }
        return result;
    }

    public ResponseAPI createEmployee(EmployeeDto employeeDto, Long cid, String uid) {
        LoginDto result = new LoginDto(null, null);
        try {
            // check account có trùng tài khoản hay không
            User user = userRepository.findUserByUsernameAndCompanyIdAndDeleteFlg(employeeDto.getUserLoginDto().getUsername(), cid, Constants.DELETE_FLG.NON_DELETE).get();
            if(user != null){
                return ResponseAPI.handlerError("Tài khoản đã bị trùng", HttpStatus.BAD_REQUEST.value());
            }
            FullName fullName = fullNameRepository.save(FullName.of(employeeDto.getFullNameDto(), cid, uid));
            Address address = addressRepository.save(Address.of(employeeDto.getAddressDto(), cid, uid));
            User user1 = userRepository.save(User.of(employeeDto.getUserLoginDto(), cid, uid));
            Employee employee = new Employee();
            employee.setIdUser(user1.getId());
            employee.setIdAddress(address.getId());
            employee.setIdFullName(fullName.getId());
            employee.setTel(employeeDto.getTel());
            employee.setIdPosition(employeeDto.getIdPosition());
            employeeRepository.save(employee);
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new BadCredentialsException(ex.getMessage(), ex);
        }
        return ResponseAPI.handlerSuccess("");
    }

}
