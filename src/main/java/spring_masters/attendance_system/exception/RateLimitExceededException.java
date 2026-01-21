package spring_masters.attendance_system.exception;

public class RateLimitExceededException extends RuntimeException {

    private long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public RateLimitExceededException(String message) {
        super(message);
        this.retryAfterSeconds = 60; // Default 60 seconds
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
