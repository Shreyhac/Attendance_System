# 📚 Attendance Management System

> **A modern, full-stack attendance tracking application built with Spring Boot and beautiful vanilla JavaScript frontend**

**Made by Spring Masters (a.k.a ML Masters)** 🚀

---

## 🌟 Overview

The Attendance Management System is a comprehensive web application designed to streamline attendance tracking for educational institutions. It features role-based access control, JWT authentication, and a stunning modern UI.

### Key Features

- ✅ **Secure Authentication** - JWT-based authentication with role-based access control
- 👨‍🏫 **Teacher Dashboard** - Create subjects and mark student attendance
- 👨‍🎓 **Student Dashboard** - View attendance records and calculate attendance percentage
- 📧 **Email Notifications** - Automatic low attendance alerts sent to students
- 🎨 **Beautiful UI** - Modern, responsive design with smooth animations
- 🔒 **Security** - Spring Security with stateless session management
- 📊 **MongoDB Integration** - NoSQL database for flexible data storage

---

## 🛠️ Technology Stack

### Backend
- **Framework:** Spring Boot 4.0.1
- **Language:** Java 17
- **Database:** MongoDB
- **Security:** Spring Security + JWT (JJWT)
- **Build Tool:** Maven

### Frontend
- **HTML5** - Semantic markup
- **CSS3** - Modern design with CSS variables, animations, and glassmorphism
- **JavaScript (ES6+)** - Vanilla JS with async/await
- **Font:** Inter (Google Fonts)

---

## 📁 Project Structure

```
attendance_system/
├── src/main/java/spring_masters/attendance_system/
│   ├── config/
│   │   ├── security/
│   │   │   ├── SecurityConfig.java          # Spring Security configuration
│   │   │   └── PasswordConfig.java          # Password encoder bean
│   │   └── CorsConfig.java                  # CORS configuration
│   ├── controller/
│   │   ├── auth/
│   │   │   └── AuthController.java          # Authentication endpoints
│   │   ├── StudentController.java           # Student endpoints
│   │   ├── TeacherController.java           # Teacher endpoints
│   │   └── SubjectController.java           # Subject endpoints
│   ├── dto/
│   │   ├── request/
│   │   │   ├── RegisterRequest.java         # Registration DTO
│   │   │   └── LoginRequest.java            # Login DTO
│   │   ├── CreateSubjectRequest.java        # Subject creation DTO
│   │   └── MarkAttendanceRequest.java       # Attendance marking DTO
│   ├── model/
│   │   ├── entity/
│   │   │   ├── User.java                    # User entity
│   │   │   ├── Subject.java                 # Subject entity
│   │   │   └── Attendance.java              # Attendance entity
│   │   └── enums/
│   │       └── Role.java                    # User roles enum
│   ├── repository/
│   │   ├── UserRepository.java              # User MongoDB repository
│   │   ├── SubjectRepository.java           # Subject MongoDB repository
│   │   └── AttendanceRepository.java        # Attendance MongoDB repository
│   ├── security/
│   │   └── JwtAuthFilter.java               # JWT authentication filter
│   ├── service/
│   │   ├── auth/
│   │   │   └── AuthService.java             # Authentication service
│   │   ├── StudentService.java              # Student service
│   │   └── TeacherService.java              # Teacher service
│   ├── util/
│   │   └── JwtUtil.java                     # JWT utility class
│   └── AttendanceSystemApplication.java     # Main application class
├── src/main/resources/
│   └── application.yml                      # Application configuration
├── frontend/
│   ├── index.html                           # Main HTML file
│   ├── styles.css                           # CSS styles
│   └── app.js                               # JavaScript application logic
└── pom.xml                                  # Maven dependencies
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **MongoDB** (running on localhost:27017)
- **Maven** (or use included Maven wrapper)
- **Modern web browser**

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd attendance_system
   ```

2. **Configure MongoDB**
   - Ensure MongoDB is running on `localhost:27017`
   - Database `attendance_db` will be created automatically

3. **Configure Email Settings** (Optional but recommended)
   - Open `src/main/resources/application.yml`
   - Update email configuration:
     ```yaml
     spring:
       mail:
         username: your-email@gmail.com
         password: your-app-password  # For Gmail, use App Password
     app:
       email:
         from: your-email@gmail.com
         attendance-threshold: 75.0
     ```
   - **For Gmail**: Enable 2FA and create an [App Password](https://myaccount.google.com/apppasswords)
   - **For Testing**: Use [Mailtrap](https://mailtrap.io/) to test without sending real emails

4. **Start the Backend**
   ```bash
   ./mvnw.cmd spring-boot:run
   ```
   Backend will start on `http://localhost:8081`

5. **Start the Frontend**
   ```bash
   cd frontend
   python -m http.server 3000
   ```
   Frontend will be available at `http://localhost:3000`

6. **Access the Application**
   - Open your browser and navigate to `http://localhost:3000`

---

## 📡 API Documentation

### Base URL
```
http://localhost:8081/api
```

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "TEACHER" | "STUDENT"
}

Response: "User registered successfully"
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response: "eyJhbGciOiJIUzI1NiJ9..." (JWT Token)
```

### Teacher Endpoints (Requires TEACHER role)

#### Create Subject
```http
POST /api/teacher/subject
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Mathematics",
  "teacherEmail": "teacher@example.com"
}

