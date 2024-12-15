package model;

import com.opencsv.bean.CsvBindByPosition;

public class HistoryContentTest {
    @CsvBindByPosition(position = 0)
    private String id;

    @CsvBindByPosition(position = 1)
    private String actor;

    @CsvBindByPosition(position = 2)
    private String action;

    @CsvBindByPosition(position = 3)
    private String content;
    public HistoryContentTest() {}

    public HistoryContentTest(String id, String actor, String action, String content) {
        this.id = id;
        this.actor = actor;
        this.action = action;
        this.content = content;
    }
    public String getId() {
        return id;
    }
    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getContent() {
        return content;
    }


}
