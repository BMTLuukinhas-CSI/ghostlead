package com.ghostlead.api.repository;

import com.ghostlead.api.entity.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void deveSalvarEEncontrarEmpresa() {
        Company company = new Company(
                "Oficina Teste",
                "oficina@teste.com"
        );

        Company savedCompany = companyRepository.save(company);

        assertThat(savedCompany.getId()).isNotNull();

        Company foundCompany = companyRepository
                .findById(savedCompany.getId())
                .orElseThrow();

        assertThat(foundCompany.getName())
                .isEqualTo("Oficina Teste");

        assertThat(foundCompany.getEmail())
                .isEqualTo("oficina@teste.com");
    }
}
