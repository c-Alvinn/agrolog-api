package br.com.agrologqueue.api.controller;

import br.com.agrologqueue.api.model.dto.schedule.ScheduleRequestDTO;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleResponseDTO;
import br.com.agrologqueue.api.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class SchedulingController {

    private final ScheduleService scheduleService;

    public SchedulingController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
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

    @PatchMapping("/{id}/in-service")
    public ResponseEntity<ScheduleResponseDTO> moveToInService(@PathVariable Long id) {
        ScheduleResponseDTO response = scheduleService.moveToInService(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/completed")
    public ResponseEntity<ScheduleResponseDTO> moveToCompleted(@PathVariable Long id) {
        ScheduleResponseDTO response = scheduleService.moveToCompleted(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ScheduleResponseDTO> cancel(@PathVariable Long id) {
        ScheduleResponseDTO response = scheduleService.cancel(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}