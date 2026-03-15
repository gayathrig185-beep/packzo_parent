package com.ecommerce.packzo.user.product.service.interfaces;

import com.ecommerce.packzo.response.SectorDto;
import com.ecommerce.packzo.user.product.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IndustryListService {

    List<SectorDto> getAllActiveSectors();


}
