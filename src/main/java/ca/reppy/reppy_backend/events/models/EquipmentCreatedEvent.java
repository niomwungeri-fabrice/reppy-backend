package ca.reppy.reppy_backend.events.models;

import java.util.UUID;

public record EquipmentCreatedEvent(UUID equipmentId, String equipmentName) {}
