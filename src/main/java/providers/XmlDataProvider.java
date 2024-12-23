package providers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class XmlDataProvider<T> implements IDataProvider<T> {
    private Path filePath;
    private final Class<T> type;
    private final XmlMapper xmlMapper = new XmlMapper();;

    public XmlDataProvider(Class<T> type) {
        this.type = type;
    }

    @Override
    public void initDataSource(String filePathOrDb) {
        try {
            this.filePath = Paths.get(filePathOrDb);
            if (!Files.exists(this.filePath)) {
                Files.createFile(this.filePath);
                xmlMapper.writeValue(Files.newBufferedWriter(this.filePath), new ArrayList<T>());
            }
        } catch (Exception e) {
            log.error("Ошибка при инициализации источника данных", e);
            throw new DataProviderException("Не удалось инициализировать источник данных", e);
        }
    }

    @Override
    public void saveRecord(T record) {
        try {
            List<T> records = getAllRecords();
            records.removeIf(existing -> existing.equals(record)); // Удаляем старую версию, если есть
            records.add(record);
            xmlMapper.writeValue(Files.newBufferedWriter(filePath), records);
        } catch (Exception e) {
            log.error("Ошибка при сохранении записи: {}", record, e);
            throw new DataProviderException("Не удалось сохранить запись", e);
        }
    }

    @Override
    public void deleteRecord(long id) {
        try {
            List<T> records = getAllRecords().stream()
                    .filter(record -> !record.toString().contains(Long.toString(id)))
                    .collect(Collectors.toList());
            xmlMapper.writeValue(Files.newBufferedWriter(filePath), records);
        } catch (Exception e) {
            log.error("Ошибка при удалении записи с ID: {}", id, e);
            throw new DataProviderException("Не удалось удалить запись", e);
        }
    }

    @Override
    public T getRecordById(long id) {
        try {
            return getAllRecords().stream()
                    .filter(record -> record.toString().contains(Long.toString(id)))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.error("Ошибка при получении записи с ID: {}", id, e);
            throw new DataProviderException("Не удалось получить запись", e);
        }
    }

    @Override
    public List<T> getAllRecords() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0) {
                return new ArrayList<>();
            }
            return xmlMapper.readValue(Files.newBufferedReader(filePath),
                    xmlMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (Exception e) {
            log.error("Ошибка при чтении всех записей", e);
            throw new DataProviderException("Не удалось прочитать все записи", e);
        }
    }
}
