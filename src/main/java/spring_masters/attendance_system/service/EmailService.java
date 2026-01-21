package spring_masters.attendance_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    /**
     * Send low attendance alert to student
     */
    public void sendLowAttendanceAlert(String studentEmail, String studentName,
            String subjectName, double attendancePercentage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(studentEmail);
            helper.setSubject("⚠️ Low Attendance Alert - " + subjectName);

            String htmlContent = buildLowAttendanceEmailTemplate(
                    studentName, subjectName, attendancePercentage);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Low attendance alert sent to: " + studentEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + studentEmail + ": " + e.getMessage());
            // In production, you might want to log this or throw a custom exception
        }
    }

    public void sendAttendanceMarkedNotification(String studentEmail, String studentName,
            String subjectName, boolean present,
            LocalDate date) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(studentEmail);
            helper.setSubject("📚 Attendance Marked - " + subjectName);

            String htmlContent = buildAttendanceMarkedEmailTemplate(
                    studentName, subjectName, present, date);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Attendance marked notification sent to: " + studentEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + studentEmail + ": " + e.getMessage());
        }
    }

    private String buildLowAttendanceEmailTemplate(String studentName,
            String subjectName,
            double percentage) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            margin: 0;
                            padding: 0;
                            background-color: #f4f4f4;
                        }
                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background: white;
                            border-radius: 10px;
                            overflow: hidden;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white;
                            padding: 30px 20px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 30px 20px;
                        }
                        .warning-box {
                            background-color: #fff3cd;
                            border-left: 4px solid #ffc107;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .warning-box .percentage {
                            font-size: 32px;
                            font-weight: bold;
                            color: #e74c3c;
                            margin: 10px 0;
                        }
                        .info-box {
                            background-color: #e8f4f8;
                            border-left: 4px solid #3498db;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #666;
                        }
                        .button {
                            display: inline-block;
                            padding: 12px 24px;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            margin: 20px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>⚠️ Low Attendance Alert</h1>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>

                            <div class="warning-box">
                                <p style="margin: 0; font-size: 14px;">Your attendance in</p>
                                <h2 style="margin: 10px 0; color: #333;">%s</h2>
                                <p style="margin: 0; font-size: 14px;">has dropped to</p>
                                <div class="percentage">%.2f%%</div>
                            </div>

                            <div class="info-box">
                                <p style="margin: 0;"><strong>📌 Important Notice:</strong></p>
                                <p style="margin: 10px 0 0 0;">
                                    You need to maintain at least <strong>75%% attendance</strong>
                                    to meet the minimum requirement. Please ensure regular attendance
                                    to avoid academic penalties.
                                </p>
                            </div>

                            <p>If you have any concerns or need to discuss this matter, please contact your teacher or the administration office.</p>

                            <p style="margin-top: 30px;">Best regards,<br>
                            <strong>Attendance Management System</strong><br>
                            Spring Masters Team</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message from the Attendance Management System.</p>
                            <p>© 2026 Spring Masters. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(studentName, subjectName, percentage);
    }

    /**
     * Build HTML template for attendance marked notification
     */
    private String buildAttendanceMarkedEmailTemplate(String studentName,
            String subjectName,
            boolean present,
            LocalDate date) {
        String status = present ? "Present ✅" : "Absent ❌";
        String statusColor = present ? "#27ae60" : "#e74c3c";
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            margin: 0;
                            padding: 0;
                            background-color: #f4f4f4;
                        }
                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background: white;
                            border-radius: 10px;
                            overflow: hidden;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white;
                            padding: 30px 20px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 30px 20px;
                        }
                        .status-box {
                            background-color: #f8f9fa;
                            border-radius: 8px;
                            padding: 20px;
                            margin: 20px 0;
                            text-align: center;
                        }
                        .status {
                            font-size: 28px;
                            font-weight: bold;
                            color: %s;
                            margin: 10px 0;
                        }
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📚 Attendance Marked</h1>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>

                            <p>Your attendance has been marked for the following class:</p>

                            <div class="status-box">
                                <p style="margin: 0; color: #666;">Subject</p>
                                <h2 style="margin: 10px 0; color: #333;">%s</h2>
                                <p style="margin: 10px 0 0 0; color: #666;">Date: %s</p>
                                <div class="status">%s</div>
                            </div>

                            <p>You can view your complete attendance records by logging into the Attendance Management System.</p>

                            <p style="margin-top: 30px;">Best regards,<br>
                            <strong>Attendance Management System</strong><br>
                            Spring Masters Team</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message from the Attendance Management System.</p>
                            <p>© 2026 Spring Masters. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(statusColor, studentName, subjectName, formattedDate, status);
    }
}
