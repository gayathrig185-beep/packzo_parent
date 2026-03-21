package com.ecommerce.packzo.user.product.service.impl;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.Category;
import com.ecommerce.packzo.entity.Sector;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.exception.ResourceNotFoundException;
import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.ProductDto;
import com.ecommerce.packzo.response.SectorResponseDto;
import com.ecommerce.packzo.user.product.repository.CategoryRepository;
import com.ecommerce.packzo.user.product.repository.IndustryRepository;
import com.ecommerce.packzo.user.product.repository.ProductRepository;
import com.ecommerce.packzo.user.product.repository.SectorCategoryMapRepository;
import com.ecommerce.packzo.user.product.service.interfaces.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.packzo.constants.ErrorConstants.*;


@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

    private final IndustryRepository industryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SectorCategoryMapRepository sectCatMapRepo;

    public CatalogServiceImpl(IndustryRepository industryRepository, CategoryRepository categoryRepository, ProductRepository productRepository, SectorCategoryMapRepository sectCatMapRepo) {
        this.industryRepository = industryRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.sectCatMapRepo = sectCatMapRepo;
    }

    private static ProductDto apply(Product p) {
        return new ProductDto(p.getProductId(), p.getProductName(), p.getOriginalPrice(), p.getDiscountPrice(), p.getTotalRatings());
    }

    // 1️⃣ Browse All
    public List<CategoryDto> browseAll(int catPageNo, int catPageSize, int prdPageNo , int prdPageSize) {
        List<CategoryDto> categoryList = new ArrayList<>();

        try {
            Pageable catPage = PageRequest.of(catPageNo, catPageSize);
            Pageable prdPage = PageRequest.of(prdPageNo, prdPageSize);

            Page<Category> categories = Optional.ofNullable(categoryRepository.findByIsActiveTrue(catPage))
                    .filter(catList -> !catList.isEmpty())
                    .orElseThrow(() -> new PaczoException(SERVICE_005, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

            categoryList = categories.getContent().stream()
                    .map(cat -> mapCategoryWithProducts(cat, prdPage))
                    .toList();

        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception e) {
            logger.debug("Exception Occured in browseAll Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryList;
    }

    // 2️⃣ Sector-wise
    public SectorResponseDto browseBySector(String sectorCode, int catPageNo, int catPageSize, int prdPageNo, int prdPageSize) {
        SectorResponseDto sectorResponseDto = null;
        try {
            Pageable catPageAble = PageRequest.of(catPageNo, catPageSize);
            Pageable prdPageAble = PageRequest.of(prdPageNo, prdPageSize);

            Optional<Sector> industry = Optional.ofNullable(industryRepository
                            .findBySectorCodeAndIsActiveTrue(sectorCode))
                    .orElseThrow(() -> new PaczoException(SERVICE_004, SECTOR_NOT_FOUND, SECTOR_NOT_FOUND));
            if(industry.isPresent()){

                Page<Category> categories = Optional.ofNullable(sectCatMapRepo.findCategoriesBySector(industry.get().getSectorId() , catPageAble ))
                        .filter(prdCatList -> !prdCatList.isEmpty())
                        .orElseThrow(() -> new PaczoException(SERVICE_006, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

                List<CategoryDto> categoryDtos = categories.getContent().stream()
                        .map(cat -> mapCategoryWithProducts(cat,prdPageAble))
                        .toList();

                sectorResponseDto = new SectorResponseDto(
                        industry.get().getSectorId(),
                        industry.get().getSectorCode(),
                        industry.get().getSectorName(),
                        categoryDtos,
                        categories.getNumber(),
                        categories.getSize(),
                        (int) categories.getTotalElements(),
                        categories.getTotalPages(),categories.isLast());
            }
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseBySector Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return sectorResponseDto;
    }

    // 3️⃣ Category-wise
    public CategoryDto browseByCategory(String sectorId,String categoryId, int pageNo , int pageSize) {
        Category category = null;
        CategoryDto categoryDto = null;
        try {
            category = categoryRepository.findById(categoryId)
                    .filter(Category::isActive)
                    .orElseThrow(() -> new PaczoException(SERVICE_009,PRD_CAT_NOT_FOUND,PRD_CAT_NOT_FOUND));
            if(category!=null){
                Pageable prdPage = PageRequest.of(pageNo,pageSize);
                categoryDto = mapSectorCategoryWithProducts(category,prdPage,sectorId);
            }
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    private CategoryDto mapSectorCategoryWithProducts(Category category, Pageable prdPage,String sectorId) {
        CategoryDto categoryDto = null;
        List<ProductDto> productDtos = new ArrayList<>();
        int pageNo = 0;
        int pageSize = 0;
        Long totalElements = 0L;
        int totalpages = 0;
        boolean islast = false;
        try{
            Optional<Page<Product>> products = productRepository.findByCategoryAndSector(category.getCategoryId(),sectorId,prdPage);

            if(products.isPresent()){
                productDtos = products.get().getContent().stream().map(CatalogServiceImpl::apply)
                        .toList();
                pageNo = products.get().getNumber();
                pageSize = products.get().getSize();
                totalpages = products.get().getTotalPages();
                totalElements = products.get().getTotalElements();
                islast= products.get().isLast();
            }
            categoryDto = new CategoryDto(
                    category.getCategoryId(),
                    category.getCategoryName(),
                    productDtos,pageNo,pageSize,totalElements,totalpages,islast
            );
        } catch (Exception e) {
            logger.debug("Exception Occured in mapCategoryWithProducts Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    private CategoryDto mapCategoryWithProducts(Category category, Pageable pageValue) {
        CategoryDto categoryDto = null;
        List<ProductDto> productDtos = new ArrayList<>();
        int pageNo = 0;
        int pageSize = 0;
        Long totalElements = 0L;
        int totalpages = 0;
        boolean islast = false;
        try {

            Optional<Page<Product>> products = Optional.ofNullable(productRepository.findByCategoryCategoryIdAndIsActiveTrue(category.getCategoryId(), pageValue))
                    .filter(prdList -> !prdList.isEmpty());

            if(products.isPresent()){
                productDtos = products.get().getContent().stream().map(CatalogServiceImpl::apply)
                        .toList();
                pageNo = products.get().getNumber();
                pageSize = products.get().getSize();
                totalpages = products.get().getTotalPages();
                totalElements = products.get().getTotalElements();
                        islast= products.get().isLast();

            }

            categoryDto = new CategoryDto(
                    category.getCategoryId(),
                    category.getCategoryName(),
                    productDtos,pageNo,pageSize,totalElements,totalpages,islast
            );

        } catch (Exception e) {
            logger.debug("Exception Occured in mapCategoryWithProducts Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    // 4 BrowseAll-Category-wise
    public CategoryDto browseAllByCategory(String categoryId,int prdPageNo, int prdPageSize) {
        Category category = null;
        CategoryDto categoryDto = null;
        try {
            category = categoryRepository.findById(categoryId)
                    .filter(Category::isActive)
                    .orElseThrow(() -> new PaczoException(SERVICE_009,PRD_CAT_NOT_FOUND,PRD_CAT_NOT_FOUND));
            if(category!=null){
                Pageable prdPage = PageRequest.of(prdPageNo,prdPageSize);
                categoryDto = mapCategoryWithProducts(category,prdPage);
            }
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }
}
