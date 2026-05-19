package com.ganesh.skillbridge.service;

import com.ganesh.skillbridge.entity.Company;
import com.ganesh.skillbridge.exception.ResourceNotFoundException;
import com.ganesh.skillbridge.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepo;

    public List<Company> getAllCompanies() {
        return companyRepo.findAll();
    }

    public Company getById(Long id) {
        return companyRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }
}
