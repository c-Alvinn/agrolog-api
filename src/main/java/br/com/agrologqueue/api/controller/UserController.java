package br.com.agrologqueue.api.controller;

import br.com.agrologqueue.api.model.dto.user.RegisterCarrierUserRequestDTO;
import br.com.agrologqueue.api.model.dto.user.RegisterDriverRequestDTO;
import br.com.agrologqueue.api.model.dto.user.RegisterInternalUserRequestDTO;
import br.com.agrologqueue.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/internal/register")
    public ResponseEntity<Void> registerInternal(@RequestBody @Valid RegisterInternalUserRequestDTO data) {
        userService.registerInternalUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/carrier/register")
    public ResponseEntity<Void> carrierDriver(@RequestBody @Valid RegisterCarrierUserRequestDTO data) {
        userService.registerCarrierUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/driver/register")
    public ResponseEntity<Void> registerDriver(@RequestBody @Valid RegisterDriverRequestDTO data) {
        userService.registerDriver(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}