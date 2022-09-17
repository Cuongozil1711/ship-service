package vn.clmart.manager_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.security.CustomUserDetailsService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@Transactional
public class UserService {
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

    @Autowired
    CustomUserDetailsService customUserDetailsService;



    private static final Logger logger = LogManager.getLogger(UserService.class);

    public LoginDto authenticateUserHandler(UserLoginDto userLoginDto, Long cid,  HttpServletRequest request) {
        LoginDto result = new LoginDto(null, null, null, null);
        try {
            User user = userRepository.findAllByUsernameAndCompanyIdAndDeleteFlg(userLoginDto.getUsername(), cid, Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if(user != null ){
                BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
                boolean checkPass = bc.matches(userLoginDto.getPassword(), user.getPassword());
                if(!checkPass){
                    throw new UsernameNotFoundException("PASSWORD_INCORRECT");
                }
            }
            else{
                throw new UsernameNotFoundException("USER_NOT_FOUND");
            }
            UserDetails userDetails = customUserDetailsService.loadUserByCodeAndCid(cid, user.getUid());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(user);
            logger.info("Login on: " + new Date() + " username: " + userLoginDto.getUsername());
//            Employee employee = employeeRepository.findAllByIdUserAndDeleteFlgAndCompanyId(user.getId(), Constants.DELETE_FLG.NON_DELETE, cid).stream().findFirst().orElse(null);
            result = new LoginDto(jwt, user.getCompanyId(), user.getUid(), userDetails.getAuthorities().toString());
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new BadCredentialsException(ex.getMessage(), ex);
        }
        return result;
    }

    public Employee createEmployee(EmployeeDto employeeDto, Long cid, String uid) {
        try {
            // check account có trùng tài khoản hay không
            User user = userRepository.findAllByUsernameAndCompanyIdAndDeleteFlg(employeeDto.getUserLoginDto().getUsername(), cid, Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if(user != null){
                return null;
            }
            FullName fullName = fullNameRepository.save(FullName.of(employeeDto.getFullNameDto(), cid, uid));
            Address address = addressRepository.save(Address.of(employeeDto.getAddressDto(), cid, uid));
            employeeDto.getUserLoginDto().setPassword(new BCryptPasswordEncoder().encode(employeeDto.getUserLoginDto().getPassword()));
            User user1 = userRepository.save(User.of(employeeDto.getUserLoginDto(), cid, uid));
            Employee employee = new Employee();
            employee.setIdUser(user1.getId());
            employee.setIdAddress(address.getId());
            employee.setIdFullName(fullName.getId());
            employee.setTel(employeeDto.getTel());
            employee.setIdPosition(employeeDto.getIdPosition());
            employee.setCompanyId(cid);
            employee.setCreateBy(uid);
            employeeRepository.save(employee);
            return employee;
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

}
