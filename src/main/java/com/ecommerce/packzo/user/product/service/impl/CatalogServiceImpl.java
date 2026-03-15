package com.ecommerce.packzo.user.product.service.impl;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductCategory;
import com.ecommerce.packzo.entity.Sector;
import com.ecommerce.packzo.exception.PackzoException;
import com.ecommerce.packzo.exception.ResourceNotFoundException;
import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.ProductDto;
import com.ecommerce.packzo.response.SectorResponseDto;
import com.ecommerce.packzo.user.product.repository.CategoryRepository;
import com.ecommerce.packzo.user.product.repository.IndustryRepository;
import com.ecommerce.packzo.user.product.repository.ProductRepository;
import com.ecommerce.packzo.user.product.service.interfaces.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CatalogServiceImpl(IndustryRepository industryRepository, CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.industryRepository = industryRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // 1️⃣ Browse All
    public List<CategoryDto> browseAll() {
        List<CategoryDto> categoryList = new ArrayList<>();

        try {
            List<ProductCategory> categories = Optional.ofNullable(categoryRepository.findByIsActiveTrue())
                    .filter(catList -> !catList.isEmpty())
                    .orElseThrow(() -> new PackzoException(SERVICE_005,PRD_CAT_NOT_FOUND,PRD_CAT_NOT_FOUND));

            categoryList = categories.stream()
                    .map(this::mapCategoryWithProducts)
                    .toList();
        } catch(PackzoException e){
            throw new PackzoException(e.getErrorCode(),e.getText(),e.getErrorMessage());
        }catch (Exception e) {
            logger.debug("Exception Occured in browseAll Method {}", e.getMessage());
            throw new PackzoException(SERVICE_500,INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR);
        }
        return categoryList;
    }

    // 2️⃣ Sector-wise
    public SectorResponseDto browseBySector(String sectorCode) {
        SectorResponseDto sectorResponseDto = null;
        try{
            Optional<Sector> industry = Optional.ofNullable(industryRepository
                    .findBySectorCodeAndIsActiveTrue(sectorCode))
                    .orElseThrow(() -> new PackzoException(SERVICE_004,SECTOR_NOT_FOUND,SECTOR_NOT_FOUND));
            if(industry.isPresent()){
                List<ProductCategory> categories = Optional.ofNullable(categoryRepository.findBySectorSectorId(industry.get().getSectorId()))
                        .filter(prdCatList -> !prdCatList.isEmpty())
                        .orElseThrow(() -> new PackzoException(SERVICE_006,PRD_CAT_NOT_FOUND,PRD_CAT_NOT_FOUND));

                List<CategoryDto> categoryDtos = categories.stream()
                        .map(this::mapCategoryWithProducts)
                        .toList();

                sectorResponseDto = new SectorResponseDto(
                        industry.get().getSectorCode(),
                        industry.get().getSectorName(),
                        categoryDtos);
            }
        }catch(PackzoException e){
            throw new PackzoException(e.getErrorCode(),e.getText(),e.getErrorMessage());
        }
        catch(Exception ex){
            logger.debug("Exception Occured in browseBySector Method {}", ex.getMessage());
            throw new PackzoException(SERVICE_500,INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR);
        }
        return sectorResponseDto;
    }

    // 3️⃣ Category-wise
    public CategoryDto browseByCategory(String categoryId) {
        ProductCategory category = null;
        try{
            category = categoryRepository.findById(categoryId)
                    .filter(ProductCategory::isActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        }catch(PackzoException e){
            throw new PackzoException(e.getErrorCode(),e.getText(),e.getErrorMessage());
        }catch(Exception ex){
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PackzoException(SERVICE_500,INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR);
        }
        return mapCategoryWithProducts(category);
    }

    private CategoryDto mapCategoryWithProducts(ProductCategory category) {

        List<Product> products = Optional.ofNullable(productRepository.findByProductCategoryProductCategoryIdAndIsActiveTrue(category.getProductCategoryId()))
                .filter(prdList -> !prdList.isEmpty())
                .orElseThrow(() -> new PackzoException(SERVICE_009,INVALID_PRD_DATA,INVALID_PRD_DATA));

        List<ProductDto> productDtos = products.stream()
                .map(p -> new ProductDto(p.getProductId(), p.getProductName(), p.getOriginalPrice(), p.getDiscountPrice()))
                .toList();

        return new CategoryDto(
                category.getProductCategoryId(),
                category.getPrdCategoryName(),
                productDtos
        );
    }
}
