package providers;



import java.util.List;


public interface IDataProvider<T> {
    void initDataSource(String filePathOrDb) throws Exception;
    void saveRecord(T record) throws Exception;
    void deleteRecord(long id) throws Exception;
    T getRecordById(long id) throws Exception;
    List<T> getAllRecords() throws Exception;
}

