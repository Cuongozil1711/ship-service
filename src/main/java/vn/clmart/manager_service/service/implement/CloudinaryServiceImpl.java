package vn.clmart.manager_service.service.implement;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Storage;
import vn.clmart.manager_service.repository.EmployeeRepo;
import vn.clmart.manager_service.repository.ItemsRepo;
import vn.clmart.manager_service.repository.StorageRepo;
import vn.clmart.manager_service.service.CloudinaryService;
import vn.clmart.manager_service.utils.Constants;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Cloudinary cloudinaryConfig;

    private final StorageRepo storageRepository;

    private final ItemsRepo itemsRepo;

    private final EmployeeRepo employeeRepo;

    public CloudinaryServiceImpl(Cloudinary cloudinaryConfig, StorageRepo storageRepository, ItemsRepo itemsRepo, EmployeeRepo employeeRepo) {
        this.cloudinaryConfig = cloudinaryConfig;
        this.storageRepository = storageRepository;
        this.itemsRepo = itemsRepo;
        this.employeeRepo = employeeRepo;
    }

    /**
     * Upload a MultipartFile to Cloudinary. More info:
     * https://stackoverflow.com/a/39572293
     *
     * @param type
     * @param rootId
     * @param file to be uploaded
     * @return the publicId assigned to the uploaded file, or null in case of
     * error
     */
    @Override
    @Transactional
    public String upload(MultipartFile file, String type, Long rootId, Long cid) {
        try {
            Map uploadResult = cloudinaryConfig.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String publicId = uploadResult.get("url").toString();
            Storage storage = new Storage();
            storage.setTypeCode(type);
            storage.setRootId(rootId.toString());
            storageRepository.save(storage);
            if(type.equals("items")){
                Items items = itemsRepo.findById(rootId).orElse(null);
                if(items != null){
                    items.setImage(publicId);
                    itemsRepo.save(items);
                }
            }
            if(type.equals("employee")){
                Employee items = employeeRepo.findById(rootId).orElse(null);
                if(items != null){
                    items.setImage(publicId);
                    employeeRepo.save(items);
                }
            }
            return publicId;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public String uploadByte(String base64,  String type, String rootId, Long entityId) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            Map uploadResult = cloudinaryConfig.uploader().upload(bytes, ObjectUtils.emptyMap());
            String publicId = uploadResult.get("url").toString();
            Storage storage = new Storage();
            storage.setTypeCode(type);
            storage.setRootId(rootId);
            storage.setLinkedId(publicId);
            storage.setEntityId(entityId);
            storageRepository.save(storage);
            return publicId;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getListImage(List<String> rootIds, String modelType, Long entityId) {
        try {
            return storageRepository.findAllByRootIdInAndTypeCodeAndDeleteFlgAndEntityId(rootIds, modelType, Constants.DELETE_FLG.NON_DELETE, entityId).stream().map(Storage::getLinkedId).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Storage> getListStorage(List<String> rootIds, String modelType, Long entityId) {
        try {
            if(entityId == null) return storageRepository.findAllByRootIdInAndTypeCodeAndDeleteFlg(rootIds, modelType, Constants.DELETE_FLG.NON_DELETE);
            return storageRepository.findAllByRootIdInAndTypeCodeAndDeleteFlgAndEntityId(rootIds, modelType, Constants.DELETE_FLG.NON_DELETE, entityId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Storage> getListStorageByEntityId(List<String> rootIds, String modelType, List<Long> entityIds) {
        try {
            return storageRepository.findAllByRootIdInAndTypeCodeAndDeleteFlgAndEntityIdIn(rootIds, modelType, Constants.DELETE_FLG.NON_DELETE, entityIds);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }


    @Override
    public void deleteAll(List<String> rootIds, String modelType, List<String> images, Long entityId) {
        try {
            List<Storage> storage = storageRepository.findAllByRootIdInAndTypeCodeAndDeleteFlgAndLinkedIdNotInAndEntityId(rootIds, modelType, Constants.DELETE_FLG.NON_DELETE, images, entityId);
            storage.stream().map(s -> {
                s.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                return s;
            }).collect(Collectors.toList());
            storageRepository.saveAll(storage);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}
