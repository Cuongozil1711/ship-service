package vn.clmart.manager_service.api.user;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.config.message.MessageContext;
import vn.clmart.manager_service.dto.request.SearchDTO;
import vn.clmart.manager_service.service.ProductService;

@RestController
@RequestMapping("/user/product")
public class ProductUserApi {
    private final ProductService productService;

    public ProductUserApi(ProductService productService) {
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

    @GetMapping("/find-other/{id}")
    protected @ResponseBody
    ResponseEntity<Object> findOther(
            @PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(productService.productOther(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/find/{id}")
    protected @ResponseBody
    ResponseEntity<Object> find(
            @PathVariable("id") Long id) {
        try {
            String text = MessageContext.getMessage("app.lang.vi");
            throw new BusinessException(text);
//            return new ResponseEntity<>(productService.productOther(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
