package vn.clmart.manager_service.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.CategoryDto;
import vn.clmart.manager_service.service.CategoryService;
import vn.clmart.manager_service.service.CloudinaryService;

import java.util.Base64;

@RestController
@RequestMapping("/category")
public class CategoryApi {

    private final vn.clmart.manager_service.service.CategoryService categoryService;

    public CategoryApi(CategoryService CategoryService) {
        this.categoryService = CategoryService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search() {
        try {
            return new ResponseEntity<>(categoryService.search(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> getById(
            @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(categoryService.getById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> update(
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody CategoryDto CategoryDto
    ) {
        try {
            return new ResponseEntity<>(categoryService.update(CategoryDto, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader String uid,
            @RequestBody CategoryDto CategoryDto
    ) {
        try {
            return new ResponseEntity<>(categoryService.create(CategoryDto, uid), HttpStatus.OK);
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
            return new ResponseEntity<>(categoryService.delete(uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
