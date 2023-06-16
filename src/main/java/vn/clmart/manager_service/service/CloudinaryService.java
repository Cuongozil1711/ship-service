package vn.clmart.manager_service.service;

import org.springframework.stereotype.Service;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.utils.Constants;

public interface CloudinaryService {
    String upload(MultipartFile file,  String type, Long rootId, Long cid);
    String uploadByte(String base64,  String type, String rootId, Long entityId);

    List<String> getListImage(List<String> rootIds, String modelType, Long entityId);

    List<Storage> getListStorage(List<String> rootIds, String modelType, Long entityId);

    List<Storage> getListStorageByEntityId(List<String> rootIds, String modelType, List<Long> entityIds);
    void deleteAll(List<String> rootIds, String modelType, List<String> images, Long entityId);

}
