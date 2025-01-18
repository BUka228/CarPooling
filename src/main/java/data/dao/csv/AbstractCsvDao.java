package data.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import exceptions.dao.DataAccessException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.man.constant.ErrorMessages.ERROR_DELETE_RECORD;
import static com.man.constant.ErrorMessages.ERROR_INIT_FILE;

/**
 * Абстрактный класс для работы с CSV файлами.
 * @param <T> Тип данных, которые хранятся в CSV файле.
 */
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
            return csvToBean.parse();
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
        if (items == null) {
            return Optional.empty();
        }
        return items.stream().filter(predicate).findFirst();
    }

    /**
     * Удаляет запись по ID.
     *
     * @param predicate Условие для удаления.
     * @throws IOException Если произошла ошибка при записи файла.
     */
    protected void deleteById(Predicate<T> predicate) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        List<T> items = readAll();
        List<T> updatedItems = new ArrayList<>();
        for (T item : items) {
            if (!predicate.test(item)) {
                updatedItems.add(item);
            }
        }
        if (items.size() == updatedItems.size())
            throw new IOException(ERROR_DELETE_RECORD);
        writeAll(updatedItems);
    }

    /**
     * Генерация уникального ID.
     *
     * @return Уникальный ID.
     */
    protected String generateId() {
        return UUID.randomUUID().toString();
    }
}
