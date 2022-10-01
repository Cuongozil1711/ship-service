package vn.clmart.manager_service.api.warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ImportListDataWareHouseDto;
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

    @PostMapping("/list")
    protected @ResponseBody
    ResponseEntity<Object> createList(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ImportListDataWareHouseDto importListDataWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.importListWareHouse(importListDataWareHouseDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/edit")
    protected @ResponseBody
    ResponseEntity<Object> editList(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ImportListDataWareHouseDto importListDataWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.editImportListWareHouse(importListDataWareHouseDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/{idReceiptExport}")
    protected @ResponseBody
    ResponseEntity<Object> deleteExport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptExport") Long idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.deleteExport(cid, uid, idReceiptExport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/checkRemoveItem/{id}")
    protected @ResponseBody
    ResponseEntity<Object> checkRemoveItem(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.checkRemoveListImport(cid, uid, id), HttpStatus.OK);
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
            return new ResponseEntity<>(importWareHouseService.getImportByReceiptId(cid, uid, idReceiptImport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/list/{status}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByImportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "status") Integer status,
            Pageable pageable
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.search(cid, status, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/restore")
    protected @ResponseBody
    ResponseEntity<Object> restoreImportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody Long[] idReceiptImport
    ) {
        try {
            return new ResponseEntity<>(importWareHouseService.restoreImportWareHouse(cid, uid, idReceiptImport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
