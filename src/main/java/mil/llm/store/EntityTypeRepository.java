package mil.llm.store;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityTypeRepository implements PanacheRepository<EntityType> {

    /**
     * Finds the first occurrence of an entity where the name contains the given
     * word (case-insensitive).
     *
     * @param word the word to search for in the name field
     * @return the first matching EntityType or null if no match is found
     */
    @Tool("Finds Entity Code (6 Digits) and Symbol Set (2 digits) using military unit type extracted from user text")
    public EntityType findByName(String word) {
        EntityType entityType = find("LOWER(name) LIKE LOWER(?1)", "%" + word + "%").firstResult();

        if (entityType == null) {
            entityType = find("LOWER(name) LIKE LOWER(?1)", "%Unspecified%").firstResult();
            if (entityType == null) {
                // Log a warning if "Unspecified" is unexpectedly missing
                Log.warn("EntityType 'Unspecified' not found in the database.");
            }
        }
        Log.infof("Word: %s, EntityType found: %s", word, entityType);
        return entityType;
    }
}
