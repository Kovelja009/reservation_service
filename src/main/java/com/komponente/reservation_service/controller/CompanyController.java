package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.CompanyDto;
import com.komponente.reservation_service.dto.CompanyIdDto;
import com.komponente.reservation_service.security.CheckSecurity;
import com.komponente.reservation_service.security.service.TokenService;
import com.komponente.reservation_service.service.CompanyService;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private CompanyService companyService;
    private TokenService tokenService;


    @PostMapping("/add_company")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<CompanyDto> addCompany(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CompanyDto companyDto) {
        return new ResponseEntity<>(companyService.addCompany(companyDto), HttpStatus.CREATED);
    }

    @GetMapping("/get_company")
    public ResponseEntity<CompanyIdDto> getCompany(@RequestParam @Valid String name) {
        return new ResponseEntity<>(companyService.getCompany(name), HttpStatus.OK);
    }

    @PostMapping("/update")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<CompanyDto> updateCompany(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CompanyDto companyDto) {
        return new ResponseEntity<>(companyService.updateCompany(tokenService.getIdFromToken(authorization), companyDto), HttpStatus.OK);
    }
}
