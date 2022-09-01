package vn.clmart.manager_service.api;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.dto.SupplierDto;
import vn.clmart.manager_service.service.SupplierService;
import vn.clmart.manager_service.untils.ResponseAPI;

@RestController
@RequestMapping("/supplier")
public class SupplierApi {

    private final SupplierService supplierService;

    public SupplierApi(SupplierService supplierService) {
        this.supplierService = supplierService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseAPI search(
            @RequestHeader Long cid,
            @RequestHeader String uid
            , Pageable pageable) {
        try {
            return ResponseAPI.handlerSuccess(supplierService.search(cid, pageable));
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
            return ResponseAPI.handlerSuccess(supplierService.getById(cid, uid, id));
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
            @RequestBody SupplierDto supplierDto
    ) {
        try {
            return ResponseAPI.handlerSuccess(supplierService.update(supplierDto, cid, uid, id));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseAPI create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody SupplierDto supplierDto
    ) {
        try {
            return ResponseAPI.handlerSuccess(supplierService.create(supplierDto, cid, uid));
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
            return ResponseAPI.handlerSuccess(supplierService.delete(cid, uid, id));
        } catch (Exception ex) {
            return ResponseAPI.handlerException(ex);
        }
    }

}
