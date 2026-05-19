package com.ganesh.skillbridge.repository;

import com.ganesh.skillbridge.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
