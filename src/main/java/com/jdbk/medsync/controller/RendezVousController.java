package com.jdbk.medsync.controller;

import com.jdbk.medsync.exception.AlreadyBusySalleException;
import com.jdbk.medsync.exception.NotFoundException;
import com.jdbk.medsync.model.DTO.RendezVousDTO;
import com.jdbk.medsync.model.entity.RendezVous;
import com.jdbk.medsync.model.form.RendezVousForm;
import com.jdbk.medsync.service.DemandeService;
import com.jdbk.medsync.service.RendezVousService;
import com.jdbk.medsync.service.SalleService;
import com.jdbk.medsync.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/rendezvous")
public class RendezVousController {
    private final RendezVousService rendezVousService;
    private final SalleService salleService;
    private final DemandeService demandeService;
    private final UserService userService;

    public RendezVousController(RendezVousService rendezVousService, SalleService salleService, DemandeService demandeService, UserService userService) {
        this.rendezVousService = rendezVousService;
        this.salleService = salleService;
        this.demandeService = demandeService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addRendezVous(@RequestBody @Valid RendezVousForm form) {
        // todo set user demande et salle id
        RendezVous entity = form.toEntity();
            entity.setUser(userService.getOne(form.getIdUser()));
            entity.setDemande(demandeService.getOne(form.getDemande()));
            entity.setSalle(salleService.getOne(form.getSalle()));
            Long idRendezVous = rendezVousService.addRendezVous(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(idRendezVous);

    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<?> updateRendezVous(@PathVariable Long id, @RequestBody @Valid RendezVousForm form) {
        RendezVous entity = form.toEntity();
        try {
            RendezVous rendezVous = rendezVousService.updateRendezVous(id, entity);
            return ResponseEntity.status(HttpStatus.OK).body(entity); // TODO Renvoyer DTO
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<?> removeRendezVous(@PathVariable Long id) {
        try {
            RendezVous rendezVous = rendezVousService.removeRendezVous(id);
            return ResponseEntity.status(HttpStatus.OK).body("Rendez-vous deleted");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rendez-vous not found");
        }
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<?> getRendezVousById(@PathVariable Long id) {
        try {
            RendezVous rendezVous = rendezVousService.getRendezVousById(id);
            RendezVousDTO rendezVousDTO = RendezVousDTO.toDTO(rendezVous);
            return ResponseEntity.status(HttpStatus.OK).body(rendezVousDTO);
        }catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                rendezVousService.getAll().stream()
                        .map(RendezVousDTO::toDTO)
                        .toList()
        );
    }


}
