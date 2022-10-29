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
import vn.clmart.manager_service.dto.request.EmployeeResponseDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.AddressRepository;
import vn.clmart.manager_service.repository.EmployeeRepository;
import vn.clmart.manager_service.repository.FullNameRepository;
import vn.clmart.manager_service.repository.UserRepository;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.ResponseAPI;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    PositionService positionService;

    @Autowired
    AddressService addressService;

    private static final Logger logger = LogManager.getLogger(UserService.class);

    public LoginDto authenticateUserHandler(UserLoginDto userLoginDto, Long cid,  HttpServletRequest request) {
        LoginDto result = new LoginDto(null, null, null, null, null);
        try {
            User user = userRepository.findAllByUsernameAndCompanyIdAndDeleteFlg(userLoginDto.getUsername(), cid, Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if(user != null ){
                Employee employee = employeeRepository.findAllByIdUserAndDeleteFlgAndCompanyId(user.getId(), Constants.DELETE_FLG.NON_DELETE, cid).stream().findFirst().orElse(null);
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
            UserDetails userDetails = customUserDetailsService.loadUserByCodeAndCid(cid, user.getUid());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(user);
            logger.info("Login on: " + new Date() + " username: " + userLoginDto.getUsername());
            Employee employee = employeeRepository.findAllByIdUserAndDeleteFlgAndCompanyId(user.getId(), Constants.DELETE_FLG.NON_DELETE, cid).stream().findFirst().orElse(null);
            FullName fullName = fullNameRepository.findById(employee.getIdFullName()).orElse(new FullName());
            result = new LoginDto(jwt, user.getCompanyId(), user.getUid(), userDetails.getAuthorities().stream().collect(Collectors.toList()).get(0).toString(), fullName.getFirstName() + " " + fullName.getLastName());
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
            employee.setCode(employeeDto.getCmt());
            employee.setBirthDay(employeeDto.getBirthDay());
            employee = employeeRepository.save(employee);
            return employee;
        } catch (BadCredentialsException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException(e.getMessage(), e);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<EmployeeDto> search(Long cid,  Pageable pageable){
        try {
            Page<Employee> pageSearch = employeeRepository.findAllByCompanyId(cid, pageable);
            List<EmployeeDto> responseDTOList = new ArrayList<>();
            for(Employee employee : pageSearch.getContent()){
                EmployeeDto employeeResponseDTO = new EmployeeDto();
                employeeResponseDTO.setId(employee.getId());
                employeeResponseDTO.setIdUser(employee.getIdUser());
                FullName fullName = fullNameRepository.findById(employee.getIdFullName()).orElse(new FullName());
                employeeResponseDTO.setFullNameDto(new FullNameDto(fullName.getFirstName(), fullName.getLastName()));
                employeeResponseDTO.setCmt(employee.getCode());
                employeeResponseDTO.setTel(employee.getTel());
                Position position = positionService.getById(cid, "", employee.getIdPosition());
                employeeResponseDTO.setIdPosition(position.getId());
                employeeResponseDTO.setNamePosition(position.getName());
                employeeResponseDTO.setStatus(employee.getDeleteFlg());
                employeeResponseDTO.setBirthDay(employee.getBirthDay());
                employeeResponseDTO.setImage(employee.getImage());
                if(employee.getIdAddress() != null){
                    AddressDto addressDto = new AddressDto();
                    Address address = addressRepository.findById(employee.getIdAddress()).orElse(new Address());
                    BeanUtils.copyProperties(address, addressDto);
                    employeeResponseDTO.setAddressDto(addressDto);
                }
                responseDTOList.add(employeeResponseDTO);
            }
            return new PageImpl(responseDTOList, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void deleteEmployee(Long cid, String uid, Long id){
        try {
            Employee employee = employeeRepository.findByCompanyIdAndId(cid, id).orElse(null);
            if(employee != null){
                employee.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                employee.setUpdateBy(uid);
                employeeRepository.save(employee);
                User user = userRepository.findByCompanyIdAndUidAndDeleteFlg(cid, employee.getIdUser(), Constants.DELETE_FLG.NON_DELETE).orElse(null);
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

    public FullName getFullName(Long cid, String uid){
        User user = userRepository.findUserByUidAndCompanyIdAndDeleteFlg(uid, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if(user != null){
            Employee employee = employeeRepository.findAllByIdUserAndDeleteFlgAndCompanyId(user.getId(), Constants.DELETE_FLG.NON_DELETE, cid).stream().findFirst().orElse(null);
            FullName fullName = fullNameRepository.findById(employee.getIdFullName()).orElse(new FullName());
            return fullName;
        }
        return new FullName();
    }

    public EmployeeDto getByUid(Long cid, String uid){
        Employee employee = employeeRepository.findByCompanyIdAndIdUser(cid, uid).orElseThrow(() -> new RuntimeException());
        EmployeeDto employeeResponseDTO = new EmployeeDto();
        employeeResponseDTO.setId(employee.getId());
        employeeResponseDTO.setIdUser(employee.getIdUser());
        FullName fullName = fullNameRepository.findById(employee.getIdFullName()).orElse(new FullName());
        employeeResponseDTO.setFullNameDto(new FullNameDto(fullName.getFirstName(), fullName.getLastName()));
        employeeResponseDTO.setCmt(employee.getCode());
        employeeResponseDTO.setTel(employee.getTel());
        Position position = positionService.getById(cid, "", employee.getIdPosition());
        employeeResponseDTO.setIdPosition(position.getId());
        employeeResponseDTO.setNamePosition(position.getName());
        employeeResponseDTO.setStatus(employee.getDeleteFlg());
        employeeResponseDTO.setBirthDay(employee.getBirthDay());
        employeeResponseDTO.setImage(employee.getImage());
        if(employee.getIdAddress() != null){
            AddressDto addressDto = new AddressDto();
            Address address = addressRepository.findById(employee.getIdAddress()).orElse(new Address());
            BeanUtils.copyProperties(address, addressDto);
            employeeResponseDTO.setAddressDto(addressDto);
        }
        return employeeResponseDTO;
    }
}
