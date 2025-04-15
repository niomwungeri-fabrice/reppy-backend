package ca.reppy.reppy_backend.repositories;


import ca.reppy.reppy_backend.entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    @Query("SELECT e FROM Equipment e WHERE e.user.userId = :userId")
    List<Equipment> findByUserId(@Param("userId") UUID userId);
}
