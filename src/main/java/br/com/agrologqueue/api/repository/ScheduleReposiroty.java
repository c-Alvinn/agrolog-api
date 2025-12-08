package br.com.agrologqueue.api.repository;

import br.com.agrologqueue.api.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleReposiroty extends JpaRepository<Schedule, Long> {
}
