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
import vn.clmart.manager_service.repository.EmployeeRepo;
import vn.clmart.manager_service.repository.PositionRepo;
import vn.clmart.manager_service.repository.UserRepo;
import vn.clmart.manager_service.utils.Constants;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepository;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    PositionRepo positionRepository;

    public UserDetails loadUserByCodeAndCid(String uid) throws UsernameNotFoundException {
        vn.clmart.manager_service.model.User user = userRepository.findUserByUidAndDeleteFlg(uid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if (user != null) {
            Employee employees = employeeRepo.findAllByIdUserAndDeleteFlg(user.getUid(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
            if(employees != null){
                Position position = positionRepository.findByIdAndDeleteFlg(employees.getIdPosition(),Constants.DELETE_FLG.NON_DELETE).orElseThrow();
                boolean enabled = true;
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + position.getAuthority()));
                return new User(user.getUsername(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                        accountNonLocked, authorities);
            }
            else if(user.getRole().equals(Constants.TYPE_ROLE.CUSTOMER.name())){
                boolean enabled = true;
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                return new User(uid, uid, enabled, accountNonExpired, credentialsNonExpired,
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
