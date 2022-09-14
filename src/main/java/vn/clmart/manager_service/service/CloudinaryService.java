package vn.clmart.manager_service.service;

import org.springframework.stereotype.Service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Storage;
import vn.clmart.manager_service.repository.ItemsRepository;
import vn.clmart.manager_service.repository.StorageRepository;

@Service
public class CloudinaryService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Cloudinary cloudinaryConfig;

    private final StorageRepository storageRepository;

    private final ItemsRepository itemsRepository;

    public CloudinaryService(Cloudinary cloudinaryConfig, StorageRepository storageRepository, ItemsRepository itemsRepository) {
        this.cloudinaryConfig = cloudinaryConfig;
        this.storageRepository = storageRepository;
        this.itemsRepository = itemsRepository;
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
    @Transactional
    public String upload(MultipartFile file,  String type, Long rootId, Long cid) {
            try {
                Map uploadResult = cloudinaryConfig.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String publicId = uploadResult.get("url").toString();
                Storage storage = new Storage();
                storage.setTypeCode(type);
                storage.setRootId(rootId);
                storage.setCompanyId(cid);
                storageRepository.save(storage);
                if(type.equals("items")){
                    Items items = itemsRepository.findById(rootId).orElse(null);
                    if(items != null){
                        items.setImage(publicId);
                        itemsRepository.save(items);
                    }
                }
                return publicId;
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                return null;
            }
    }

}