Response: {
  "id": "507f1f77bcf86cd799439011",
  "name": "Mathematics",
  "teacherEmail": "teacher@example.com"
}
```

#### Mark Attendance
```http
POST /api/teacher/attendance
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "studentEmail": "student@example.com",
  "subjectId": "507f1f77bcf86cd799439011",
  "present": true
}

Response: {
  "id": "507f191e810c19729de860ea",
  "studentEmail": "student@example.com",
  "subjectId": "507f1f77bcf86cd799439011",
  "date": "2026-01-21",
  "present": true
}
```

### Student Endpoints (Requires STUDENT role)

#### Get Attendance Records
```http
GET /api/student/attendance?email=student@example.com
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": true
  }
]
```

#### Get Attendance Percentage
```http
GET /api/student/attendance/percentage?email=student@example.com
Authorization: Bearer <JWT_TOKEN>

Response: 85.5
```

### Public Endpoints

#### Get Subject by ID
```http
GET /api/subjects/{id}

Response: {
  "id": "507f1f77bcf86cd799439011",
  "name": "Mathematics",
  "teacherEmail": "teacher@example.com"
}
```

### Email Endpoints (Requires TEACHER role)

#### Send Low Attendance Alert (Manual)
```http
POST /api/email/send-low-attendance-alert
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "studentEmail": "student@example.com",
  "studentName": "John Doe",
  "subjectName": "Mathematics",
  "attendancePercentage": 65.5
}

Response: "Low attendance alert sent successfully to student@example.com"
```

#### Send Attendance Notification (Manual)
```http
POST /api/email/send-attendance-notification
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/x-www-form-urlencoded

studentEmail=student@example.com
&studentName=John Doe
&subjectName=Mathematics
&present=true

Response: "Attendance notification sent successfully to student@example.com"
```

> **Note:** Email notifications are sent automatically when attendance is marked and falls below the configured threshold (default: 75%). The manual endpoints above are for testing or administrative purposes.

---

## 🔐 Security Features

### JWT Authentication
- **Token Generation:** On successful login
- **Token Expiry:** 1 hour
- **Token Storage:** LocalStorage (frontend)
- **Header Format:** `Authorization: Bearer <token>`

### Role-Based Access Control
- **ADMIN:** Full system access (future implementation)
- **TEACHER:** Create subjects, mark attendance
- **STUDENT:** View own attendance records

### Security Configuration
- **CSRF:** Disabled (stateless API)
- **Session Management:** Stateless
- **CORS:** Configured for `localhost:3000`
- **Password Encryption:** BCrypt

---

## 🎨 Frontend Features

### Pages
1. **Landing Page** - Animated welcome screen with gradient effects
2. **Login Page** - Secure authentication
3. **Register Page** - User registration with role selection
4. **Teacher Dashboard** - Subject creation and attendance marking
5. **Student Dashboard** - Attendance records and percentage display

### Design Highlights
- **Color Scheme:** Purple gradient (#667eea to #764ba2)
- **Typography:** Inter font family
- **Animations:** Floating cards, smooth transitions, micro-interactions
- **Components:** Cards, buttons, forms, tables with consistent styling
- **Responsive:** Mobile-first design with flexbox/grid layouts

### Key UI Components
- Custom radio buttons with green highlight
- Loading spinners
- Error/success messages
- Animated landing page decorations
- Glassmorphism effects

---

## 📊 Database Schema

### Users Collection
```javascript
{
  "_id": ObjectId,
  "name": String,
  "email": String (unique),
  "password": String (BCrypt hashed),
  "role": "ADMIN" | "TEACHER" | "STUDENT",
  "active": Boolean
}
```

### Subjects Collection
```javascript
{
  "_id": ObjectId,
  "name": String,
  "teacherEmail": String
}
```

### Attendance Collection
```javascript
{
  "_id": ObjectId,
  "studentEmail": String,
  "subjectId": String,
  "date": LocalDate,
  "present": Boolean
}
```

---

## 🧪 Testing

### Using Postman
1. Import the provided Postman collection: `Attendance_System_API.postman_collection.json`
2. Register a teacher and student account
3. Login to get JWT tokens
4. Test all endpoints with proper authorization headers

### Manual Testing
1. **Register as Teacher:**
   - Navigate to registration page
   - Fill in details with role "TEACHER"
   - Login with credentials

2. **Create Subject:**
   - Go to teacher dashboard
   - Create a subject (e.g., "Mathematics")

3. **Register as Student:**
   - Open incognito window
   - Register with role "STUDENT"

4. **Mark Attendance:**
   - As teacher, mark attendance for student email
   - Select subject and status (Present/Absent)

5. **View Attendance:**
   - Login as student
   - View attendance records and percentage

---

## 🐛 Troubleshooting

### Common Issues

**Issue:** Backend not starting
- **Solution:** Ensure MongoDB is running on port 27017

**Issue:** CORS errors in browser
- **Solution:** Make sure frontend is served via HTTP server (not file://)

**Issue:** 403 Forbidden on API calls
- **Solution:** Check JWT token is valid and included in Authorization header

**Issue:** Frontend not updating
- **Solution:** Hard refresh browser (Ctrl + Shift + R)

---

## 📧 Email Notification System

### Overview
The system automatically sends email notifications to students when their attendance falls below the configured threshold (default: 75%).

### Features
- **Automatic Alerts**: Emails sent when marking attendance if percentage drops below threshold
- **Professional Templates**: Beautiful HTML email templates with branding
- **Per-Subject Tracking**: Attendance calculated separately for each subject
- **Manual Triggers**: API endpoints for sending emails manually (testing/admin)

### Configuration

Email settings are configured in `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

