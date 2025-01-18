package presentation.context;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CliContext {

    public enum StorageType {
        XML, CSV, MONGO, POSTGRES
    }

    @Getter
    private static String currentUserId;
    @Getter
    private static String currentTripId;
    @Getter
    private static String currentBookingId;
    @Getter
    private static String currentRouteId;
    @Getter
    private static String currentRatingId;
    @Getter
    private static List<String> tripHistory = new ArrayList<>();
    @Getter
    private static Map<String, String> userPreferences = new HashMap<>();
    @Getter
    private static Map<String, String> searchState = new HashMap<>();
    @Getter
    private static StorageType currentStorageType = StorageType.POSTGRES; // По умолчанию


    public static void setCurrentUserId(String currentUserId) {
        CliContext.currentUserId = currentUserId;
    }

    public static void setCurrentTripId(String currentTripId) {
        CliContext.currentTripId = currentTripId;
    }

    public static void setCurrentBookingId(String currentBookingId) {
        CliContext.currentBookingId = currentBookingId;
    }

    public static void setCurrentRouteId(String currentRouteId) {
        CliContext.currentRouteId = currentRouteId;
    }

    public static void setCurrentRatingId(String currentRatingId) {
        CliContext.currentRatingId = currentRatingId;
    }

    public static void setTripHistory(List<String> tripHistory) {
        CliContext.tripHistory = tripHistory;
    }

    public static void setUserPreferences(Map<String, String> userPreferences) {
        CliContext.userPreferences = userPreferences;
    }

    public static void setSearchState(Map<String, String> searchState) {
        CliContext.searchState = searchState;
    }

    public static void setCurrentStorageType(StorageType currentStorageType) {
        CliContext.currentStorageType = currentStorageType;
    }
}