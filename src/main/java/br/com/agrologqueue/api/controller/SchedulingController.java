package br.com.agrologqueue.api.controller;

import br.com.agrologqueue.api.model.dto.report.QueueStatusReportDTO;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleRequestDTO;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleResponseDTO;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleTransitionDTO;
import br.com.agrologqueue.api.model.enums.ReportPeriod;
import br.com.agrologqueue.api.service.ReportingService;
import br.com.agrologqueue.api.service.ScheduleService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class SchedulingController {

    private final ScheduleService scheduleService;
    private final ReportingService reportingService;

    public SchedulingController(ScheduleService scheduleService, ReportingService reportingService) {
        this.scheduleService = scheduleService;
        this.reportingService = reportingService;
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> create(@RequestBody @Valid ScheduleRequestDTO dto) {
        ScheduleResponseDTO response = scheduleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> findAll() {
        List<ScheduleResponseDTO> response = scheduleService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> findById(@PathVariable Long id) {
        ScheduleResponseDTO response = scheduleService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/in-service")
    public ResponseEntity<Void> moveToInService(@RequestBody @Valid ScheduleTransitionDTO request) {
        scheduleService.moveToInService(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/completed")
    public ResponseEntity<Void> moveToCompleted(@RequestBody @Valid ScheduleTransitionDTO request) {
        scheduleService.moveToCompleted(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestBody @Valid ScheduleTransitionDTO request) {
        scheduleService.cancel(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports/queue-status")
    public ResponseEntity<QueueStatusReportDTO> getQueueStatusReport(@RequestParam Long branchId) {
        return ResponseEntity.ok(reportingService.getQueueStatusByBranch(branchId));
    }

    @GetMapping("/reports/performance/pdf")
    public void exportToPdf(@RequestParam(defaultValue = "TODAY") ReportPeriod period,
                            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        String dateStamp = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));

        String fileName = String.format("relatorio_atendimentos_%s_%s.pdf", period.name(), dateStamp);

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        reportingService.generatePerformanceReport(period, response);
    }
}