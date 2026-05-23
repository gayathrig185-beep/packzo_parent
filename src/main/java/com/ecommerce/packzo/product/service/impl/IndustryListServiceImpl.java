package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.entity.Sector;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.response.SectorDto;
import com.ecommerce.packzo.product.repository.IndustryRepository;
import com.ecommerce.packzo.product.service.interfaces.IndustryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.packzo.constants.ErrorConstants.*;
import static com.ecommerce.packzo.constants.ErrorConstants.INTERNAL_SERVER_ERROR;

@Service
public class IndustryListServiceImpl implements IndustryListService {

    private static final Logger logger = LoggerFactory.getLogger(IndustryListServiceImpl.class);

    private final IndustryRepository sectorRepository;

    public IndustryListServiceImpl(IndustryRepository sectorRepository) {

        this.sectorRepository = sectorRepository;
    }

    public List<SectorDto> getAllActiveSectors() {
        List<SectorDto> sectList = new ArrayList<>();
        try {
            logger.debug("getAllActiveSector method begins");

            List<Sector> sectorList = Optional.ofNullable(sectorRepository
                            .findByIsActiveTrueOrderBySectorNameAsc()).filter(sectorLists -> !sectorLists.isEmpty())
                    .orElseThrow(() -> new PaczoException(SERVICE_007,INVALID_SECTOR_LIST,INVALID_SECTOR_LIST));
            sectList = sectorList.stream()
                    .map(sector -> {
                        SectorDto sectorResponseDto = new SectorDto();
                        sectorResponseDto.setSectorId(String.valueOf(sector.getSectorId()));
                        sectorResponseDto.setSectorCode(sector.getSectorCode());
                        sectorResponseDto.setSectorName(sector.getSectorName());
                        sectorResponseDto.setSectorOrder(sector.getSectorOrder());
                        return sectorResponseDto;
                    }).toList();
            logger.debug("Sector List {}", sectList);
            logger.debug("getAllActiveSector method ends");
        }catch(PaczoException e){
            throw new PaczoException(e.getErrorCode(),e.getText(),e.getErrorMessage());
        }catch(Exception ex){
            logger.debug("Exception Occured in getAllActiveSectors Method {}", ex.getMessage());
            throw new PaczoException(SERVICE_500,INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR);
        }
        return sectList;
    }
}