app:
  email:
    from: your-email@gmail.com
    attendance-threshold: 75.0
```

### Email Providers

**Gmail** (Production):
1. Enable 2-factor authentication
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Use App Password in configuration

**Mailtrap** (Testing/Development):
1. Sign up at https://mailtrap.io/
2. Get SMTP credentials from your inbox
3. Update configuration:
   ```yaml
   spring:
     mail:
       host: smtp.mailtrap.io
       port: 2525
       username: your-mailtrap-username
       password: your-mailtrap-password
   ```

### How It Works

1. Teacher marks attendance for a student
2. System calculates current attendance percentage for that subject
3. If percentage < threshold (75%), email is automatically sent
4. Email contains:
   - Student name
   - Subject name
   - Current attendance percentage
   - Warning message

### Email Templates

**Low Attendance Alert**:
- Professional HTML design with gradient header
- Clear warning message
- Current attendance percentage highlighted
- Actionable advice for students

**Attendance Marked Notification** (Optional):
- Confirmation of attendance marking
- Subject and date information
- Present/Absent status

---

## 🚦 API Rate Limiting

### Overview
The system implements API rate limiting using Bucket4j to prevent abuse and ensure fair usage. Each user/IP address is limited to a configurable number of requests per time window.

### Features
- **Token Bucket Algorithm**: Smooth rate limiting with burst support
- **Per-User Limiting**: Separate limits for each authenticated user (via JWT email)
- **IP-Based Fallback**: Unauthenticated requests limited by IP address
- **Configurable**: Easily adjust limits via `application.yml`
- **Graceful Responses**: Clear error messages with retry-after information

### Configuration

Rate limiting settings are configured in `application.yml`:

```yaml
app:
  rate-limit:
    enabled: true
    capacity: 100  # Maximum requests allowed
    refill-tokens: 100  # Tokens to refill
    refill-duration-minutes: 1  # Refill period
```

### How It Works

1. **Request arrives** → RateLimitFilter intercepts
2. **Identify user** → Extract email from JWT or use IP address
3. **Get bucket** → Retrieve or create token bucket for this user
4. **Try consume** → Attempt to take 1 token from bucket
5. **Success** → Request proceeds normally
6. **Failure** → Return HTTP 429 (Too Many Requests)

### Rate Limit Headers

Successful requests include rate limit information:
```
X-Rate-Limit-Remaining: 95
```

### Rate Limit Exceeded Response

When limit is exceeded, you'll receive:
```json
{
  "error": "Rate limit exceeded",
  "message": "Too many requests. Please try again in 45 seconds.",
  "retryAfter": 45
}
```

### Testing Rate Limits

**Using Postman:**
1. Send rapid requests to any protected endpoint
2. After 100 requests in 1 minute, you'll get 429 error
3. Wait for the refill period (1 minute)
4. Bucket refills and requests work again

**Disable Rate Limiting** (for development):
```yaml
app:
  rate-limit:
    enabled: false
```

### Customization

**Adjust Limits:**
- `capacity`: Maximum tokens in bucket (max burst size)
- `refill-tokens`: How many tokens to add during refill
- `refill-duration-minutes`: How often to refill

**Example - Stricter Limits:**
```yaml
app:
  rate-limit:
    capacity: 50
    refill-tokens: 50
    refill-duration-minutes: 1  # 50 requests per minute
