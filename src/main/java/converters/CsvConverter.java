package converters;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import exceptions.ConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class CsvConverter<T> implements GenericConverter<T, String[]> {
    private final Class<T> type;
    private static final Logger log = LoggerFactory.getLogger(CsvConverter.class);

    public CsvConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public String[] serialize(T entity) {
        if (entity == null) {
            throw new ConverterException("Ошибка при сериализации объекта в CSV: объект равен null");
        }
        try {
            StringWriter writer = new StringWriter();
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false) // Не добавлять кавычки вокруг значений
                    .build();

            beanToCsv.write(entity);
            String csvLine = writer.toString().split("\n")[0];
            return csvLine.split(",");
        } catch (CsvException | RuntimeException e) {
            throw new ConverterException("Ошибка при сериализации объекта в CSV", e);
        }
    }

    @Override
    public T deserialize(String[] csvData) {
        if (csvData == null || csvData.length == 0) {
            throw new ConverterException("Не удалось десериализовать CSV: пустые данные");
        }

        try {
            String csvLine = String.join(",", csvData);
            Reader reader = new StringReader(csvLine);

            List<T> beans = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            if (beans.isEmpty()) {
                throw new ConverterException("Не удалось десериализовать CSV: пустые данные");
            }

            return beans.get(0);
        } catch (Exception e) {
            throw new ConverterException("Ошибка при десериализации CSV в объект", e);
        }
    }

    @Override
    public String getId(T entity) {
        try {
            // Предполагается, что объект имеет поле id
            return String.valueOf(entity.hashCode()); // Или изменить на реальную логику получения ID
        } catch (Exception e) {
            throw new ConverterException("Ошибка при получении ID из объекта", e);
        }
    }
}
