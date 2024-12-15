package providers;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import converters.GenericConverter;
import exceptions.DataProviderException;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvDataProvider<T> implements IDataProvider<T> {
    private final String filePath;
    private final GenericConverter<T, String[]> converter;

    public CsvDataProvider(String filePath, GenericConverter<T, String[]> converter) {
        this.filePath = filePath;
        this.converter = converter;
    }

    @Override
    public void saveRecord(T record) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            writer.writeNext(converter.serialize(record));
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при сохранении записи в CSV", e);
        }
    }

    @Override
    public void deleteRecord(T record) {
        throw new UnsupportedOperationException("Удаление записей из CSV не поддерживается");
    }

    @Override
    public T getRecordById(String id) {
        try {
            return getAllRecords().stream()
                    .filter(record -> id.equals(converter.getId(record)))
                    .findFirst()
                    .orElseThrow(() -> new DataProviderException("Запись с ID " + id + " не найдена"));
        } catch (Exception e) {
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
        } catch (Exception e) {
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
            }
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при инициализации CSV файла", e);
        }
    }
}
