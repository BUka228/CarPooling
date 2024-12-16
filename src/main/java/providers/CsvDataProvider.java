package providers;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import converters.GenericConverter;
import exceptions.DataProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvDataProvider<T> implements IDataProvider<T> {
    private final String filePath;
    private final GenericConverter<T, String[]> converter;
    private static final Logger log = LoggerFactory.getLogger(CsvDataProvider.class);

    public CsvDataProvider(String filePath, GenericConverter<T, String[]> converter) {
        this.filePath = filePath;
        this.converter = converter;
    }

    @Override
    public void saveRecord(T record) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            String[] recordData = converter.serialize(record);
            writer.writeNext(recordData);
            log.info("Запись с ID {} успешно сохранена в CSV", converter.getId(record));
        } catch (Exception e) {
            log.error("Ошибка при сохранении записи в CSV: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при сохранении записи в CSV", e);
        }
    }

    @Override
    public void deleteRecord(T record) {
        // Удаление записей из CSV не поддерживается
        throw new UnsupportedOperationException("Удаление записей из CSV не поддерживается");
    }

    @Override
    public T getRecordById(String id) {
        try {
            T record = getAllRecords().stream()
                    .filter(r -> id.equals(converter.getId(r)))
                    .findFirst()
                    .orElseThrow(() -> new DataProviderException("Запись с ID " + id + " не найдена"));
            log.info("Запись с ID {} найдена в CSV", id);
            return record;
        } catch (Exception e) {
            log.error("Ошибка при поиске записи с ID {}: {}", id, e.getMessage(), e);
            throw new DataProviderException("Ошибка при поиске записи с ID: " + id, e);
        }
    }

    @Override
    public List<T> getAllRecords() {
        List<T> records = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                records.add(converter.deserialize(line));
            }
            log.info("Получено {} записей из CSV", records.size());
        } catch (Exception e) {
            log.error("Ошибка при чтении записей из CSV: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при чтении записей из CSV", e);
        }
        return records;
    }

    @Override
    public void initDataSource() {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                log.info("CSV файл {} был создан", filePath);
            } else {
                log.info("CSV файл {} уже существует", filePath);
            }
        } catch (Exception e) {
            log.error("Ошибка при инициализации CSV файла: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при инициализации CSV файла", e);
        }
    }
}
