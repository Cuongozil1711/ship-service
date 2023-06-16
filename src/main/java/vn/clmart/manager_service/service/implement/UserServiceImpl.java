package vn.clmart.manager_service.service.implement;

import lombok.extern.log4j.Log4j2;
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
import vn.clmart.manager_service.service.AddressService;
import vn.clmart.manager_service.service.PositionService;
import vn.clmart.manager_service.service.RefreshTokenService;
import vn.clmart.manager_service.service.UserService;
import vn.clmart.manager_service.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class UserServiceImpl implements UserService{

    private final UserRepo userRepository;
    private final JwtTokenProvider tokenProvider;
    private final FullNameRepo fullNameRepo;
    private final AddressRepo addressRepo;
    private final EmployeeRepo employeeRepo;
    private final CustomUserDetailsService customUserDetailsService;
    private final PositionService positionService;
    private final AddressService addressService;
    private final TokenFireBaseRepo tokenFireBaseRepository;
    private final UserOTPRepo userOTPRepo;
    private final RefreshTokenService refreshTokenService;

    private static final Logger logger = LogManager.getLogger(UserService.class);
    public UserServiceImpl(UserRepo userRepository, JwtTokenProvider tokenProvider, FullNameRepo fullNameRepo, AddressRepo addressRepo, EmployeeRepo employeeRepo, CustomUserDetailsService customUserDetailsService, PositionService positionService, AddressService addressService, TokenFireBaseRepo tokenFireBaseRepository, UserOTPRepo userOTPRepo, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.fullNameRepo = fullNameRepo;
        this.addressRepo = addressRepo;
        this.employeeRepo = employeeRepo;
        this.customUserDetailsService = customUserDetailsService;
        this.positionService = positionService;
        this.addressService = addressService;
        this.tokenFireBaseRepository = tokenFireBaseRepository;
        this.userOTPRepo = userOTPRepo;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginDto authenticateUserHandler(UserLoginDto userLoginDto, HttpServletRequest request) {
        LoginDto result = new LoginDto(null, null, null, null);
        try {
            User user = userRepository.findAllByUsernameAndDeleteFlg(userLoginDto.getUsername(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if (user != null) {
                Employee employee = employeeRepo.findAllByIdUserAndDeleteFlg(user.getId(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
                if (employee != null) {
                    BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
                    boolean checkPass = bc.matches(userLoginDto.getPassword(), user.getPassword());
                    if (!checkPass) {
                        throw new UsernameNotFoundException("PASSWORD_INCORRECT");
                    }
                } else {
                    throw new UsernameNotFoundException("USER_NOT_FOUND");
                }
            } else {
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
            result = new LoginDto(jwt, user.getUid(), userDetails.getAuthorities().stream().collect(Collectors.toList()).get(0).toString(), fullName.getFirstName() + " " + fullName.getLastName());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUid());

            result.setName(fullName.getFirstName() + " " + fullName.getLastName());
            result.setImage(employee.getImage());
            result.setBirthDay(employee.getBirthDay());
            result.setRefreshToken(refreshToken.getToken());
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new BadCredentialsException(ex.getMessage(), ex);
        }
        return result;
    }

    @Override
    public Employee createEmployee(EmployeeDto employeeDto, Long cid, String uid) {
        try {
            cid = employeeDto.getCid();
            // check account có trùng tài khoản hay không
            User user = userRepository.findAllByUsernameAndDeleteFlg(employeeDto.getUserLoginDto().getUsername(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if (user != null) {
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

    @Override
    public Employee updateEmployeee(EmployeeDto employeeDto, Long cid, String uid) {
        Employee employee = employeeRepo.findById(employeeDto.getId()).orElse(null);
        if (employee != null) {
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

    @Override
    public void deleteEmployee(String uid, Long id) {
        try {
            Employee employee = employeeRepo.findById(id).orElse(null);
            if (employee != null) {
                employee.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                employee.setUpdateBy(uid);
                employeeRepo.save(employee);
                User user = userRepository.findByUidAndDeleteFlg(employee.getIdUser(), Constants.DELETE_FLG.NON_DELETE).orElse(null);
                if (user != null) {
                    user.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                    employee.setUpdateBy(uid);
                    userRepository.save(user);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public EmployeeDto getByUid(String uid) {
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
        if (employee.getIdAddress() != null) {
            AddressDto addressDto = new AddressDto();
            Address address = addressRepo.findById(employee.getIdAddress()).orElse(new Address());
            BeanUtils.copyProperties(address, addressDto);
            employeeResponseDTO.setAddressDto(addressDto);
        }
        return employeeResponseDTO;
    }

    @Override
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
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUid(), user.getUid(), enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(user);
        userDTO.setAccessToken(jwt);

        return userDTO;
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    String token = tokenProvider.generateTokenFromUserId(userId);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!")).getBody();
    }
}
