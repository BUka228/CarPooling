package com.carpooling.dao.xml;


import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.carpooling.constants.ErrorMessages.ERROR_DELETE_RECORD;
import static com.carpooling.constants.ErrorMessages.ERROR_INIT_FILE;


/**
 * Абстрактный класс для работы с XML-файлами.
 *
 * @param <T> Тип сущности (например, TripRecord, UserRecord и т.д.).
 * @param <W> Тип обертки (Wrapper) для списка сущностей.
 */
public abstract class AbstractXmlDao<T, W> {

    private final String filePath;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    /**
     * Конструктор.
     *
     * @param type        Класс сущности (например, TripRecord.class).
     * @param wrapperType Класс обертки (например, TripWrapper.class).
     * @param filePath    Путь к XML-файлу.
     */
    public AbstractXmlDao(Class<T> type, Class<W> wrapperType, String filePath) {
        this.filePath = filePath;
        try {
            // Инициализация JAXB контекста
            JAXBContext context = JAXBContext.newInstance(wrapperType, type);
            this.marshaller = context.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            this.unmarshaller = context.createUnmarshaller();

            // Инициализация файла (после инициализации marshaller и unmarshaller)
            initializeFile();
        } catch (JAXBException e) {
            throw new RuntimeException(ERROR_INIT_FILE, e);
        }
    }

    /**
     * Создает файл по указанному пути, если он не существует.
     * Если файл создается, он инициализируется пустой оберткой.
     *
     * @throws RuntimeException Если произошла ошибка при создании файла или записи данных.
     */
    private void initializeFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                // Инициализируем файл пустой оберткой, используя метод writeAll
                writeAll(new ArrayList<>());
            } catch (JAXBException e) {
                throw new RuntimeException(ERROR_INIT_FILE, e);
            }
        }
    }


    /**
     * Читает все записи из XML-файла.
     *
     * @return Список записей.
     * @throws JAXBException Если произошла ошибка при чтении XML.
     */
    protected List<T> readAll() throws JAXBException {
        File file = new File(filePath);
        W wrapper = (W) unmarshaller.unmarshal(file);
        List<T> items = getItemsFromWrapper(wrapper);
        return items == null ? new ArrayList<>() : items;
    }

    /**
     * Записывает все записи в XML-файл.
     *
     * @param items Список записей.
     * @throws JAXBException Если произошла ошибка при записи XML.
     */
    protected void writeAll(List<T> items) throws JAXBException {
        W wrapper = createWrapper(items);
        marshaller.marshal(wrapper, new File(filePath));
    }

    /**
     * Находит запись по условию.
     *
     * @param predicate Условие для поиска.
     * @return Найденная запись или Optional.empty().
     * @throws JAXBException Если произошла ошибка при чтении XML.
     */
    protected Optional<T> findById(Predicate<T> predicate) throws JAXBException {
        List<T> items = readAll();
        if (items == null) {
            return Optional.empty();
        }
        return items.stream().filter(predicate).findFirst();
    }

    /**
     * Удаляет запись по условию.
     *
     * @param predicate Условие для удаления.
     * @return true, если запись была удалена, false, если запись не найдена.
     * @throws JAXBException Если произошла ошибка при записи XML.
     */
    protected boolean deleteById(Predicate<T> predicate) throws JAXBException {
        List<T> items = readAll();
        List<T> updatedItems = new ArrayList<>();
        boolean removed = false;

        for (T item : items) {
            if (!predicate.test(item)) {
                updatedItems.add(item);
            } else {
                removed = true;
            }
        }

        if (removed) {
            writeAll(updatedItems);
        }
        return removed;
    }

    /**
     * Обновляет запись по условию.
     *
     * @param predicate Условие для поиска записи.
     * @param newItem   Новая запись для замены существующей.
     * @return true, если запись была обновлена, false, если запись не найдена.
     * @throws JAXBException Если произошла ошибка при чтении или записи XML.
     */
    protected boolean updateItem(Predicate<T> predicate, T newItem) throws JAXBException {
        List<T> items = readAll();
        boolean updated = false;

        for (int i = 0; i < items.size(); i++) {
            if (predicate.test(items.get(i))) {
                items.set(i, newItem);
                updated = true;
                break; // Предполагаем, что запись уникальна по предикату
            }
        }

        if (updated) {
            writeAll(items);
        }
        return updated;
    }

    /**
     * Генерирует уникальный ID.
     *
     * @return Уникальный ID.
     */
    protected UUID generateId() {
        return UUID.randomUUID();
    }

    /**
     * Получает список сущностей из обертки.
     *
     * @param wrapper Обертка.
     * @return Список сущностей.
     */
    protected abstract List<T> getItemsFromWrapper(W wrapper);

    /**
     * Создает обертку для списка сущностей.
     *
     * @param items Список сущностей.
     * @return Обертка.
     */
    protected abstract W createWrapper(List<T> items);
}

//ПРИМЕР
//<trips>
//    <trip>
//        <id>trip-1</id>
//        <userId>user-1</userId>
//        <routeId>route-1</routeId>
//        <departureTime>2023-10-01T12:00:00</departureTime>
//        <maxPassengers>4</maxPassengers>
//        <creationDate>2023-09-25T10:00:00</creationDate>
//        <status>planned</status>
//        <editable>true</editable>
//    </trip>
//    <trip>
//        <id>trip-2</id>
//        <userId>user-2</userId>
//        <routeId>route-2</routeId>
//        <departureTime>2023-10-02T14:00:00</departureTime>
//        <maxPassengers>6</maxPassengers>
//        <creationDate>2023-09-26T11:00:00</creationDate>
//        <status>completed</status>
//        <editable>false</editable>
//    </trip>
//</trips>