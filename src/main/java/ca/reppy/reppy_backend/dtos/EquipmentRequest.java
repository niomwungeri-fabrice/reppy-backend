package ca.reppy.reppy_backend.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EquipmentRequest {
    private String name;
    private String description;

}
