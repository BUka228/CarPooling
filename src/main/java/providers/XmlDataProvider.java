package providers;

import converters.GenericConverter;
import exceptions.DataProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class XmlDataProvider<T> implements IDataProvider<T> {
    private final File xmlFile;
    private final GenericConverter<T, String> converter;
    private static final Logger log = LoggerFactory.getLogger(XmlDataProvider.class);

    public XmlDataProvider(String filePath, GenericConverter<T, String> converter) {
        this.xmlFile = new File(filePath);
        this.converter = converter;
    }

    @Override
    public void saveRecord(T record) {
        try {
            List<T> records = getAllRecords();
            records.add(record);
            String xml = converter.serialize((T) records); // Преобразуем список в XML
            Files.writeString(xmlFile.toPath(), xml);
            log.info("Запись с ID {} успешно сохранена в XML", converter.getId(record));
        } catch (Exception e) {
            log.error("Ошибка при сохранении записи в XML: {}", e.getMessage(), e);
            throw new DataProviderException("Ошибка при сохранении записи в XML", e);
        }
    }

    @Override
    public T getRecordById(String id) {
        try {
            T record = getAllRecords().stream()
                    .filter(r -> id.equals(converter.getId(r)))
                    .findFirst()
                    .orElse(null);
            if (record != null) {
                log.info("Запись с ID {} найдена в XML", id);
            } else {
                log.warn("Запись с ID {} не найдена в XML", id);
            }
            return record;
        } catch (Exception e) {
            log.error("Ошибка при поиске записи с ID {}: {}", id, e.getMessage(), e);
            throw new DataProviderException("Ошибка при поиске записи с ID: " + id, e);
        }
    }

    @Override
    public List<T> getAllRecords() {
        try {
            if (!xmlFile.exists()) {
                log.warn("Файл XML {} не найден, возвращён пустой список", xmlFile.getAbsolutePath());
                return new ArrayList<>();
            }
            String xmlContent = Files.readString(xmlFile.toPath());
            List<T> records = (List<T>) converter.deserialize(xmlContent);
            log.info("Получено {} записей из XML файла {}", records.size(), xmlFile.getAbsolutePath());
            return records;
        } catch (Exception e) {
            log.error("Ошибка при чтении XML файла {}: {}", xmlFile.getAbsolutePath(), e.getMessage(), e);
            throw new DataProviderException("Ошибка при чтении XML", e);
        }
    }

    @Override
    public void deleteRecord(T record) {
        throw new UnsupportedOperationException("Удаление записей из XML не поддерживается");
    }

    @Override
    public void initDataSource() {
        try {
            if (!xmlFile.exists()) {
                xmlFile.createNewFile();
                log.info("XML файл {} был создан", xmlFile.getAbsolutePath());
            } else {
                log.info("XML файл {} уже существует", xmlFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Ошибка при инициализации XML файла {}: {}", xmlFile.getAbsolutePath(), e.getMessage(), e);
            throw new DataProviderException("Ошибка при инициализации XML файла", e);
        }
    }
}
