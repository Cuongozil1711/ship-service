package vn.clmart.manager_service.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.repository.EmployeeRepository;
import vn.clmart.manager_service.repository.PositionRepository;
import vn.clmart.manager_service.repository.UserRepository;
import vn.clmart.manager_service.untils.Constants;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PositionRepository positionRepository;

    public UserDetails loadUserByCodeAndCid(Long cid, String uid) throws UsernameNotFoundException {
        vn.clmart.manager_service.model.User user = userRepository.findUserByUidAndCompanyIdAndDeleteFlg(uid, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if (user != null) {
            Employee employees = employeeRepository.findAllByIdUserAndDeleteFlgAndCompanyId(user.getUid(), Constants.DELETE_FLG.NON_DELETE, cid).stream().findFirst().orElseThrow();
            if(employees.getIdPosition() != null){
                Position position = positionRepository.findByIdAndCompanyIdAndDeleteFlg(employees.getIdPosition(), cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
                boolean enabled = true;
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + position.getName()));
                return new User(user.getUsername(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                        accountNonLocked, authorities);
            }
            else{
                throw new UsernameNotFoundException("Authority Not Found");
            }
        } else {
            throw new UsernameNotFoundException("Username Not Found");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
