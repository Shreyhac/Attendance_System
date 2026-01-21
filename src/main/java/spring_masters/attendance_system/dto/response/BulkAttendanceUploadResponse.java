package spring_masters.attendance_system.dto.response;

import spring_masters.attendance_system.model.entity.Attendance;

import java.util.ArrayList;
import java.util.List;

public class BulkAttendanceUploadResponse {

    private int totalRecords;
    private int successCount;
    private int failureCount;
    private List<Attendance> successfulRecords;
    private List<AttendanceUploadError> failedRecords;
    private long processingTimeMs;

    public BulkAttendanceUploadResponse() {
        this.successfulRecords = new ArrayList<>();
        this.failedRecords = new ArrayList<>();
    }

    public BulkAttendanceUploadResponse(int totalRecords, int successCount, int failureCount,
            List<Attendance> successfulRecords,
            List<AttendanceUploadError> failedRecords,
            long processingTimeMs) {
        this.totalRecords = totalRecords;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.successfulRecords = successfulRecords;
        this.failedRecords = failedRecords;
        this.processingTimeMs = processingTimeMs;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<Attendance> getSuccessfulRecords() {
        return successfulRecords;
    }

    public void setSuccessfulRecords(List<Attendance> successfulRecords) {
        this.successfulRecords = successfulRecords;
    }

    public List<AttendanceUploadError> getFailedRecords() {
        return failedRecords;
    }

    public void setFailedRecords(List<AttendanceUploadError> failedRecords) {
        this.failedRecords = failedRecords;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
}
