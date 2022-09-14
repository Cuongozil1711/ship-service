package vn.clmart.manager_service.api.warehouse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
import vn.clmart.manager_service.dto.ReceiptExportWareHouseDto;
import vn.clmart.manager_service.service.ImportWareHouseService;

@RestController
@RequestMapping("/import-ware-house")
public class ImportWareHouseApi {

    private final ImportWareHouseService importWareHouseService;


    public ImportWareHouseApi(ImportWareHouseService importWareHouseService) {
        this.importWareHouseService = importWareHouseService;
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ImportWareHouseDto importWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.importWareHouse(importWareHouseDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("{idReceiptImport}")
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptImport") Long idReceiptImport
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.completeWareHouse(cid, uid, idReceiptImport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{idReceiptImport}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByIdReceiptImport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptImport") Long idReceiptImport
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.findAll(cid, uid, idReceiptImport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
