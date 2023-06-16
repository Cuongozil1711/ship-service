package vn.clmart.manager_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.TokenRefreshException;
import vn.clmart.manager_service.config.security.CustomUserDetailsService;
import vn.clmart.manager_service.config.security.JwtTokenProvider;
import vn.clmart.manager_service.dto.*;
import vn.clmart.manager_service.dto.request.TokenRefreshRequest;
import vn.clmart.manager_service.dto.response.TokenRefreshResponse;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public interface UserService {
    LoginDto authenticateUserHandler(UserLoginDto userLoginDto, HttpServletRequest request);
    Employee createEmployee(EmployeeDto employeeDto, Long cid, String uid);
    Employee updateEmployeee(EmployeeDto employeeDto, Long cid, String uid);
    void deleteEmployee(String uid, Long id);
    EmployeeDto getByUid(String uid);
    UserDTO loginCustomer(String phone, String otp, HttpServletRequest request);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
}
