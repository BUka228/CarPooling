package collections;

import collections.model.AttributeEmbeddable;
import collections.model.ShowcaseEntity;

import inheritance.common.GenericDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import dao.postgres.HibernateTestUtil; // Убедись, что путь правильный
import com.carpooling.hibernate.ThreadLocalSessionContext; // Убедись, что путь правильный

import jakarta.persistence.PersistenceException; // Для отлова ошибок NOT NULL
import org.hibernate.LazyInitializationException; // Для теста ленивой загрузки

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ShowcaseEntityCollectionsTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private GenericDao<ShowcaseEntity, Long> showcaseEntityDao;

    @BeforeAll
    static void setUpFactory() {
        sessionFactory = HibernateTestUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDownFactory() {
        // HibernateTestUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        showcaseEntityDao = new GenericDao<>(sessionFactory, ShowcaseEntity.class);

        session = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(session);
        transaction = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            ThreadLocalSessionContext.unbind();
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private ShowcaseEntity createAndSaveShowcaseEntity(String name) {
        ShowcaseEntity entity = new ShowcaseEntity(name);
        return showcaseEntityDao.save(entity);
    }

    @Test
    @DisplayName("Save and Load ShowcaseEntity with Empty Collections")
    void saveAndLoad_EmptyCollections_Success() {
        ShowcaseEntity entity = createAndSaveShowcaseEntity("EmptyCollectionsEntity");
        session.flush();
        Long entityId = entity.getId();
        session.clear();

        Optional<ShowcaseEntity> foundOpt = showcaseEntityDao.findById(entityId);
        assertThat(foundOpt).isPresent();
        ShowcaseEntity found = foundOpt.get();

        assertThat(found.getName()).isEqualTo("EmptyCollectionsEntity");
        assertThat(found.getTags()).isNotNull().isEmpty();
        assertThat(found.getRemarks()).isNotNull().isEmpty();
        assertThat(found.getProperties()).isNotNull().isEmpty();
        assertThat(found.getAttributes()).isNotNull().isEmpty();
        assertThat(found.getOrderedAttributes()).isNotNull().isEmpty();
    }

    @Nested
    @DisplayName("Set<String> Tags Collection Tests")
    class TagsCollectionTests {
        @Test
        void addAndLoadTags_Success() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("TagsEntity");
            entity.addTag("java");
            entity.addTag("hibernate");
            entity.addTag("test");
            showcaseEntityDao.update(entity); // merge
            session.flush();
            Long entityId = entity.getId();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entityId).orElseThrow();
            assertThat(found.getTags()).isNotNull().hasSize(3)
                    .containsExactlyInAnyOrder("java", "hibernate", "test");
        }

        @Test
        void addDuplicateTag_ShouldNotIncreaseSize() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("DuplicateTagEntity");
            entity.addTag("unique");
            entity.addTag("unique"); // Дубликат
            showcaseEntityDao.update(entity);
            session.flush();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            assertThat(found.getTags()).isNotNull().hasSize(1).contains("unique");
        }

        @Test
        void removeTag_Success() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("RemoveTagEntity");
            entity.addTag("to_keep");
            entity.addTag("to_remove");
            showcaseEntityDao.update(entity);
            session.flush();
            session.clear();

            ShowcaseEntity entityToModify = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            entityToModify.removeTag("to_remove"); // Используем свой метод
            showcaseEntityDao.update(entityToModify);
            session.flush();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            assertThat(found.getTags()).isNotNull().hasSize(1).containsExactly("to_keep");
        }

    }

    @Nested
    @DisplayName("List<String> Remarks Collection Tests")
    class RemarksCollectionTests {
        @Test
        void addAndLoadRemarks_ShouldPreserveOrder() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("RemarksEntity");
            entity.addRemark("First remark");
            entity.addRemark("Second remark");
            entity.addRemark("Third remark");
            showcaseEntityDao.update(entity);
            session.flush();
            Long entityId = entity.getId();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entityId).orElseThrow();
            assertThat(found.getRemarks()).isNotNull().hasSize(3)
                    .containsExactly("First remark", "Second remark", "Third remark");
        }

        @Test
        void modifyRemarksList_ShouldReflectChanges() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("ModifyRemarksEntity");
            entity.getRemarks().addAll(List.of("A", "B", "C"));
            showcaseEntityDao.update(entity);
            session.flush();
            session.clear();

            ShowcaseEntity entityToModify = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            entityToModify.getRemarks().remove(1); // Remove "B"
            entityToModify.getRemarks().add(1, "B_MODIFIED"); // Insert at index 1
            entityToModify.getRemarks().add("D");

            showcaseEntityDao.update(entityToModify);
            session.flush();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            assertThat(found.getRemarks()).containsExactly("A", "B_MODIFIED", "C", "D");
        }
    }

    @Nested
    @DisplayName("Map<String, String> Properties Collection Tests")
    class PropertiesCollectionTests {
        @Test
        void addAndLoadProperties_Success() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("PropertiesEntity");
            entity.addProperty("color", "blue");
            entity.addProperty("size", "large");
            showcaseEntityDao.update(entity);
            session.flush();
            Long entityId = entity.getId();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entityId).orElseThrow();
            assertThat(found.getProperties()).isNotNull().hasSize(2)
                    .containsEntry("color", "blue")
                    .containsEntry("size", "large");
        }

        @Test
        void updateProperty_ShouldChangeValue() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("UpdatePropEntity");
            entity.addProperty("key1", "value1");
            showcaseEntityDao.update(entity);
            session.flush();
            session.clear();

            ShowcaseEntity entityToModify = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            entityToModify.addProperty("key1", "value1_updated"); // Обновление по тому же ключу
            entityToModify.addProperty("key2", "value2"); // Добавление новой
            showcaseEntityDao.update(entityToModify);
            session.flush();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entity.getId()).orElseThrow();
            assertThat(found.getProperties())
                    .containsEntry("key1", "value1_updated")
                    .containsEntry("key2", "value2");
        }
    }

    @Nested
    @DisplayName("Set<AttributeEmbeddable> Attributes Collection Tests")
    class AttributesCollectionTests {
        @Test
        void addAndLoadAttributes_Success() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("AttributesEntity");
            entity.addAttribute(new AttributeEmbeddable("width", "100px"));
            entity.addAttribute(new AttributeEmbeddable("height", "200px"));
            showcaseEntityDao.update(entity);
            session.flush();
            Long entityId = entity.getId();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entityId).orElseThrow();
            assertThat(found.getAttributes()).isNotNull().hasSize(2)
                    .containsExactlyInAnyOrder(
                            new AttributeEmbeddable("width", "100px"),
                            new AttributeEmbeddable("height", "200px")
                    );
        }

        @Test
        @DisplayName("Save Attribute: Failure (Null name in AttributeEmbeddable with @Column(nullable=false))")
        void saveAttribute_NullNameInComponent_Failure() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("NullAttrNameEntity");

            PersistenceException ex = assertThrows(PersistenceException.class, () -> {
                entity.addAttribute(new AttributeEmbeddable(null, "someValue"));
                showcaseEntityDao.update(entity);
                session.flush();
            });
            // Причина может быть ConstraintViolationException или PropertyValueException
            // в зависимости от того, на каком этапе сработает проверка NOT NULL
            // (на уровне Hibernate или на уровне БД при flush).
            String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
            String causeMessage = ex.getCause() != null && ex.getCause().getMessage() != null ? ex.getCause().getMessage().toLowerCase() : "";
            assertThat(message.contains("attr_name") || causeMessage.contains("attr_name") || message.contains("null property") || causeMessage.contains("null property"))
                    .as("Exception should mention 'attr_name' or null property constraint")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("List<AttributeEmbeddable> OrderedAttributes Collection Tests")
    class OrderedAttributesCollectionTests {
        @Test
        void addAndLoadOrderedAttributes_ShouldPreserveOrder() {
            ShowcaseEntity entity = createAndSaveShowcaseEntity("OrderedAttributesEntity");
            AttributeEmbeddable attr1 = new AttributeEmbeddable("priority", "high");
            AttributeEmbeddable attr2 = new AttributeEmbeddable("status", "active");
            entity.addOrderedAttribute(attr1);
            entity.addOrderedAttribute(attr2);
            showcaseEntityDao.update(entity);
            session.flush();
            Long entityId = entity.getId();
            session.clear();

            ShowcaseEntity found = showcaseEntityDao.findById(entityId).orElseThrow();
            assertThat(found.getOrderedAttributes()).isNotNull().hasSize(2)
                    .containsExactly(attr1, attr2);
        }
    }

    @Test
    @DisplayName("Delete ShowcaseEntity - Should Cascade Delete Collections")
    void deleteShowcaseEntity_ShouldCascadeDeleteCollectionRecords() {
        ShowcaseEntity entity = createAndSaveShowcaseEntity("CascadeDeleteEntity");
        entity.addTag("tag1");
        entity.addRemark("remark1");
        entity.addProperty("propKey1", "propValue1");
        entity.addAttribute(new AttributeEmbeddable("attrName1", "attrValue1"));
        entity.addOrderedAttribute(new AttributeEmbeddable("orderedAttr1", "val1"));

        showcaseEntityDao.update(entity);
        session.flush();
        Long entityId = entity.getId();

        // Проверка, что записи в коллекциях есть (косвенно, через размер)
        // Можно было бы сделать NativeQuery для проверки таблиц коллекций
        session.clear();
        ShowcaseEntity beforeDelete = showcaseEntityDao.findById(entityId).orElseThrow();
        assertThat(beforeDelete.getTags()).isNotEmpty();
        assertThat(beforeDelete.getRemarks()).isNotEmpty();
        assertThat(beforeDelete.getProperties()).isNotEmpty();
        assertThat(beforeDelete.getAttributes()).isNotEmpty();
        assertThat(beforeDelete.getOrderedAttributes()).isNotEmpty();

        // Удаляем родительскую сущность
        showcaseEntityDao.deleteById(entityId);
        session.flush();
        session.clear();

        assertThat(showcaseEntityDao.findById(entityId)).isNotPresent();
    }

    @Test
    @DisplayName("Lazy Loading Test for Tags Collection")
    void lazyLoading_TagsCollection_ShouldThrowExceptionAfterSessionClose() {
        ShowcaseEntity entity = createAndSaveShowcaseEntity("LazyEntity");
        entity.addTag("lazyTag");
        showcaseEntityDao.update(entity);
        session.flush();
        Long entityId = entity.getId();
        // НЕ ДЕЛАЕМ session.clear() здесь

        // Завершаем транзакцию и закрываем сессию
        transaction.commit(); // Коммитим, чтобы данные сохранились
        ThreadLocalSessionContext.unbind();
        session.close();

        // Пытаемся получить сущность в новой сессии, но не инициализируем коллекцию
        Session newSession = sessionFactory.openSession();
        ThreadLocalSessionContext.bind(newSession);
        Transaction newTransaction = newSession.beginTransaction();
        ShowcaseEntity fetchedEntity = showcaseEntityDao.findById(entityId).orElseThrow(); // showcaseEntityDao будет использовать newSession

        // Закрываем новую сессию ДО обращения к ленивой коллекции
        newTransaction.commit();
        ThreadLocalSessionContext.unbind();
        newSession.close();

        // Попытка доступа к ленивой коллекции tags должна вызвать LazyInitializationException
        assertThrows(LazyInitializationException.class, () -> {
            int size = fetchedEntity.getTags().size(); // Обращение к коллекции
            System.out.println("Size if accessed: " + size); // Эта строка не должна выполниться
        });
    }
}