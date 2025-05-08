package mil.llm.store;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entity_type")
public class EntityType extends PanacheEntityBase {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for auto-increment
    public Long id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "code", nullable = false)
    public String code;

    @Column(name = "symbol_set", nullable = false)
    public String symbolSet;

    @Override
    public String toString() {
        return "EntityType [id=" + id + ", name=" + name + ", code=" + code + ", symbolSet=" + symbolSet + "]";
    }

    
}
