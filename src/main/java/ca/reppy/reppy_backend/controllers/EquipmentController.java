package ca.reppy.reppy_backend.controllers;

import ca.reppy.reppy_backend.dtos.EquipmentRequest;
import ca.reppy.reppy_backend.dtos.GenericSuccessResponse;
import ca.reppy.reppy_backend.entities.Equipment;
import ca.reppy.reppy_backend.services.EquipmentService;
import ca.reppy.reppy_backend.utils.JSONUtils;
import ca.reppy.reppy_backend.utils.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/equipments")
@Slf4j
public class EquipmentController {

    private final EquipmentService equipmentService;
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping("")
    public ResponseEntity<?> addEquipment(@RequestBody EquipmentRequest equipmentRequest, Principal principal) {
        log.info("equipment requests: {}", JSONUtils.toJson(equipmentRequest));
        this.equipmentService.saveOrUpdateEquipment(equipmentRequest, principal.getName());
        GenericSuccessResponse response = GenericSuccessResponse.builder().data(String.format("Equipment %s was added", equipmentRequest.getName())).status(ResponseStatus.SUCCESS).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("")
    public ResponseEntity<?> getEquipments(Principal principal) {
        List<Equipment> equipments = this.equipmentService.getEquipmentsByUserId(principal.getName());
        GenericSuccessResponse response = GenericSuccessResponse.builder().data(equipments).status(ResponseStatus.SUCCESS).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipment(@PathVariable("id") String equipmentID, Principal principal) {
        this.equipmentService.deleteEquipment(equipmentID, principal.getName());
        GenericSuccessResponse response = GenericSuccessResponse.builder().data(String.format("Equipment %s was deleted", equipmentID)).status(ResponseStatus.SUCCESS).build();
        log.info(JSONUtils.toJson(response));
        return ResponseEntity.noContent().build();
    }
}
