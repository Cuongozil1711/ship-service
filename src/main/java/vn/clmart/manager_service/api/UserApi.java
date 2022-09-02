package vn.clmart.manager_service.api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.EmployeeDto;
import vn.clmart.manager_service.dto.UserLoginDto;
import vn.clmart.manager_service.service.UserService;
import vn.clmart.manager_service.untils.ResponseAPI;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @PostMapping("/login")
    protected @ResponseBody
    ResponseEntity<Object> login(
            @RequestHeader Long cid,
            @RequestBody UserLoginDto userLoginDto
    ) {
        try {
            return new ResponseEntity<>(userService.authenticateUserHandler(userLoginDto, cid), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("PASSWORD_INCORRECT",  HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>("DATA_NOT_VALID",  HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/create")
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody EmployeeDto employeeDto
    ) {
        try {
            return new ResponseEntity<>(userService.createEmployee(employeeDto, cid, uid), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
