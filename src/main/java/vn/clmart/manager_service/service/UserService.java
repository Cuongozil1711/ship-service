package vn.clmart.manager_service.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import vn.clmart.manager_service.config.security.CustomUserDetailsService;
import vn.clmart.manager_service.config.security.JwtTokenProvider;
import vn.clmart.manager_service.dto.*;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.utils.Constants;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepo userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    FullNameRepo fullNameRepo;

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    PositionService positionService;

    @Autowired
    AddressService addressService;

    @Autowired
    TokenFireBaseRepo tokenFireBaseRepository;

    @Autowired
    UserOTPRepo userOTPRepo;

    private static final Logger logger = LogManager.getLogger(UserService.class);

    public LoginDto authenticateUserHandler(UserLoginDto userLoginDto,  HttpServletRequest request) {
        LoginDto result = new LoginDto(null, null, null, null);
        try {
            User user = userRepository.findAllByUsernameAndDeleteFlg(userLoginDto.getUsername(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if(user != null ){
                Employee employee = employeeRepo.findAllByIdUserAndDeleteFlg(user.getId(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
                if(employee != null){
                    BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
                    boolean checkPass = bc.matches(userLoginDto.getPassword(), user.getPassword());
                    if(!checkPass){
                        throw new UsernameNotFoundException("PASSWORD_INCORRECT");
                    }
                }
                else{
                    throw new UsernameNotFoundException("USER_NOT_FOUND");
                }
            }
            else{
                throw new UsernameNotFoundException("USER_NOT_FOUND");
            }
            UserDetails userDetails = customUserDetailsService.loadUserByCodeAndCid(user.getUid());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(user);
            logger.info("Login on: " + new Date() + " username: " + userLoginDto.getUsername());
            Employee employee = employeeRepo.findAllByIdUserAndDeleteFlg(user.getId(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            FullName fullName = fullNameRepo.findById(employee.getIdFullName()).orElse(new FullName());
            TokenFireBase tokenFireBase = tokenFireBaseRepository.findByDeleteFlgAndUserId(Constants.DELETE_FLG.NON_DELETE, user.getUid()).orElse(null);
            if(tokenFireBase != null && userLoginDto.getTokenFirebase() != null){
                tokenFireBase.setToken(userLoginDto.getTokenFirebase());
                tokenFireBaseRepository.save(tokenFireBase);
            }
            else if(userLoginDto.getTokenFirebase() != null){
                TokenFireBase tokenFireBaseOnline = new TokenFireBase();
                tokenFireBaseOnline.setUserId(user.getUid());
                tokenFireBaseOnline.setToken(userLoginDto.getTokenFirebase());
                tokenFireBaseRepository.save(tokenFireBaseOnline);
            }
            result = new LoginDto(jwt, user.getUid(), userDetails.getAuthorities().stream().collect(Collectors.toList()).get(0).toString(), fullName.getFirstName() + " " + fullName.getLastName());
            result.setName(fullName.getFirstName() + " " + fullName.getLastName());
            result.setImage(employee.getImage());
            result.setBirthDay(employee.getBirthDay());
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
            cid = employeeDto.getCid();
            // check account có trùng tài khoản hay không
            User user = userRepository.findAllByUsernameAndDeleteFlg(employeeDto.getUserLoginDto().getUsername(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if(user != null){
                return null;
            }
            FullName fullName = fullNameRepo.save(FullName.of(employeeDto.getFullNameDto(), cid, uid));
            Address address = addressRepo.save(Address.of(employeeDto.getAddressDto(), cid, uid));
            employeeDto.getUserLoginDto().setPassword(new BCryptPasswordEncoder().encode(employeeDto.getUserLoginDto().getPassword()));
            User user1 = userRepository.save(User.of(employeeDto.getUserLoginDto(), uid));
            Employee employee = new Employee();
            employee.setIdUser(user1.getId());
            employee.setIdAddress(address.getId());
            employee.setIdFullName(fullName.getId());
            employee.setTel(employeeDto.getTel());
            employee.setIdPosition(employeeDto.getIdPosition());
            employee.setCreateBy(uid);
            employee.setCode(employeeDto.getCmt());
            employee.setBirthDay(employeeDto.getBirthDay());
            employee = employeeRepo.save(employee);
            return employee;
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public Employee updateEmployeee(EmployeeDto employeeDto, Long cid, String uid){
        Employee employee = employeeRepo.findById(employeeDto.getId()).orElse(null);
        if(employee != null){
            FullName fullName = fullNameRepo.findById(employee.getIdFullName()).orElseThrow();
            fullName.setFirstName(employeeDto.getFullNameDto().getFirstName());
            fullName.setLastName(employeeDto.getFullNameDto().getLastName());
            fullNameRepo.save(fullName);

            Address address = addressRepo.findById(employee.getIdAddress()).orElse(new Address());
            address.setProvinceId(employeeDto.getAddressDto().getProvinceId());
            address.setDistrictId(employeeDto.getAddressDto().getDistrictId());
            address.setWardId(employeeDto.getAddressDto().getWardId());
            address.setName(employeeDto.getAddressDto().getName());
            addressRepo.save(address);

//            User user = userRepository.findUserByUidAndDeleteFlg(employee.getIdUser(), Constants.DELETE_FLG.NON_DELETE).orElse(null);
//            if(user != null)
//            employeeDto.setUserLoginDto(new UserLoginDto(user.getUsername(), "", ""));

            employee.setIdAddress(address.getId());
            employee.setIdFullName(fullName.getId());
            employee.setTel(employeeDto.getTel());
            employee.setIdPosition(employeeDto.getIdPosition());
            employee.setCreateBy(uid);
            employee.setCode(employeeDto.getCmt());
            employee.setTel(employeeDto.getTel());
            employee.setBirthDay(employeeDto.getBirthDay());
            employee.setCode(employeeDto.getCmt());
            employeeRepo.save(employee);
        }
        return employee;
    }

    public void deleteEmployee(String uid, Long id){
        try {
            Employee employee = employeeRepo.findById(id).orElse(null);
            if(employee != null){
                employee.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                employee.setUpdateBy(uid);
                employeeRepo.save(employee);
                User user = userRepository.findByUidAndDeleteFlg(employee.getIdUser(), Constants.DELETE_FLG.NON_DELETE).orElse(null);
                if(user != null){
                    user.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                    employee.setUpdateBy(uid);
                    userRepository.save(user);
                }
            }
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public EmployeeDto getByUid(String uid){
        Employee employee = employeeRepo.findByIdUser(uid).orElseThrow(() -> new RuntimeException());
        EmployeeDto employeeResponseDTO = new EmployeeDto();
        employeeResponseDTO.setId(employee.getId());
        employeeResponseDTO.setIdUser(employee.getIdUser());
        FullName fullName = fullNameRepo.findById(employee.getIdFullName()).orElse(new FullName());
        employeeResponseDTO.setFullNameDto(new FullNameDto(fullName.getFirstName(), fullName.getLastName()));
        employeeResponseDTO.setCmt(employee.getCode());
        employeeResponseDTO.setTel(employee.getTel());
        Position position = positionService.getById(employee.getIdPosition());
        employeeResponseDTO.setIdPosition(position.getId());
        employeeResponseDTO.setNamePosition(position.getName());
        employeeResponseDTO.setStatus(employee.getDeleteFlg());
        employeeResponseDTO.setBirthDay(employee.getBirthDay());
        employeeResponseDTO.setImage(employee.getImage());
        if(employee.getIdAddress() != null){
            AddressDto addressDto = new AddressDto();
            Address address = addressRepo.findById(employee.getIdAddress()).orElse(new Address());
            BeanUtils.copyProperties(address, addressDto);
            employeeResponseDTO.setAddressDto(addressDto);
        }
        return employeeResponseDTO;
    }

    public UserDTO loginCustomer(String phone, String otp, HttpServletRequest request) {
        User user = userRepository.findByPhoneAndDeleteFlg(phone, Constants.DELETE_FLG.NON_DELETE).orElse(null);

        if (user == null) {
            User userNew = new User();
            userNew.setPhone(phone);
            userNew.setUsername(phone);
            userNew.setRole(Constants.TYPE_ROLE.CUSTOMER.name());
            user = userRepository.save(userNew);
        }

        UserOTP userOTP = new UserOTP();
        userOTP.setOtp(otp);
        userOTP.setUserId(user.getId());
        userOTPRepo.save(userOTP);

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setUserId(user.getId());

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails =  new org.springframework.security.core.userdetails.User(user.getUid(), user.getUid(), enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(user);
        userDTO.setAccessToken(jwt);

        return userDTO;
    }
}
