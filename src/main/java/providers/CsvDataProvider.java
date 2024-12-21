package providers;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class CsvDataProvider<T> implements IDataProvider<T> {
    private Path filePath;
    private final Class<T> type;

    public CsvDataProvider(Class<T> type) {
        this.type = type;
    }

    @Override
    public void initDataSource(String filePathOrDb) {
        try {
            this.filePath = Paths.get(filePathOrDb);
            if (!Files.exists(this.filePath)) {
                Files.createFile(this.filePath);
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
            records.remove(record);
            records.add(record);
            writeRecords(records);
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
            writeRecords(records);
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
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                        .withType(type)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                return csvToBean.parse();
            }
        } catch (Exception e) {
            log.error("Ошибка при чтении всех записей", e);
            throw new DataProviderException("Не удалось прочитать все записи", e);
        }
    }

    private void writeRecords(List<T> records) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).build();
            beanToCsv.write(records);
        } catch (Exception e) {
            log.error("Ошибка при записи данных", e);
            throw new DataProviderException("Не удалось записать данные", e);
        }
    }
}
