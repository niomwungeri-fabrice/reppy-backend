package ca.reppy.reppy_backend.services;

import ca.reppy.reppy_backend.dtos.EquipmentRequest;
import ca.reppy.reppy_backend.entities.Equipment;
import ca.reppy.reppy_backend.entities.User;
import ca.reppy.reppy_backend.events.models.EquipmentCreatedEvent;
import ca.reppy.reppy_backend.exceptions.NotFoundException;
import ca.reppy.reppy_backend.repositories.EquipmentRepository;
import ca.reppy.reppy_backend.repositories.UserRepository;
import ca.reppy.reppy_backend.utils.GenericUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    ApplicationEventPublisher eventPublisher;

    public EquipmentService(EquipmentRepository equipmentRepository, UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.eventPublisher = applicationEventPublisher;
    }


    public void saveOrUpdateEquipment(EquipmentRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GenericUtils.ParsedEquipment parsed = GenericUtils.parse(request.getName());
        List<Equipment> userEquipments = equipmentRepository.findByUserId(user.getUserId());

        for (Equipment eq : userEquipments) {
            GenericUtils.ParsedEquipment existing = GenericUtils.parse(eq.getName());
            if (existing.type().equals(parsed.type()) &&
                    existing.weight() != null &&
                    existing.weight().equals(parsed.weight())) {
                log.info("Exact type+weight equipment already exists: {}", eq.getName());
                return;
            }
        }

        Equipment equipment = new Equipment();
        equipment.setName(WordUtils.capitalizeFully(request.getName()));
        equipment.setDescription(request.getDescription());
        equipment.setUser(user);
        equipment.setIconUrl(null); // Set as null for now

        Equipment saved = equipmentRepository.save(equipment);

        // 🔥 Fire async icon assignment
        log.info("📣 Publishing EquipmentCreatedEvent for {}", saved.getName());
        eventPublisher.publishEvent(new EquipmentCreatedEvent(saved.getEquipmentId(), saved.getName()));
    }

    public List<Equipment> getEquipmentsByUserId(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return equipmentRepository.findByUserId(user.getUserId());
    }


    public void deleteEquipment(String equipmentId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Equipment equipment = getEquipmentsByUserId(user.getUserId().toString()).stream()
                .filter(eq -> eq.getEquipmentId().toString().equals(equipmentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Equipment not found"));
        equipmentRepository.delete(equipment);
    }
}
