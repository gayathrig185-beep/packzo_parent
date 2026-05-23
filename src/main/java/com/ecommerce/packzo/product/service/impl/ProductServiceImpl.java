/*
package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductVariant;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.product.repository.ProductRepository;
import com.ecommerce.packzo.product.repository.ProductVariantRepository;
import com.ecommerce.packzo.product.service.interfaces.ProductService;
import com.ecommerce.packzo.response.OptionDto;
import com.ecommerce.packzo.response.ProductDetailDto;
import com.ecommerce.packzo.response.VariantGroupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecommerce.packzo.constants.ErrorConstants.*;
import static com.ecommerce.packzo.constants.ErrorConstants.PRD_CAT_NOT_FOUND;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

   */
/* public ProductDetailDto getProductDetail(Long productId) {
        List<VariantDto> variantDtos = new ArrayList<>();
        try{
            Product product = Optional.ofNullable(productRepository
                            .findByProductIdAndIsActiveTrue(productId)).filter(prd -> !prd.equals(""))
                    .orElseThrow(() -> new PaczoException(SERVICE_011, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

            if(product!=null){
                List<ProductVariant> variantList = productRepository.findProductsByVariant(product.getProductId());
            }

            return new ProductDetailDto(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductShortDescription(),
                    product.getProductLongDescription(),
                    product.getOriginalPrice(),
                    product.getDiscountPrice(),
                    product.getRating(),
                    product.getTotalRatings(),
                    variantDtos
            );

        } catch (PaczoException e) {
            throw new PaczoException(e.getErrorCode(), e.getText(), e.getErrorMessage());
        } catch (Exception e) {
            logger.debug("Exception Occured in browseAll Method {}", e.getMessage());
            throw new PaczoException(SERVICE_500, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }


    }*//*


    @Override
    public ProductDetailDto getProductDetails(Long productId) {

        Product product = Optional.ofNullable(productRepository.findByProductIdAndIsActiveTrue(productId))
                .orElseThrow(() -> new PaczoException(SERVICE_011, PRD_CAT_NOT_FOUND, PRD_CAT_NOT_FOUND));

        List<ProductVariant> variantsList = productVariantRepository.findByProduct_ProductIdAndIsActiveTrue(productId);

        // 🔥 Group by capacity (90ML, 120ML)
        Map<String, List<ProductVariant>> grouped = variantsList.stream().collect(Collectors.groupingBy(ProductVariant::getCapacity));

        List<VariantGroupDto> variantGroups = grouped.entrySet().stream()
                .map(entry -> {

                    String capacity = entry.getKey();

                    List<OptionDto> options = entry.getValue().stream()
                            .map(v -> {

                                boolean inStock = v.getStock() != null && v.getStock() > 0;

                                return new OptionDto(
                                        v.getVariantId(),
                                        v.getPieces(),
                                        v.getDiscountPrice(),
                                        v.getMrp(),
                                        inStock
                                );

                            }).toList();

                    return new VariantGroupDto(capacity, options);

                }).toList();

        return new ProductDetailDto(
                product.getProductId(),
                product.getProductName(),
                product.getRating(),
                product.getTotalRatings(),
                variantGroups
        );
    }
}

*/
