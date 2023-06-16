package vn.clmart.manager_service.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.ListTypeProductDto;
import vn.clmart.manager_service.dto.ProductDto;
import vn.clmart.manager_service.dto.ReviewProductDto;
import vn.clmart.manager_service.dto.TypeAttributeDto;
import vn.clmart.manager_service.dto.request.SearchDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.utils.Base64Utils;
import vn.clmart.manager_service.utils.Constants;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ProductService {
    ProductDto create(ProductDto productDto, String uid);
    ProductDto get(Long id);
    ProductDto update(ProductDto productDto, Long id, String uid);

    void delete(Long id, String uid);
    Page<ProductDto> findAll(Pageable pageable, SearchDTO<Long> request);
    List<ProductDto> productOther(Long idProduct);
}
