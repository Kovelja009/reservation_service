package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.CompanyDto;
import com.komponente.reservation_service.dto.CompanyIdDto;
import com.komponente.reservation_service.exceptions.ForbiddenException;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.CompanyMapper;
import com.komponente.reservation_service.model.Company;
import com.komponente.reservation_service.repository.CompanyRepository;
import com.komponente.reservation_service.service.CompanyService;
import io.github.resilience4j.retry.Retry;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {
    private CompanyRepository companyRepo;
    private CompanyMapper companyMapper;
    private RestTemplate userServiceRestTemplate;
    private Retry serviceRetry;

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

    @Override
    public CompanyIdDto getCompany(String name) {
        Optional<Company> optionalCompany = companyRepo.findByName(name);
        if (!optionalCompany.isPresent())
            throw new IllegalArgumentException("Company with name " + name + " does not exist");
        CompanyIdDto companyIdDto = new CompanyIdDto();
        companyIdDto.setId(optionalCompany.get().getId());
        return companyIdDto;
    }

    @Override
    public CompanyDto updateCompany(Long userId, CompanyDto companyDto) {

        Optional<Company> optionalCompany = companyRepo.findByName(companyDto.getName());
        String str_id = Retry.decorateSupplier(serviceRetry, () -> ReservationServiceImpl.getCompanyId(userId, userServiceRestTemplate)).get();
        Long id = Long.parseLong(str_id);
        if(optionalCompany.isPresent() && !optionalCompany.get().getId().equals(id))
            throw new IllegalArgumentException("Company with name " + companyDto.getName() + " already exists");

        Optional<Company> optional = companyRepo.findById(id);
        if(!optional.isPresent())
            throw new IllegalArgumentException("Company with id " + id + " does not exist");

        Company company = optional.get();
        company.setName(companyDto.getName());
        company.setInfo(companyDto.getInfo());
        companyRepo.save(company);

        return companyDto;
    }

}
