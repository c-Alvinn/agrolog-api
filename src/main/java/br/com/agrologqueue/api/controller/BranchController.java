package br.com.agrologqueue.api.controller;

import br.com.agrologqueue.api.model.dto.branch.BranchRequestDTO;
import br.com.agrologqueue.api.model.dto.branch.BranchResponseDTO;
import br.com.agrologqueue.api.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    public ResponseEntity<BranchResponseDTO> create(@RequestBody @Valid BranchRequestDTO dto) {
        BranchResponseDTO response = branchService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> update(@PathVariable Long id, @RequestBody @Valid BranchRequestDTO dto) {
        BranchResponseDTO response = branchService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> findAll() {
        List<BranchResponseDTO> response = branchService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> findById(@PathVariable Long id) {
        BranchResponseDTO response = branchService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BranchResponseDTO>> findByCompanyId(@PathVariable Long companyId) {
        List<BranchResponseDTO> response = branchService.findBranchesByCompanyId(companyId);
        return ResponseEntity.ok(response);
    }
}