package model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class HistoryContent {
    private String id;
    private String className;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private String actor;
    private String methodName;
    private Map<String, Object> object;
    private Status status;

    // Константа для значения actor по умолчанию
    public static final String DEFAULT_ACTOR = "system";
    public HistoryContent() {}

    public HistoryContent(
            String id,
            String className,
            LocalDateTime createdDate,
            String actor,
            String methodName,
            Map<String, Object> object,
            Status status
    ) {
        this.id = id;
        this.className = className;
        this.createdDate = createdDate;
        this.actor = actor;
        this.methodName = methodName;
        this.object = object;
        this.status = status;
    }


    // Конструктор со значением по умолчанию для actor
    public HistoryContent(String className, String methodName, Map<String, Object> object, Status status) {
        this.id = UUID.randomUUID().toString(); // Генерация ID в строковом формате
        this.className = className;
        this.createdDate = LocalDateTime.now();
        this.actor = DEFAULT_ACTOR; // Значение по умолчанию
        this.methodName = methodName;
        this.object = object;
        this.status = status;
    }

    // Конструктор с возможностью указать actor
    public HistoryContent(String className, String actor, String methodName, Map<String, Object> object, Status status) {
        this.id = UUID.randomUUID().toString();
        this.className = className;
        this.createdDate = LocalDateTime.now();
        this.actor = actor != null ? actor : DEFAULT_ACTOR;
        this.methodName = methodName;
        this.object = object;
        this.status = status;
    }

    public String getId() {return id;}
    public String getClassName() {return className;}
    public LocalDateTime getCreatedDate() {return createdDate;}
    public String getActor() {return actor;}
    public String getMethodName() {return methodName;}
    public Map<String, Object> getObject() {return object;}
    public Status getStatus() {return status;}
    /*public void setId(String id) {this.id = id;}
    public void setClassName(String className) {this.className = className;}
    public void setCreatedDate(LocalDateTime createdDate) {this.createdDate = createdDate;}
    public void setActor(String actor) {this.actor = actor;}
    public void setMethodName(String methodName) {this.methodName = methodName;}
    public void setObject(Map<String, Object> object) {this.object = object;}
    public void setStatus(Status status) {this.status = status;}*/



    @Override
    public String toString() {
        return "HistoryContent{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                ", createdDate=" + createdDate +
                ", actor='" + actor + '\'' +
                ", methodName='" + methodName + '\'' +
                ", object=" + object +
                ", status=" + status +
                '}';
    }
}

