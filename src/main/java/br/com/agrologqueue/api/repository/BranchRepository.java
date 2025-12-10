package br.com.agrologqueue.api.repository;

import br.com.agrologqueue.api.model.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    boolean existsByCode(String branchCode);

    List<Branch> findByCompanyId(Long companyId);
}
