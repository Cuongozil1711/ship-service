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
import vn.clmart.manager_service.model.ListTypeProduct;
import vn.clmart.manager_service.model.Product;
import vn.clmart.manager_service.model.ReviewProduct;
import vn.clmart.manager_service.model.Storage;
import vn.clmart.manager_service.repository.CategoryRepo;
import vn.clmart.manager_service.repository.ListTypeProductRepo;
import vn.clmart.manager_service.repository.ProductRepo;
import vn.clmart.manager_service.repository.ReviewProductRepo;
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

@Service
@Transactional
@Log4j2
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final CategoryRepo categoryRepo;
    private final ReviewProductRepo reviewProductRepo;
    private final ProductRepo productRepo;
    private final ListTypeProductRepo listTypeProductRepo;
    private final CloudinaryService cloudinaryService;

    public ProductService(CategoryRepo categoryRepo, ReviewProductRepo reviewProductRepo, ProductRepo productRepo, ListTypeProductRepo listTypeProductRepo, CloudinaryService cloudinaryService) {
        this.categoryRepo = categoryRepo;
        this.reviewProductRepo = reviewProductRepo;
        this.productRepo = productRepo;
        this.listTypeProductRepo = listTypeProductRepo;
        this.cloudinaryService = cloudinaryService;
    }

    public ProductDto create(ProductDto productDto, String uid) {
        try {
            Product product = Product.of(uid, productDto);
            product = productRepo.save(product);
            Product finalProduct = product;

            if (productDto.getImages() != null) {
                Arrays.stream(productDto.getImages()).distinct().forEach(image -> {
                    cloudinaryService.uploadByte(splitBase64(image), Constants.MODEL_IMAGE.PRODUCT.name(), finalProduct.getId().toString(), finalProduct.getId());
                });
            }

            if (productDto.getReview() != null) {
                List<ReviewProduct> reviewProducts = productDto.getReview().stream().map(rv -> {
                    rv.setProductId(finalProduct.getId());
                    ReviewProduct reviewProduct = ReviewProduct.of(uid, rv);
                    return reviewProduct;
                }).collect(Collectors.toList());

                reviewProductRepo.saveAll(reviewProducts);
            }

            if (productDto.getListTypeProduct() != null) {
                List<ListTypeProduct> listTypeProducts = productDto.getListTypeProduct().stream().map(lt -> {
                    lt.setProductId(finalProduct.getId());
                    ListTypeProduct listTypeProduct = ListTypeProduct.of(uid, lt);
                    return listTypeProduct;
                }).collect(Collectors.toList());

                listTypeProductRepo.saveAll(listTypeProducts);

                Product finalProduct1 = product;
                productDto.getListTypeProduct().stream().forEach(lt -> {
                    if (lt.getImage() != null)
                        cloudinaryService.uploadByte(splitBase64(lt.getImage()), Constants.MODEL_IMAGE.TYPE_PRODUCT.name(), lt.getKeyIndex().toString(), finalProduct1.getId());
                });
            }

            return productDto;
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    public ProductDto get(Long id) {
        try {
            Product product = productRepo.findById(id).orElseThrow(EntityExistsException::new);
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(product, productDto);

            List<String> images = cloudinaryService.getListImage(List.of(id.toString()), Constants.MODEL_IMAGE.PRODUCT.name(), id);
            productDto.setImages(images.toArray(new String[0]));

            List<ListTypeProductDto> listTypeProducts = listTypeProductRepo.findAllByProductId(id).stream().map(ltp -> {
                ListTypeProductDto listTypeProductDto = new ListTypeProductDto();
                BeanUtils.copyProperties(ltp, listTypeProductDto);
                return listTypeProductDto;
            }).collect(Collectors.toList());

            List<ReviewProductDto> ReviewProductDtos = reviewProductRepo.findAllByProductId(id).stream().map(rv -> {
                ReviewProductDto ReviewProductDto = new ReviewProductDto();
                BeanUtils.copyProperties(rv, ReviewProductDto);
                return ReviewProductDto;
            }).collect(Collectors.toList());

            List<Storage> storageImage = cloudinaryService.getListStorage(listTypeProducts.stream().map(ListTypeProductDto::getKeyIndex).collect(Collectors.toList()), Constants.MODEL_IMAGE.TYPE_PRODUCT.name(), id);
            Map<String, Storage> imagesListTypeMap = storageImage.stream().collect(Collectors.toMap(Storage::getRootId, Function.identity(), (o, n) -> o));
            listTypeProducts.stream().map(listTypeProductDto -> {
                if (imagesListTypeMap.containsKey(listTypeProductDto.getKeyIndex()))
                    listTypeProductDto.setImage(imagesListTypeMap.get(listTypeProductDto.getKeyIndex()).getLinkedId());
                return listTypeProductDto;
            }).collect(Collectors.toList());


            productDto.setListTypeProduct(listTypeProducts);
            productDto.setReview(ReviewProductDtos);

            return productDto;
        } catch (Exception ex) {
            logger.error("GET_PRODUCT", ex);
            throw new RuntimeException(ex);
        }
    }

    public ProductDto update(ProductDto productDto, Long id, String uid) {
        Product product = productRepo.findById(id).orElseThrow(EntityExistsException::new);
        BeanUtils.copyProperties(productDto, product);
        product.setUpdateBy(uid);
        product = productRepo.save(product);

        Product finalProduct = product;

        if (productDto.getImages() != null) {
            List<String> images = new ArrayList<>();

            List<String> listImages = cloudinaryService.getListImage(List.of(id.toString()), Constants.MODEL_IMAGE.PRODUCT.name(), id);
            listImages.forEach(e -> {
                if (!Arrays.asList(productDto.getImages()).contains(e)) {
                    images.add(e);
                }
            });
            cloudinaryService.deleteAll(List.of(id.toString()), Constants.MODEL_IMAGE.PRODUCT.name(), images, id);


            Arrays.stream(productDto.getImages()).distinct().forEach(image -> {
                String imageSplit = splitBase64(image);
                if (Base64Utils.isBase64String(imageSplit)) {
                    cloudinaryService.uploadByte(splitBase64(imageSplit), Constants.MODEL_IMAGE.PRODUCT.name(), finalProduct.getId().toString(), id);
                }
            });
        }

        if (productDto.getListTypeProduct() != null) {
            List<ListTypeProduct> listAllTypeProducts = listTypeProductRepo.findAllByProductId(id);
            Map<Long, ListTypeProduct> listTypeProductMap = listAllTypeProducts.stream().collect(Collectors.toMap(ListTypeProduct::getId, Function.identity(), (o, n) -> n));

            List<ListTypeProduct> listTypeProducts = productDto.getListTypeProduct().stream().map(lt -> {
                lt.setProductId(finalProduct.getId());
                ListTypeProduct listTypeProduct = ListTypeProduct.of(uid, lt);
                if (lt.getId() != null) {
                    if (listTypeProductMap.containsKey(lt.getId()))
                        listTypeProduct = listTypeProductMap.get(lt.getId());
                }
                BeanUtils.copyProperties(lt, listTypeProduct);
                listTypeProduct.setUpdateBy(uid);
                return listTypeProduct;
            }).collect(Collectors.toList());

            listTypeProductRepo.saveAll(listTypeProducts);

            productDto.getListTypeProduct().stream().filter(lt -> lt.getImage() != null).forEach(lt -> {
                    if(Base64Utils.isBase64String(lt.getImage())) cloudinaryService.uploadByte(splitBase64(lt.getImage()), Constants.MODEL_IMAGE.TYPE_PRODUCT.name(), lt.getKeyIndex().toString(), id);
            });
        }

        List<ReviewProduct> reviewAllProduct = reviewProductRepo.findAllByProductId(id);
        Map<Long, ReviewProduct> reviewAllProductMap = reviewAllProduct.stream().collect(Collectors.toMap(ReviewProduct::getId, Function.identity(), (o, n) -> n));


        if (productDto.getReview() != null) {
            List<ReviewProduct> reviewProducts = productDto.getReview().stream().map(rv -> {
                rv.setProductId(finalProduct.getId());
                ReviewProduct reviewProduct = ReviewProduct.of(uid, rv);
                if(rv.getId() != null) {
                    if (reviewAllProductMap.containsKey(rv.getId()))
                        reviewProduct = reviewAllProductMap.get(rv.getId());
                }
                BeanUtils.copyProperties(rv, reviewProduct);
                return reviewProduct;
            }).collect(Collectors.toList());

            reviewProductRepo.saveAll(reviewProducts);
        }

        return productDto;
    }

    private String splitBase64(String base64) {
        String base[] = base64.split(",");
        return base.length >= 2 ? base[1] : base[0];
    }


    public void delete(Long id, String uid) {
        Product product = productRepo.findById(id).orElseThrow(EntityExistsException::new);
        product.setDeleteFlg(Constants.DELETE_FLG.DELETE);
        product.setUpdateBy(uid);
        productRepo.save(product);
    }

    public Page<ProductDto> findAll(Pageable pageable) {
        try {
            Page<Product> page = productRepo.findAll(pageable);
            List<Long> productId = page.get().collect(Collectors.toList()).stream().map(Product::getId).collect(Collectors.toList());

            List<Storage> storageImage = cloudinaryService.getListStorage(productId.stream().map(String::valueOf).collect(Collectors.toList()), Constants.MODEL_IMAGE.PRODUCT.name(), null);
            Map<String, List<Storage>> imagesListTypeMap = storageImage.stream().collect(Collectors.groupingBy(Storage::getRootId));

            List<ProductDto> product = page.get().collect(Collectors.toList()).stream().map(p -> {
                ProductDto productDto = new ProductDto();
                BeanUtils.copyProperties(p, productDto);
                if (imagesListTypeMap.containsKey(p.getId().toString()))
                    productDto.setImages(imagesListTypeMap.get(p.getId().toString()).stream().map(Storage::getLinkedId).collect(Collectors.toList()).toArray(new String[0]));
                return productDto;
            }).collect(Collectors.toList());

            return new PageImpl(product, pageable, page.getTotalElements());
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

}
