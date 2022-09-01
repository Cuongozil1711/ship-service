package vn.clmart.manager_service.api;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.EmployeeDto;
import vn.clmart.manager_service.dto.UserLoginDto;
import vn.clmart.manager_service.service.UserService;
import vn.clmart.manager_service.untils.ResponseAPI;

@RestController
@RequestMapping("/user")
public class UserApi {

    private final UserService userService;

    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    protected @ResponseBody
    ResponseAPI login(
            @RequestHeader Long cid,
            @RequestBody UserLoginDto userLoginDto
    ) {
        try {
            return ResponseAPI.handlerSuccess(userService.authenticateUserHandler(userLoginDto, cid));
        } catch (BadCredentialsException e) {
            return ResponseAPI.handlerError("PASSWORD_INCORRECT", HttpStatus.INTERNAL_SERVER_ERROR.value());

        } catch (NullPointerException ex) {
            return ResponseAPI.handlerError("DATA_NOT_VALID", HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

    @PostMapping("/create")
    protected @ResponseBody
    ResponseAPI create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody EmployeeDto employeeDto
    ) {
        try {
            return ResponseAPI.handlerSuccess(userService.createEmployee(employeeDto, cid, uid));
        }
        catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }
}
