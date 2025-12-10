package br.com.agrologqueue.api.repository;

import br.com.agrologqueue.api.model.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    boolean existsByName(String name);
}
