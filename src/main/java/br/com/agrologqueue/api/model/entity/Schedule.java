package br.com.agrologqueue.api.model.entity;

import br.com.agrologqueue.api.model.enums.GrainType;
import br.com.agrologqueue.api.model.enums.OperationType;
import br.com.agrologqueue.api.model.enums.QueueStatus;
import br.com.agrologqueue.api.model.enums.TruckType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "schedule")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_sequence")
    @SequenceGenerator(name = "schedule_sequence", sequenceName = "seq_schedule", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "grain_type", nullable = false)
    private GrainType grainType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "truck_type", nullable = false)
    private TruckType truckType;

    @Enumerated(EnumType.STRING)
    @Column(name = "queue_status", nullable = false)
    private QueueStatus queueStatus;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @CreationTimestamp
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;
}