package vn.clmart.manager_service.api.warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.service.ExportWareHouseService;

import java.util.List;

@RestController
@RequestMapping("/export-ware-house")
public class ExportWareHouseApi {

    private final ExportWareHouseService exportWareHouseService;


    public ExportWareHouseApi(ExportWareHouseService exportWareHouseService) {
        this.exportWareHouseService = exportWareHouseService;
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ExportWareHouseDto exportWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.exportWareHouse(exportWareHouseDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/list")
    protected @ResponseBody
    ResponseEntity<Object> createList(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody List<ExportWareHouseDto> exportWareHouseDto
    ) {
        try {
            for(ExportWareHouseDto item : exportWareHouseDto){
                exportWareHouseService.exportWareHouse(item, cid, uid);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping({"idReceiptExport"})
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptExport") Long idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.completeWareHouse(cid, uid, idReceiptExport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
    @GetMapping({"idReceiptExport"})
    protected @ResponseBody
    ResponseEntity<Object> findAllByIdReceiptExport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptExport") Long idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.findAll(cid, uid, idReceiptExport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/list")
    protected @ResponseBody
    ResponseEntity<Object> findAllByImportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            Pageable pageable
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.search(cid, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
