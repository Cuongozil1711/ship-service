package vn.clmart.manager_service.api;

import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.service.PositionService;
import vn.clmart.manager_service.untils.ResponseAPI;

@RestController
@RequestMapping("/position")
public class PositionApi {

    private final PositionService positionService;

    public PositionApi(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping("/search")
    protected @ResponseBody
    ResponseAPI search(
           @RequestHeader Long cid,
           @RequestHeader String uid
            , Pageable pageable) {
        try {
            return ResponseAPI.handlerSuccess(positionService.search(cid, pageable));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseAPI getById(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
            , Pageable pageable) {
        try {
            return ResponseAPI.handlerSuccess(positionService.getById(cid, uid, id));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

    @PutMapping("{id}")
    protected @ResponseBody
    ResponseAPI update(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody PositionDto positionDto
            ) {
        try {
            return ResponseAPI.handlerSuccess(positionService.update(positionDto, cid, uid, id));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseAPI create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody PositionDto positionDto
            ) {
        try {
            return ResponseAPI.handlerSuccess(positionService.create(positionDto, cid, uid));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

    @PutMapping("/delete/{id}")
    protected @ResponseBody
    ResponseAPI delete(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
    ) {
        try {
            return ResponseAPI.handlerSuccess(positionService.delete(cid, uid, id));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }
}
