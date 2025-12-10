package br.com.agrologqueue.api.controller;

import br.com.agrologqueue.api.model.dto.carrier.CarrierRequestDTO;
import br.com.agrologqueue.api.model.dto.carrier.CarrierResponseDTO;
import br.com.agrologqueue.api.service.CarrierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrier")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @PostMapping
    public ResponseEntity<CarrierResponseDTO> create(@RequestBody @Valid CarrierRequestDTO dto) {
        CarrierResponseDTO response = carrierService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carrierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarrierResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CarrierRequestDTO dto) {
        CarrierResponseDTO response = carrierService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CarrierResponseDTO>> findAll() {
        List<CarrierResponseDTO> response = carrierService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierResponseDTO> findById(@PathVariable Long id) {
        CarrierResponseDTO response = carrierService.findById(id);
        return ResponseEntity.ok(response);
    }
}