package br.com.agrologqueue.api.repository;

import br.com.agrologqueue.api.model.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchReposiroty extends JpaRepository<Branch, Long> {
}
