package br.com.agrologqueue.api.service;

import br.com.agrologqueue.api.model.dto.company.CompanyRequestDTO;
import br.com.agrologqueue.api.model.dto.company.CompanyResponseDTO;
import br.com.agrologqueue.api.exceptions.ResourceNotFoundException;
import br.com.agrologqueue.api.exceptions.ValidationException;
import br.com.agrologqueue.api.model.entity.Company;
import br.com.agrologqueue.api.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyResponseDTO create(CompanyRequestDTO dto) {
        if (companyRepository.existsByCnpj(dto.cnpj())) {
            throw new ValidationException("O CNPJ " + dto.cnpj() + " já está cadastrado para outra empresa.");
        }

        Company company = new Company();
        company.setName(dto.name());
        company.setCnpj(dto.cnpj());

        Company savedCompany = companyRepository.save(company);
        return toResponseDTO(savedCompany);
    }

    @Transactional
    public void delete(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", "id", id);
        }
        companyRepository.deleteById(id);
    }

    @Transactional
    public CompanyResponseDTO update(Long id, CompanyRequestDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        if (!company.getCnpj().equals(dto.cnpj()) && companyRepository.existsByCnpj(dto.cnpj())) {
            throw new ValidationException("O novo CNPJ " + dto.cnpj() + " já está em uso.");
        }

        company.setName(dto.name());
        company.setCnpj(dto.cnpj());

        Company updatedCompany = companyRepository.save(company);
        return toResponseDTO(updatedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return toResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> findAll() {
        return companyRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private CompanyResponseDTO toResponseDTO(Company company) {
        return new CompanyResponseDTO(
                company.getId(),
                company.getName(),
                company.getCnpj()
        );
    }
}