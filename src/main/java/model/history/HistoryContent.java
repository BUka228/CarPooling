package model.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryContent {
    private String id;
    private String className;
    private LocalDateTime createdDate;
    private String actor;
    private String methodName;
    private Map<String, Object> object;
    private Status status;
}

