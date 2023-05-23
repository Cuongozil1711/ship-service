package vn.clmart.manager_service.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.CategoryService;

@RestController
@RequestMapping("user/category")
public class CategoryUserApi {

    private final CategoryService categoryService;

    public CategoryUserApi(CategoryService CategoryService) {
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
}
