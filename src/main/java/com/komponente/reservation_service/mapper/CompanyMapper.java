package com.komponente.reservation_service.mapper;

import com.komponente.reservation_service.dto.CompanyDto;
import com.komponente.reservation_service.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    public Company companyDtoToCompany(CompanyDto companyDto) {
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setInfo(companyDto.getInfo());
        return company;
    }

    public CompanyDto companyToCompanyDto(Company company) {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName(company.getName());
        companyDto.setInfo(company.getInfo());
        return companyDto;
    }


}
