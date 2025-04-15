package ca.reppy.reppy_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class EquipmentIconResolver {

    private static final String DEFAULT_ICON = "/icons/default.svg";
    private static final String ICON_PATH_PREFIX = "/icons/";

    private final Set<String> iconNames = new HashSet<>();

    @PostConstruct
    public void loadIconNames() {
        try (InputStream is = getClass().getResourceAsStream("/icons/equipment-icon-index.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode iconsNode = root.get("icons");

            if (iconsNode != null) {
                iconsNode.fieldNames().forEachRemaining(iconNames::add);
                log.info("✅ Loaded {} icon keywords from equipment-icon-index.json", iconNames.size());
            } else {
                log.warn("⚠️ No 'icons' node found in ph.json");
            }
        } catch (Exception e) {
            log.error("❌ Failed to load equipment-icon-index.json for icon resolution", e);
            throw new RuntimeException("Failed to load icon index", e);
        }
    }

    /**
     * Attempts to match the equipment name to a known icon name using substring matching.
     * Returns the matched icon URL path or a default.
     */
    public String resolveIcon(String rawEquipmentName) {
        String normalized = rawEquipmentName.toLowerCase();

        return iconNames.stream()
                .filter(normalized::contains)
                .map(icon -> ICON_PATH_PREFIX + icon + ".svg")
                .findFirst()
                .orElse(DEFAULT_ICON);
    }
}
