package com.ecommerce.packzo.user.product.service.impl;

import com.ecommerce.packzo.response.SectorDto;
import com.ecommerce.packzo.response.SectorResponseDto;
import com.ecommerce.packzo.user.product.controller.IndustrySectorListController;
import com.ecommerce.packzo.user.product.repository.IndustryRepository;
import com.ecommerce.packzo.user.product.service.interfaces.IndustryListService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndustryListServiceImpl implements IndustryListService {

    private static final Logger logger = LoggerFactory.getLogger(IndustryListServiceImpl.class);

    private final IndustryRepository sectorRepository;

    public IndustryListServiceImpl(IndustryRepository sectorRepository) {

        this.sectorRepository = sectorRepository;
    }

    public List<SectorDto> getAllActiveSectors() {
        logger.debug("getAllActiveSector method begins");

        List<SectorDto> sectorList = Optional.ofNullable(sectorRepository
                .findByIsActiveTrueOrderBySectorNameAsc()).filter(sectList -> !sectList.isEmpty())
                .get().stream()
                .map(sector -> {
                    SectorDto sectorResponseDto = new SectorDto();
                    sectorResponseDto.setSectorCode(sector.getSectorCode());
                    sectorResponseDto.setSectorName(sector.getSectorName());
                    return sectorResponseDto;
                }).toList();

        logger.debug("Sector List {}",sectorList);
        return sectorList;
    }
}
