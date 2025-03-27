package com.carpooling.dao.csv;

import com.carpooling.exceptions.dao.DataAccessException;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.carpooling.constants.ErrorMessages.ERROR_INIT_FILE;

@Slf4j
public abstract class AbstractCsvDao<T> {

    private final Class<T> type;
    private final String filePath;

    public AbstractCsvDao(Class<T> type, String filePath) {
        this.type = type;
        this.filePath = filePath;
        try {
            log.debug("Initializing CSV DAO for type {} with file path: {}", type.getSimpleName(), filePath);
            initializeFile(); // Инициализация файла
        } catch (IOException e) { // Ловим ошибку из initializeFile
            log.error("Failed to initialize CSV file {}: {}", filePath, e.getMessage(), e);
            // Оборачиваем в DataAccessException, как было раньше
            throw new DataAccessException(ERROR_INIT_FILE, e);
        }
    }

    /**
     * Инициализирует файл, если он не существует, создавая необходимые директории.
     * @throws IOException Если не удалось создать директории или файл.
     */
    private void initializeFile() throws IOException { // Теперь бросает IOException
        File file = new File(filePath);
        if (!file.exists()) {
            log.info("CSV file not found, attempting to create: {}", filePath);
            // --- ИЗМЕНЕНИЕ: Создаем родительские директории ---
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                log.debug("Creating parent directories for: {}", parentDir.getAbsolutePath());
                if (!parentDir.mkdirs()) {
                    throw new IOException("Could not create parent directories for: " + parentDir.getAbsolutePath());
                }
                log.info("Parent directories created successfully.");
            }
            // --- КОНЕЦ ИЗМЕНЕНИЯ ---

            // Пытаемся создать пустой файл
            if (file.createNewFile()) {
                log.info("CSV file created successfully: {}", filePath);
                // Можно опционально записать заголовок сразу, если нужно
                // writeHeaderIfNeeded(file);
            } else {
                // Это маловероятно после проверки exists, но на всякий случай
                throw new IOException("Failed to create CSV file (unknown reason): " + filePath);
            }
        } else {
            log.debug("CSV file already exists: {}", filePath);
        }
    }

    /**
     * Читает все записи из CSV-файла.
     *
     * @return Список записей.
     * @throws IOException Если произошла ошибка при чтении файла.
     */
    protected List<T> readAll() throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead() || file.length() == 0) {
            log.warn("CSV file is missing, not readable, or empty. Returning empty list: {}", filePath);
            return new ArrayList<>(); // Возвращаем пустой список
        }
        // Используем try-with-resources для FileReader и CSVReader
        try (FileReader fileReader = new FileReader(filePath);
             CSVReader reader = new CSVReader(fileReader)) {

            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(type);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy) // Используем стратегию
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true) // Игнорировать пробелы в начале
                    .withThrowExceptions(false) // Не бросать исключения парсинга сразу, а собирать их
                    .build();

            List<T> result = csvToBean.parse();

            // Логируем ошибки парсинга, если были
            List<CsvException> exceptions = csvToBean.getCapturedExceptions();
            if (!exceptions.isEmpty()) {
                log.warn("Encountered {} parsing exceptions in CSV file {}:", exceptions.size(), filePath);
                for (CsvException e : exceptions) {
                    log.warn(" - Line {}: {}", e.getLineNumber(), e.getMessage());
                }
            }

            log.trace("Read {} valid items from {}", (result != null ? result.size() : 0), filePath);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            log.error("IOException during CSV read operation for {}: {}", filePath, e.getMessage());
            throw e; // Перебрасываем IOException
        } catch (Exception e) { // Ловим другие RuntimeException от CsvToBean, если withThrowExceptions(true)
            log.error("Unexpected error during CSV parsing for {}: {}", filePath, e.getMessage(), e);
            // Оборачиваем в IOException или DataAccessException
            throw new IOException("Failed to parse CSV file: " + filePath, e);
        }
    }

    /**
     * Записывает все записи в CSV-файл.
     *
     * @param items Список записей.
     * @throws IOException Если произошла ошибка при записи файла.
     */
    protected void writeAll(List<T> items) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (Writer writer = new FileWriter(filePath)) { // FileWriter перезаписывает файл
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(type);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .withApplyQuotesToAll(false) // Не заключать все поля в кавычки без необходимости
                    .build();
            beanToCsv.write(items);
            log.trace("Wrote {} items to {}", (items != null ? items.size() : 0), filePath);
        } catch (IOException e) {
            log.error("IOException during CSV write operation for {}: {}", filePath, e.getMessage());
            throw e;
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            log.error("CSV writing error for {}: {}", filePath, e.getMessage());
            throw e;
        } catch (Exception e) { // Ловим другие RuntimeException
            log.error("Unexpected error during CSV writing for {}: {}", filePath, e.getMessage(), e);
            throw new IOException("Failed to write CSV file: " + filePath, e);
        }
    }


    protected Optional<T> findById(Predicate<T> predicate) throws IOException {
        List<T> items = readAll();
        return items.stream().filter(predicate).findFirst();
    }

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

    protected boolean updateItem(Predicate<T> predicate, T updatedItem) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        List<T> items = readAll();
        boolean updated = false;
        for (int i = 0; i < items.size(); i++) {
            if (predicate.test(items.get(i))) {
                items.set(i, updatedItem);
                updated = true;
                break;
            }
        }
        if (updated) {
            writeAll(items);
        }
        return updated;
    }

    protected UUID generateId() {
        return UUID.randomUUID();
    }
}