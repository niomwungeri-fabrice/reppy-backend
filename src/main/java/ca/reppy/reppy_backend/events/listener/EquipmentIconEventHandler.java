package ca.reppy.reppy_backend.events.listener;

import ca.reppy.reppy_backend.events.models.EquipmentCreatedEvent;
import ca.reppy.reppy_backend.repositories.EquipmentRepository;
import ca.reppy.reppy_backend.utils.EquipmentIconResolver;
import ca.reppy.reppy_backend.utils.JSONUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EquipmentIconEventHandler {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentIconResolver iconResolver;

    @EventListener
    public void onEquipmentCreated(EquipmentCreatedEvent event) {
        log.info("⚡ Received event: {}", JSONUtils.toJson(event));
        handleIconAssignmentAsync(event); // async call here
    }

    @Async
    public void handleIconAssignmentAsync(EquipmentCreatedEvent event) {
        try {
            log.info("⏳ [Async] Resolving icon for: {}", event.equipmentName());
            String icon = iconResolver.resolveIcon(event.equipmentName());
            equipmentRepository.findById(event.equipmentId()).ifPresent(equipment -> {
                equipment.setIconUrl(icon);
                equipmentRepository.save(equipment);
                log.info("✅ Assigned icon: {}", icon);
            });
        } catch (Exception e) {
            log.error("❌ Failed in async icon handler", e);
        }
    }
}
