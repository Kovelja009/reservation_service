package com.komponente.reservation_service.service;

import com.komponente.reservation_service.dto.CompanyDto;
import com.komponente.reservation_service.dto.CompanyIdDto;

public interface CompanyService {
    String changeName(Long id, String name);
    String changeInfo(Long id,String info);
    CompanyDto addCompany(CompanyDto companyDto);
    CompanyIdDto getCompany(String name);
}
