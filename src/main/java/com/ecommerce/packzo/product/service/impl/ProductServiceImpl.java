package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductVariant;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.product.repository.CategoryProductTypeMapRepository;
import com.ecommerce.packzo.product.repository.ProductRepository;
import com.ecommerce.packzo.product.repository.ProductVariantRepository;
import com.ecommerce.packzo.product.service.interfaces.ProductService;
import com.ecommerce.packzo.response.OptionDto;
import com.ecommerce.packzo.response.ProductDetailDto;
import com.ecommerce.packzo.response.VariantDto;
import com.ecommerce.packzo.response.VariantGroupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ecommerce.packzo.constants.ErrorConstants.*;
import static com.ecommerce.packzo.constants.ErrorConstants.PRD_CAT_NOT_FOUND;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final CategoryProductTypeMapRepository categoryProductTypeMapRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductVariantRepository productVariantRepository, CategoryProductTypeMapRepository categoryProductTypeMapRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.categoryProductTypeMapRepository = categoryProductTypeMapRepository;
    }


    @Override
    public ProductDetailDto getProductDetails(Long productId, String categoryId) {
        ProductDetailDto productDetailDto = null;
        List<VariantDto> variantDtosList = new ArrayList<>();
        try {
            Product product = Optional.ofNullable(productRepository.findByProductIdAndIsActiveTrue(productId))
                    .orElseThrow(() -> new PaczoException(SERVICE_011, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

            if (product != null) {
                boolean isExists = categoryProductTypeMapRepository.existsByCategory_CategoryIdAndProductType_TypeId(categoryId, product.getProductType().getTypeId());
                if (isExists) {
                    List<ProductVariant> variantsList = Optional.ofNullable(productVariantRepository.findVariants(productId))
                            .filter(variantList -> !variantList.isEmpty()).orElseThrow();

                    for (ProductVariant prdVariant : variantsList) {
                        VariantDto variantDto = new VariantDto();
                        variantDto.setVariantId(prdVariant.getVariantId());
                        variantDto.setVariantName(prdVariant.getVariantName());
                        variantDto.setDimension(prdVariant.getDimensions());
                        variantDto.setColor(prdVariant.getColor());
                        variantDto.setCapacityMl(prdVariant.getCapacity());
                        variantDto.setOriginalPrice(prdVariant.getOriginalPrice());
                        variantDto.setDiscountPrice(prdVariant.getDiscountedPrice());
                        variantDto.setNoOfPieces(prdVariant.getNoOfPieces());
                        variantDto.setUnits(prdVariant.getUnits());
                        variantDto.setMaterial(prdVariant.getMaterial());
                        variantDto.setOrigin(product.getCountryOfOrigin());
                        variantDto.setDescription(prdVariant.getDescription());
                        variantDto.setDisclaimer(prdVariant.getDisclaimer());
                        variantDto.setKeyfeatures(prdVariant.getFeature());
                        variantDto.setMarketNameAndAddress(product.getManfacturersAddress());
                        variantDto.setSellerAddress(product.getSellerAddress());
                        variantDto.setReturnPolicy(product.getReturnPolicy());
                        variantDto.setCareDetails(product.getCustomerCare());
                        variantDto.setQunatity(product.getQuantity());
                        variantDtosList.add(variantDto);
                    }
                    List<VariantGroupDto> variantGroupDtoList =
                            variantDtosList.stream()
                                    .sorted(Comparator.comparingInt(v ->
                                            Integer.parseInt(
                                                    v.getCapacityMl()
                                                            .replaceAll("[^0-9]", "")
                                            )))
                                    .collect(Collectors.groupingBy(
                                            VariantDto::getCapacityMl,
                                            LinkedHashMap::new,
                                            Collectors.toList()))
                                    .entrySet()
                                    .stream()
                                    .map(entry -> new VariantGroupDto(
                                            entry.getKey(),
                                            entry.getValue()))
                                    .toList();
                    /*Map<String, List<VariantDto>> groupedVariants = Optional.of(variantDtosList).get()
                            .stream().collect(Collectors.groupingBy(VariantDto::getCapacityMl));
                    List<VariantGroupDto> variantGroupDtoList = Optional.of(groupedVariants).filter(variantMap -> !variantMap.isEmpty())
                            .get().entrySet().stream().map(entry -> new VariantGroupDto(entry.getKey(), entry.getValue())).toList();
*/
                    productDetailDto = new ProductDetailDto(product.getProductId(), product.getProductName(), categoryId, variantGroupDtoList);
                }
            }
            return productDetailDto;
        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception e) {
            logger.debug("Exception Occured in browseAll Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }
}

