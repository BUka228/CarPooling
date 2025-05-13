package collections.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "showcase_entities")

public class ShowcaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", nullable = false)
    private String name;

    // 1. Коллекция Set<String>
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "showcase_tags", // Имя таблицы для хранения тегов
            joinColumns = @JoinColumn(name = "showcase_entity_id") // Внешний ключ на ShowcaseEntity
    )
    @Column(name = "tag_value", nullable = false) // Колонка для хранения самих тегов
    private Set<String> tags = new HashSet<>();

    // 2. Коллекция List<String> с сохранением порядка
    @ElementCollection
    @CollectionTable(
            name = "showcase_remarks",
            joinColumns = @JoinColumn(name = "showcase_entity_id")
    )
    @OrderColumn(name = "remark_order") // Колонка для хранения индекса (порядка)
    @Column(name = "remark_text", nullable = false)
    private List<String> remarks = new ArrayList<>();

    // 3. Коллекция Map<String, String>
    @ElementCollection
    @CollectionTable(
            name = "showcase_properties",
            joinColumns = @JoinColumn(name = "showcase_entity_id")
    )
    @MapKeyColumn(name = "property_key", length = 100) // Колонка для ключей мапы
    @Column(name = "property_value", length = 255)    // Колонка для значений мапы
    private Map<String, String> properties = new HashMap<>();

    // 4. Коллекция Set<AttributeEmbeddable> (компонентов)
    @ElementCollection
    @CollectionTable(
            name = "showcase_attributes",
            joinColumns = @JoinColumn(name = "showcase_entity_id")
    )
    private Set<AttributeEmbeddable> attributes = new HashSet<>();

    // 5. Коллекция List<AttributeEmbeddable> с сохранением порядка
    @ElementCollection
    @CollectionTable(
            name = "showcase_ordered_attributes",
            joinColumns = @JoinColumn(name = "showcase_entity_id")
    )
    @OrderColumn(name = "ordered_attr_idx")
    private List<AttributeEmbeddable> orderedAttributes = new ArrayList<>();

    public ShowcaseEntity(String name) {
        this.name = name;
    }

    // Методы-хелперы для удобной работы с коллекциями
    public void addTag(String tag) {
        this.tags.add(tag);
    }
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    public void addRemark(String remark) {
        this.remarks.add(remark);
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public void addAttribute(AttributeEmbeddable attribute) {
        this.attributes.add(attribute);
    }
    public void addOrderedAttribute(AttributeEmbeddable attribute) {
        this.orderedAttributes.add(attribute);
    }
}