package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.CompanyDto;
import com.komponente.reservation_service.mapper.CompanyMapper;
import com.komponente.reservation_service.model.Company;
import com.komponente.reservation_service.repository.CompanyRepository;
import com.komponente.reservation_service.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {
    private CompanyRepository companyRepo;
    private CompanyMapper companyMapper;

    @Override
    public String changeName(Long id, String name) {
        Optional<Company> optionalCompany = companyRepo.findByName(name);
        if (optionalCompany.isPresent())
            throw new IllegalArgumentException("Company with name " + name + " already exists");
        Company company = companyRepo.findById(id).get();
        company.setName(name);
        companyRepo.save(company);

        return name;
    }

    @Override
    public String changeInfo(Long id, String info) {
        Company company = companyRepo.findById(id).get();
        company.setInfo(info);
        companyRepo.save(company);
        return info;
    }

    @Override
    public CompanyDto addCompany(CompanyDto companyDto) {
        Optional<Company> optionalCompany = companyRepo.findByName(companyDto.getName());
        if (optionalCompany.isPresent())
            throw new IllegalArgumentException("Company with name " + companyDto.getName() + " already exists");
        Company company = companyMapper.companyDtoToCompany(companyDto);
        companyRepo.save(company);
        return companyDto;
    }
}
