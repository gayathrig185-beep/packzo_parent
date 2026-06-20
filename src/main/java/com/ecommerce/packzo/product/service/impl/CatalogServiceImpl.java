package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.entity.*;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.helper.ValidationHelper;
import com.ecommerce.packzo.product.repository.*;
import com.ecommerce.packzo.response.*;
import com.ecommerce.packzo.product.service.interfaces.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ecommerce.packzo.constants.ErrorConstants.*;


@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

    private final IndustryRepository industryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SectorCategoryMapRepository sectCatMapRepo;
    private final ProductTypeRepository productTypeRepository;
    private static ValidationHelper validationHelper = null;

    public CatalogServiceImpl(IndustryRepository industryRepository, CategoryRepository categoryRepository, ProductRepository productRepository, SectorCategoryMapRepository sectCatMapRepo, ProductTypeRepository productTypeRepository, ValidationHelper validationHelper) {
        this.industryRepository = industryRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.sectCatMapRepo = sectCatMapRepo;
        this.productTypeRepository = productTypeRepository;
        CatalogServiceImpl.validationHelper = validationHelper;
    }

    private static ProductDto apply(Product p) {
        return new ProductDto(p.getProductId(), p.getProductName(), p.getProductType().getTypeName(), p.getOriginalPrice(), p.getDiscountPrice(), p.getTotalRatings(), validationHelper.calculationOfDiscountedPrice(p.getOriginalPrice(), p.getDiscountPrice()), p.getQuantity());
    }

    // 1️⃣ Browse All
    public List<CategoryDto> browseAll(int catPageNo, int catPageSize, int prdPageNo, int prdPageSize) {
        List<CategoryDto> categoryList;
        try {
            logger.debug("CatalogService browseAll method begins");

            Pageable catPage = PageRequest.of(catPageNo, catPageSize);
            Pageable prdPage = PageRequest.of(prdPageNo, prdPageSize);

            Page<Category> categories = Optional.ofNullable(categoryRepository.findByIsActiveTrue(catPage))
                    .filter(catList -> !catList.isEmpty())
                    .orElseThrow(() -> new PaczoException(SERVICE_005, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

            categoryList = categories.getContent().stream()
                    .map(cat -> mapCategoryWithProducts(cat, prdPage))
                    .toList();

            logger.debug("categoryList::: -> {}", categoryList);

            logger.debug("CatalogService browseAll method ends");

        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception e) {
            logger.debug("Exception Occured in browseAll Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryList;
    }

    // 2️⃣ Sector-wise
    public List<CategoryDto> browseBySector(String sectorCode, int catPageNo, int catPageSize, int prdPageNo, int prdPageSize) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        try {
            Pageable catPageAble = PageRequest.of(catPageNo, catPageSize);
            Pageable prdPageAble = PageRequest.of(prdPageNo, prdPageSize);

            Optional<Sector> industry = Optional.ofNullable(industryRepository
                            .findBySectorCodeAndIsActiveTrue(sectorCode))
                    .orElseThrow(() -> new PaczoException(SERVICE_004, SECTOR_NOT_FOUND, SECTOR_NOT_FOUND));
            if (industry.isPresent()) {

                Page<Category> categories = Optional.ofNullable(sectCatMapRepo.findCategoriesBySector(industry.get().getSectorId(), catPageAble))
                        .filter(prdCatList -> !prdCatList.isEmpty())
                        .orElseThrow(() -> new PaczoException(SERVICE_006, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

                categoryDtoList = categories.getContent().stream()
                        .map(cat -> mapSectorCategoryWithProducts(cat, prdPageAble, industry.get().getSectorId()))
                        .toList();
            }
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseBySector Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDtoList;
    }

    // 3️⃣ Category-wise
    public List<ProductDto> browseBySectorCategory(String sectorId, String categoryId) {
        Category category = null;
        List<ProductDto> productDtoList = null;
        try {
            category = categoryRepository.findById(categoryId)
                    .filter(Category::isActive)
                    .orElseThrow(() -> new PaczoException(SERVICE_009, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));
            if (category != null) {

                Optional<Sector> industry = Optional.ofNullable(industryRepository
                                .findBySectorCodeAndIsActiveTrue(sectorId))
                        .orElseThrow(() -> new PaczoException(SERVICE_004, SECTOR_NOT_FOUND, SECTOR_NOT_FOUND));
                if(industry.isPresent()){
                    productDtoList = fetchProductsBasedonSectorCategory(category, industry.get().getSectorId());
                }
            }
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return productDtoList;
    }


    public CategoryDto browseBySectorByPagination(String sector, String categoryValue, int prdPageNo, int prdPageSize) {
        CategoryDto categoryDto = null;
        try {
            Pageable prdPageAble = PageRequest.of(prdPageNo, prdPageSize);
            Optional<Sector> industry = Optional.ofNullable(industryRepository
                            .findBySectorCodeAndIsActiveTrue(sector))
                    .orElseThrow(() -> new PaczoException(SERVICE_014, SECTOR_NOT_FOUND, SECTOR_NOT_FOUND));
            if (industry.isPresent()) {
                Category category = categoryRepository.findById(categoryValue)
                        .filter(Category::isActive)
                        .orElseThrow(() -> new PaczoException(SERVICE_011, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

                categoryDto = mapSectorCategoryWithProducts(category, prdPageAble, industry.get().getSectorId());
            }

        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    private List<ProductDto> fetchProductsBasedonSectorCategory(Category category, long sectorId) {
        List<ProductDto> productDtoList;
        try {
            List<Product> productsList = Optional.ofNullable(productRepository.findProductsByCategoryAndSector(category.getCategoryId(), sectorId))
                    .filter(prod -> !prod.isEmpty()).orElseThrow(() -> new PaczoException(SERVICE_019, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

            productDtoList = productsList.stream().map(product -> new ProductDto(product.getProductId(), product.getProductName(), product.getProductType().getTypeName(), product.getOriginalPrice(), product.getDiscountPrice(), product.getTotalRatings(), validationHelper.calculationOfDiscountedPrice(product.getOriginalPrice(), product.getDiscountPrice()),product.getQuantity()))
                    .toList();
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        }  catch (Exception e) {
            logger.debug("Exception Occured in fetchProductsBasedonSectorCategory Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return productDtoList;
    }

    private CategoryDto mapSectorCategoryWithProducts(Category category, Pageable prdPage, long sectorId) {
        CategoryDto categoryDto = null;
        List<ProductDto> productDtos = new ArrayList<>();
        int pageNo = 0;
        int pageSize = 0;
        Long totalElements = 0L;
        int totalpages = 0;
        boolean islast = false;
        List<String> subList = new ArrayList<>();
        ProductPageDto productPageDto = new ProductPageDto();
        try {

            Optional<Page<Product>> products = Optional.ofNullable(productRepository.findProductsByCategoryAndSector(category.getCategoryId(), sectorId, prdPage));

            if (products.isPresent()) {
                productDtos = products.get().getContent().stream().map(CatalogServiceImpl::apply)
                        .toList();
                pageNo = products.get().getNumber();
                pageSize = products.get().getSize();
                totalpages = products.get().getTotalPages();
                totalElements = products.get().getTotalElements();
                islast = products.get().isLast();
                subList.addFirst("All");
                List<String> subLists = productTypeRepository.findByCategoryId(category.getCategoryId()).stream()
                        .map(ProductType::getTypeName)
                        .toList();
                subList.addAll(subLists);
                productPageDto.setProducts(productDtos);
                productPageDto.setPage(pageNo);
                productPageDto.setSize(pageSize);
                productPageDto.setTotalElements(totalElements);
                productPageDto.setTotalPages(totalpages);
                productPageDto.setLast(islast);


            }
            categoryDto = new CategoryDto(
                    category.getCategoryId(),
                    category.getCategoryName(), subList,
                    productPageDto
            );
        } catch (Exception e) {
            logger.debug("Exception Occured in mapCategoryWithProducts Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    private CategoryDto mapCategoryWithProducts(Category category, Pageable pageValue) {
        CategoryDto categoryDto;
        List<ProductDto> productDtos = new ArrayList<>();
        List<String> subList = new ArrayList<>();
        int pageNo = 0;
        int pageSize = 0;
        Long totalElements = 0L;
        int totalpages = 0;
        boolean islast = false;
        ProductPageDto productPageDto = new ProductPageDto();
        try {
            logger.debug("mapCategoryWithProducts(Category category, Pageable pageValue) method begins");

            Optional<Page<Product>> products = Optional.ofNullable(productRepository.findProductsByCategory(category.getCategoryId(), pageValue))
                    .filter(prdList -> !prdList.isEmpty());

            if(products.isPresent()){
                logger.debug("ProductsList {}", products.get().getContent());

                productDtos = products.get().getContent().stream().map(CatalogServiceImpl::apply)
                        .toList();
                pageNo = products.get().getNumber();
                pageSize = products.get().getSize();
                totalpages = products.get().getTotalPages();
                totalElements = products.get().getTotalElements();
                islast = products.get().isLast();
                subList.addFirst("All");

                List<String> subLists = productTypeRepository.findByCategoryId(category.getCategoryId()).stream()
                        .map(ProductType::getTypeName)
                        .toList();
                subList.addAll(subLists);

                productPageDto.setProducts(productDtos);
                productPageDto.setPage(pageNo);
                productPageDto.setSize(pageSize);
                productPageDto.setTotalElements(totalElements);
                productPageDto.setTotalPages(totalpages);
                productPageDto.setLast(islast);
            }
            categoryDto = new CategoryDto(
                    category.getCategoryId(),
                    category.getCategoryName(), subList,
                    productPageDto
            );
            logger.debug("Category value {}", categoryDto);
            logger.debug("mapCategoryWithProducts(Category category, Pageable pageValue) method ends");
        } catch (Exception e) {
            logger.debug("Exception Occured in mapCategoryWithProducts Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    // 4 BrowseAll-Category-wise
    public List<ProductDto> browseAllByCategory(String categoryId) {
        Category category;
        List<ProductDto> productsDtoList = new ArrayList<>();
        try {
            category = categoryRepository.findById(categoryId)
                    .filter(Category::isActive)
                    .orElseThrow(() -> new PaczoException(SERVICE_020, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));
            if (category != null) {
                productsDtoList = fetchAllProductByCategoryId(category);
            }
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return productsDtoList;
    }

    private List<ProductDto> fetchAllProductByCategoryId(Category category) {
        List<ProductDto> productsDtoList;
        try {
            List<Product> productsList = Optional.ofNullable(productRepository.findProductsByCategory(category.getCategoryId()))
                    .filter(prdList -> !prdList.isEmpty()).orElseThrow(() -> new PaczoException(SERVICE_021, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));
            productsDtoList = productsList.stream().map(product -> new ProductDto(product.getProductId(), product.getProductName(), product.getProductType().getTypeName(), product.getOriginalPrice(), product.getDiscountPrice(), product.getTotalRatings(), validationHelper.calculationOfDiscountedPrice(product.getOriginalPrice(), product.getDiscountPrice()),product.getQuantity())).toList();
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return productsDtoList;
    }

    public CategoryDto browseByProductTypeId(String sectCode, String categoryValue, String productTypeName, int prdPageNo, int prdPageSize) {
        CategoryDto categoryDto = null;
        try {
            Pageable prdPageAble = PageRequest.of(prdPageNo, prdPageSize);
            List<String> subList = new ArrayList<>();
            List<String> businessTypeList = Arrays.asList("CORP", "CATT", "CLD", "WED", "REST", "HOTEL", "BIO", "BLK", "HOSP", "DECOR");
            Optional<Page<Product>> products = Optional.empty();
            Category category = categoryRepository.findById(categoryValue)
                    .filter(Category::isActive)
                    .orElseThrow(() -> new PaczoException(SERVICE_023, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));
            if (sectCode.equalsIgnoreCase("all")) {
                if(productTypeName.equalsIgnoreCase("all")){
                    products = Optional.ofNullable(productRepository.findProductsByCategory(categoryValue,prdPageAble));
                }else{
                    products = Optional.ofNullable(productRepository.findProductsByCategorybyProductTypeName(categoryValue, productTypeName, prdPageAble));
                }
            } else {
                if(businessTypeList.contains(sectCode)){
                     Optional<Sector> industry = Optional.ofNullable(industryRepository.findBySectorCodeAndIsActiveTrue(sectCode))
                             .orElseThrow(() -> new PaczoException(SERVICE_004, SECTOR_NOT_FOUND, SECTOR_NOT_FOUND));
                     if(industry.isPresent()){
                         if(productTypeName.equalsIgnoreCase("all")){
                             products = Optional.ofNullable(productRepository.findProductsByCategoryAndSector(category.getCategoryId(), industry.get().getSectorId(), prdPageAble));
                         }
                         else{
                             products = Optional.ofNullable(productRepository.findProductsByCategoryAndSectorbyProductId(categoryValue, industry.get().getSectorId(), productTypeName, prdPageAble));
                         }
                     }
                }else{
                    throw new PaczoException(SERVICE_025, INVALID_SECTORID, INVALID_SECTORID);
                }
            }
            List<ProductDto> productDtos = List.of();
            ProductPageDto productPageDto = new ProductPageDto();
            int pageNo = 0;
            int pageSize = 0;
            int totalpages = 0;
            long totalElements = 0;
            boolean islast = false;
            if (products.isPresent()) {
                productDtos = products.get().getContent().stream().map(CatalogServiceImpl::apply)
                        .toList();
                pageNo = products.get().getNumber();
                pageSize = products.get().getSize();
                totalpages = products.get().getTotalPages();
                totalElements = products.get().getTotalElements();
                islast = products.get().isLast();
                subList.add("All");
                List<String> subLists = productTypeRepository.findByCategoryId(category.getCategoryId()).stream()
                        .map(ProductType::getTypeName)
                        .toList();
                subList.addAll(subLists);
                productPageDto.setProducts(productDtos);
                productPageDto.setPage(pageNo);
                productPageDto.setSize(pageSize);
                productPageDto.setTotalElements(totalElements);
                productPageDto.setTotalPages(totalpages);
                productPageDto.setLast(islast);
            }
            categoryDto = new CategoryDto(
                    category.getCategoryId(),
                    category.getCategoryName(), subList,
                    productPageDto
            );
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

    @Override
    public Map<String, List<String>> getFilterValueForBrowseAll() {
        try {
            List<String> categoryIdList = Optional.ofNullable(categoryRepository.findAllCategories())
                    .filter(cateList -> !cateList.isEmpty()).orElseThrow();

            return categoryIdList.stream()
                    .collect(Collectors.toMap(
                            catId -> catId, // The Key
                            catId -> productTypeRepository.findByCategoryId(catId).stream()
                                    .map(ProductType::getTypeName)
                                    .collect(Collectors.toList()),       // Value
                            (existing, replacement) -> existing,             // Merge function (handles duplicate keys)
                            LinkedHashMap::new
                    ));

        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    /*@Override
    public Map<String, List<String>> getFilterValueBySectorCode(String sectorId) {
        try {
            List<String> catList = Optional.ofNullable(sectCatMapRepo.findCategoriesBysector(sectorId))
                    .filter(sectList -> !sectList.isEmpty()).orElseThrow();
            return catList.stream()
                    .collect(Collectors.toMap(
                            catId -> catId, // The Key
                            catId -> productTypeRepository.findByCategoryId(catId).stream()
                                    .map(ProductType::getTypeName)
                                    .collect(Collectors.toList()),       // Value
                            (existing, replacement) -> existing,             // Merge function (handles duplicate keys)
                            LinkedHashMap::new
                    ));
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }*/

    public List<CategoryListResponse> getCategories(String key) {
        List<CategoryListResponse> catListResponse = new ArrayList<>();
        List<Category> categoryList = new ArrayList<>();
        if(key.equalsIgnoreCase("all")){
             categoryList = categoryRepository.findAllCategoriesList();
        }else{
            Optional<Sector> industry = Optional.ofNullable(industryRepository
                            .findBySectorCodeAndIsActiveTrue(key))
                    .orElseThrow(() -> new PaczoException(SERVICE_014, SECTOR_NOT_FOUND, SECTOR_NOT_FOUND));
            if(industry.isPresent()){
                categoryList = sectCatMapRepo.findCategoriesListBysector(industry.get().getSectorId());
            }

        }

        if(categoryList!=null && !categoryList.isEmpty()){
            for (Category cat : categoryList) {
                CategoryListResponse categoryListResponse = new CategoryListResponse();
                categoryListResponse.setCategoryName(cat.getCategoryName());
                categoryListResponse.setCategoryUrl(null);
                categoryListResponse.setCategoryId(cat.getCategoryId());
                catListResponse.add(categoryListResponse);
            }
        }
            return catListResponse;
    }

    @Override
    public CategoryDto browseAllByCategoryByPagination(String categoryValue, int prdPageNo, int prdPageSize) {
        CategoryDto categoryDto;
        try {
            Pageable prdPageAble = PageRequest.of(prdPageNo, prdPageSize);
            Category category = categoryRepository.findById(categoryValue)
                    .filter(Category::isActive)
                    .orElseThrow(() -> new PaczoException(SERVICE_022, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

            categoryDto = mapCategoryWithProducts(category, prdPageAble);

        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception ex) {
            logger.debug("Exception Occured in browseByCategory Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
        return categoryDto;
    }

}
