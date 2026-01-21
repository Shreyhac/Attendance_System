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

3. **Start the Backend**
   ```bash
   ./mvnw.cmd spring-boot:run
   ```
   Backend will start on `http://localhost:8081`

4. **Start the Frontend**
   ```bash
   cd frontend
   python -m http.server 3000
   ```
   Frontend will be available at `http://localhost:3000`

5. **Access the Application**
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

## 🔄 Future Enhancements

- [ ] Admin dashboard for user management
- [ ] Bulk attendance marking
- [ ] Attendance reports and analytics
- [ ] Email notifications for low attendance
- [ ] Export attendance to CSV/PDF
- [ ] Subject-wise attendance breakdown
- [ ] Date range filtering
- [ ] Password reset functionality
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
