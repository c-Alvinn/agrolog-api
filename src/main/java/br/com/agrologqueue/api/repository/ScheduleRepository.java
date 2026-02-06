package br.com.agrologqueue.api.repository;

import br.com.agrologqueue.api.model.entity.Schedule;
import br.com.agrologqueue.api.model.enums.QueueStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(s.queuePosition) FROM schedule s WHERE s.branch.id = :branchId AND s.queueStatus = 'SCHEDULED'")
    Optional<Integer> findMaxQueuePosition(@Param("branchId") Long branchId);

    @Modifying
    @Transactional
    @Query("UPDATE schedule s SET s.queuePosition = s.queuePosition - 1 " +
            "WHERE s.branch.id = :branchId " +
            "AND s.queueStatus = 'SCHEDULED' " +
            "AND s.queuePosition > :oldPosition")
    int reorderQueuePositions(@Param("branchId") Long branchId, @Param("oldPosition") Integer oldPosition);

    List<Schedule> findAllByBranchIdAndQueueStatusOrderByQueuePositionAsc(
            @Param("branchId") Long branchId,
            @Param("status") QueueStatus status
    );

    Optional<Schedule> findFirstByLicensePlateAndQueueStatusIn(
            String licensePlate,
            List<QueueStatus> statuses
    );

    Optional<Schedule> findFirstByDriver_IdAndQueueStatusIn(
            Long driverId,
            List<QueueStatus> statuses
    );

    long countByBranchIdAndQueueStatus(Long branchId, QueueStatus status);

    List<Schedule> findByBranch_Company_Id(Long companyId);
    List<Schedule> findByCarrierId(Long carrierId);
    List<Schedule> findByDriverId(Long driverId);

    @Query("SELECT s FROM schedule s WHERE s.branch.company.id = :companyId " +
            "AND s.queueStatus IN :statuses " +
            "AND s.createdAt BETWEEN :start AND :end")
    List<Schedule> findReportData(
            @Param("companyId") Long companyId,
            @Param("statuses") List<QueueStatus> statuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT s FROM schedule s " +
            "WHERE s.licensePlate = :plate " +
            "AND s.branch.name = :branchName " +
            "AND s.active = true " +
            "ORDER BY s.createdAt DESC")
    Optional<Schedule> findLatestActiveByPlateAndBranch(
            @Param("plate") String plate,
            @Param("branchName") String branchName
    );
}