```

**Example - More Lenient:**
```yaml
app:
  rate-limit:
    capacity: 1000
    refill-tokens: 1000
    refill-duration-minutes: 60  # 1000 requests per hour
```

---

## 📊 Analytics APIs

### Overview
Comprehensive analytics endpoints providing insights for students, teachers, and subjects. Track attendance trends, identify struggling students, and make data-driven decisions.

### Student Analytics

#### GET /api/analytics/student/{email}/overview
Get comprehensive analytics overview for a student.

**Authorization**: STUDENT role (own data only)

**Response:**
```json
{
  "studentEmail": "student1@example.com",
  "studentName": "John Doe",
  "overallPercentage": 85.5,
  "totalClasses": 120,
  "attendedClasses": 103,
  "subjectWiseAttendance": [
    {
      "subjectId": "...",
      "subjectName": "Mathematics",
      "totalClasses": 40,
      "attendedClasses": 36,
      "percentage": 90.0
    }
  ]
}
```

#### GET /api/analytics/student/{email}/subject/{subjectId}
Get detailed analytics for a specific subject.

**Authorization**: STUDENT role

#### GET /api/analytics/student/{email}/trends?days=30
Get attendance trends over the last N days.

**Authorization**: STUDENT role

**Query Parameters:**
- `days` (optional, default: 30) - Number of days to analyze

---

### Teacher Analytics

#### GET /api/analytics/teacher/subject/{subjectId}/summary?threshold=75
Get comprehensive subject analytics for teachers.

**Authorization**: TEACHER role

**Query Parameters:**
- `threshold` (optional, default: 75.0) - Attendance threshold percentage

**Response:**
```json
{
  "subjectId": "...",
  "subjectName": "Mathematics",
  "totalClasses": 45,
  "totalStudents": 30,
  "averageAttendance": 82.5,
  "distribution": {
    "excellent": 12,
    "good": 10,
    "poor": 8
  },
  "defaulters": [
    {
      "studentEmail": "student@example.com",
      "studentName": "Jane Doe",
      "attendancePercentage": 65.0,
      "totalClasses": 45,
      "attendedClasses": 29
    }
  ]
}
```

#### GET /api/analytics/teacher/subject/{subjectId}/defaulters?threshold=75
Get list of students with attendance below threshold.

**Authorization**: TEACHER role

**Query Parameters:**
- `threshold` (optional, default: 75.0) - Attendance threshold percentage

#### GET /api/analytics/teacher/subject/{subjectId}/distribution
Get attendance distribution (excellent/good/poor).

**Authorization**: TEACHER role

**Response:**
```json
{
  "excellent": 12,
  "good": 10,
  "poor": 8
}
```

---

### Subject Analytics

#### GET /api/analytics/subject/{subjectId}/stats
Get subject-level statistics.

**Authorization**: All authenticated users

**Response:**
```json
{
  "subjectId": "...",
  "subjectName": "Mathematics",
  "teacherEmail": "teacher@example.com",
  "totalClasses": 45,
  "totalRecords": 1350,
  "averageAttendance": 82.5
}
```

#### GET /api/analytics/subject/{subjectId}/trends?days=30
Get subject attendance trends over time.

**Authorization**: All authenticated users

**Query Parameters:**
- `days` (optional, default: 30) - Number of days to analyze

**Response:**
```json
[
  {
    "date": "2026-01-15",
    "attendancePercentage": 85.0,
    "present": 25,
    "absent": 5,
    "total": 30
  }
]
```

---

### Analytics Features

- **Real-time Calculations**: All analytics computed on-demand
- **Date Range Filtering**: Analyze specific time periods
- **Trend Analysis**: Track attendance patterns over time
- **Distribution Analysis**: Categorize students by performance
- **Defaulter Identification**: Automatically identify at-risk students
- **Subject Comparison**: Compare performance across subjects

---

## 🔄 Future Enhancements

- [ ] Admin dashboard for user management
- [ ] Bulk attendance marking
- [x] Attendance reports and analytics ✅
- [x] Email notifications for low attendance ✅
- [x] API rate limiting ✅
- [ ] Export attendance to CSV/PDF
- [x] Subject-wise attendance breakdown ✅
- [x] Date range filtering ✅
- [ ] Password reset functionality via email
- [ ] Profile management
- [ ] Dark/Light theme toggle

---

## 👥 Contributors

**Spring Masters (a.k.a ML Masters)**

---

## 📄 License

This project is created for educational purposes.

---

## 📞 Support

For issues or questions, please create an issue in the repository.

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- MongoDB for the flexible NoSQL database
- Google Fonts for the Inter font family
- All open-source contributors

---

**Made with ❤️ by Spring Masters (a.k.a ML Masters)**
