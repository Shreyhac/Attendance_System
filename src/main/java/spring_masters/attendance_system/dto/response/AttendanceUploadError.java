package spring_masters.attendance_system.dto.response;

public class AttendanceUploadError {

    private int rowNumber;
    private String studentEmail;
    private String subjectName;
    private String errorMessage;
    private String errorType;

    public AttendanceUploadError() {
    }

    public AttendanceUploadError(int rowNumber, String studentEmail, String subjectName,
            String errorMessage, String errorType) {
        this.rowNumber = rowNumber;
        this.studentEmail = studentEmail;
        this.subjectName = subjectName;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
