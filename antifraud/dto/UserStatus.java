package antifraud.dto;

public enum UserStatus {
    DELETED("Deleted successfully!");

    public final String message;

    UserStatus(String message) {
        this.message = message;
    }
}
