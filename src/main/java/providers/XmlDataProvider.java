package providers;

import converters.GenericConverter;
import exceptions.DataProviderException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class XmlDataProvider<T> implements IDataProvider<T> {
    private final File xmlFile;
    private final GenericConverter<T, String> converter;

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
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при сохранении записи в XML", e);
        }
    }

    @Override
    public T getRecordById(String id) {
        return getAllRecords().stream()
                .filter(r -> id.equals(converter.getId(r)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> getAllRecords() {
        try {
            if (!xmlFile.exists()) {
                return new ArrayList<>();
            }
            String xmlContent = Files.readString(xmlFile.toPath());
            return (List<T>) converter.deserialize(xmlContent);
        } catch (Exception e) {
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
            }
        } catch (Exception e) {
            throw new DataProviderException("Ошибка при инициализации XML файла", e);
        }
    }
}
