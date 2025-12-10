package br.com.agrologqueue.api.model.dto.branch;

public record BranchResponseDTO(
        Long id,
        String name,
        String address,
        String branchCode,
        Long companyId,
        String companyName
) {}