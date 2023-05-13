package vn.clmart.manager_service.api;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ProductDto;
import vn.clmart.manager_service.dto.request.SearchDTO;
import vn.clmart.manager_service.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductApi {
    private final vn.clmart.manager_service.service.ProductService productService;

    public ProductApi(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(Pageable pageable, @RequestBody SearchDTO<Long> request) {
        try {
            if (request.getSearch() == null) request.setSearch("");
            return new ResponseEntity<>(productService.findAll(pageable, request), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> getById(
            @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(productService.get(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> update(
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody ProductDto productDto
    ) {
        try {
            return new ResponseEntity<>(productService.update(productDto, id, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader String uid,
            @RequestBody ProductDto productDto
    ) {
        try {
            return new ResponseEntity<>(productService.create(productDto, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/delete/{id}")
    protected @ResponseBody
    ResponseEntity<Object> delete(
            @RequestHeader String uid,
            @PathVariable("id") Long id
    ) {
        try {
            productService.delete(id, uid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
