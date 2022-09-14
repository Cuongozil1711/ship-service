package vn.clmart.manager_service.api.shophouse;


import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ItemsDto;
import vn.clmart.manager_service.service.ItemsService;

@RestController
@RequestMapping("/items")
public class ItemsApi {

    private final vn.clmart.manager_service.service.ItemsService ItemsService;

    public ItemsApi(ItemsService ItemsService) {
        this.ItemsService = ItemsService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestParam(value = "", required = false) String search
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(ItemsService.search(cid, pageable, search), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> getById(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(ItemsService.getById(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> update(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody ItemsDto ItemsDto
    ) {
        try {
            return new ResponseEntity<>(ItemsService.update(ItemsDto, cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ItemsDto ItemsDto
    ) {
        try {
            return new ResponseEntity<>(ItemsService.create(ItemsDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/delete/{id}")
    protected @ResponseBody
    ResponseEntity<Object> delete(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
    ) {
        try {
            return new ResponseEntity<>(ItemsService.delete(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
