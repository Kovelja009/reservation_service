package com.komponente.reservation_service.controller;

import com.komponente.reservation_service.dto.CompanyDto;
import com.komponente.reservation_service.dto.CompanyIdDto;
import com.komponente.reservation_service.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/change_name")
    public ResponseEntity<String> changeName(@RequestParam @Valid Long id, @RequestParam @Valid String name) {
        return new ResponseEntity<>(companyService.changeName(id, name), HttpStatus.OK);
    }

    @PostMapping("/change_info")
    public ResponseEntity<String> changeInfo(@RequestParam @Valid Long id, @RequestParam @Valid String info) {
        return new ResponseEntity<>(companyService.changeInfo(id, info), HttpStatus.OK);
    }

    @PostMapping("/add_company")
    public ResponseEntity<CompanyDto> addCompany(@RequestBody @Valid CompanyDto companyDto) {
        return new ResponseEntity<>(companyService.addCompany(companyDto), HttpStatus.CREATED);
    }

    @GetMapping("/get_company")
    public ResponseEntity<CompanyIdDto> getCompany(@RequestParam @Valid String name) {
        return new ResponseEntity<>(companyService.getCompany(name), HttpStatus.OK);
    }
}
