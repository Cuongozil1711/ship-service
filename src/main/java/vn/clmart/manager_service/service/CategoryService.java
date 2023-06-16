package vn.clmart.manager_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.CategoryDto;
import vn.clmart.manager_service.model.Category;
import vn.clmart.manager_service.repository.CategoryRepo;
import vn.clmart.manager_service.utils.Constants;
import vn.clmart.manager_service.utils.MapUtils;

import java.util.List;

public interface CategoryService {
    Category create(CategoryDto categoryDto, String uid);
    Category update(CategoryDto categoryDto, String uid, Long id);
    Category getById(Long id);
    List<Category> search();
    Category delete(String uid, Long id);
}
