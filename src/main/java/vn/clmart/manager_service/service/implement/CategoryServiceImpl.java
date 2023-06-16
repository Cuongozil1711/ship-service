package vn.clmart.manager_service.service.implement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.CategoryDto;
import vn.clmart.manager_service.model.Category;
import vn.clmart.manager_service.repository.CategoryRepo;
import vn.clmart.manager_service.service.CategoryService;
import vn.clmart.manager_service.utils.Constants;
import vn.clmart.manager_service.utils.MapUtils;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    private void validate(CategoryDto categoryDto){
        if(categoryDto.getName().isEmpty()) throw new BusinessException("empty");
    }

    @Override
    public Category create(CategoryDto categoryDto, String uid){
        try {
            validate(categoryDto);
            Category category = Category.of(categoryDto, uid);
            return categoryRepo.save(category);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Category update(CategoryDto categoryDto, String uid, Long id){
        try {
            Category item = categoryRepo.findById(id).orElseThrow();
            MapUtils.copyWithoutAudit(categoryDto, item);
            item.setUpdateBy(uid);
            return categoryRepo.save(item);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Category getById(Long id){
        try {
            return categoryRepo.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Category> search(){
        try {
            return categoryRepo.findAll();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Category delete(String uid, Long id){
        try {
            Category Category = categoryRepo.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Category.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Category.setUpdateBy(uid);
            return categoryRepo.save(Category);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
