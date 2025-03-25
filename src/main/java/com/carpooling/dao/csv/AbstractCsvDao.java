package com.carpooling.dao.csv;

import com.carpooling.exceptions.dao.DataAccessException;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.carpooling.constants.ErrorMessages.ERROR_DELETE_RECORD;
import static com.carpooling.constants.ErrorMessages.ERROR_INIT_FILE;

/**
 * Абстрактный класс для работы с CSV файлами.
 * @param <T> Тип данных, которые хранятся в CSV файле.
 */
@Slf4j
public abstract class AbstractCsvDao<T> {

    private final Class<T> type;
    private final String filePath;

    /**
     * Конструктор класса.
     * @param type Класс данных, которые хранятся в CSV файле.
     * @param filePath Путь к CSV файлу.
     */
    public AbstractCsvDao(Class<T> type, String filePath) {
        this.type = type;
        this.filePath = filePath;
        initializeFile();
    }

    /**
     * Инициализирует файл, если он не существует.
     */
    private void initializeFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile(); // Создаем пустой файл
            } catch (IOException e) {
                throw new DataAccessException(ERROR_INIT_FILE, e);
            }
        }
    }

    /**
     * Читает все записи из CSV-файла.
     *
     * @return Список записей.
     * @throws IOException Если произошла ошибка при чтении файла.
     */
    protected List<T> readAll() throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .build();
            List<T> result = csvToBean.parse();
            return result != null ? result : new ArrayList<>();
        }
    }

    /**
     * Записывает все записи в CSV-файл.
     *
     * @param items Список записей.
     * @throws IOException Если произошла ошибка при записи файла.
     */
    protected void writeAll(List<T> items) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (Writer writer = new FileWriter(filePath)) {
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(type);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(items);
        }
    }

    /**
     * Находит запись по ID.
     *
     * @param predicate Условие для поиска.
     * @return Найденная запись или Optional.empty().
     * @throws IOException Если произошла ошибка при чтении файла.
     */
    protected Optional<T> findById(Predicate<T> predicate) throws IOException {
        List<T> items = readAll();
        return items.stream().filter(predicate).findFirst();
    }

    /**
     * Удаляет запись по условию.
     *
     * @param predicate Условие для удаления.
     * @return true, если запись была удалена, false, если запись не найдена.
     * @throws IOException Если произошла ошибка при записи файла.
     * @throws CsvDataTypeMismatchException Если произошла ошибка при преобразовании данных.
     * @throws CsvRequiredFieldEmptyException Если обязательное поле пустое.
     */
    protected boolean deleteById(Predicate<T> predicate) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
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
     * @param updatedItem Новый объект для замены существующей записи.
     * @return true, если запись была обновлена, false, если запись не найдена.
     * @throws IOException Если произошла ошибка при чтении файла.
     * @throws CsvDataTypeMismatchException Если произошла ошибка при преобразовании данных.
     * @throws CsvRequiredFieldEmptyException Если обязательное поле пустое.
     */
    protected boolean updateItem(Predicate<T> predicate, T updatedItem) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        List<T> items = readAll();
        boolean updated = false;

        for (int i = 0; i < items.size(); i++) {
            if (predicate.test(items.get(i))) {
                items.set(i, updatedItem);
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
     * Генерация уникального ID.
     *
     * @return Уникальный ID.
     */
    protected UUID generateId() {
        return UUID.randomUUID();
    }
}