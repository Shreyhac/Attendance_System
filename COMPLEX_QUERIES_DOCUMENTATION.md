# 🔍 Complex Queries Implementation - API Documentation

**Implementation Date:** January 21, 2026  
**Branch:** shreyanshdev  
**Feature:** Complex MongoDB Queries for Attendance System

---

## 📋 Overview

This document details all the complex query features implemented in the Attendance Management System. These queries enable advanced filtering, date-range searches, and detailed analytics for both students and teachers.

---

## 🎯 Features Implemented

### ✅ Repository Layer Enhancements
- **15+ new query methods** in `AttendanceRepository`
- **Date-based filtering** with `findByDateBetween`
- **Subject-specific queries** with `findBySubjectIdAndDate`
- **Student-subject combinations** with `findByStudentEmailAndSubjectId`
- **Custom MongoDB queries** using `@Query` annotation
- **Count queries** for statistics

### ✅ Service Layer Enhancements
- **StudentService**: 9 new methods for filtered attendance queries
- **TeacherService**: 11 new methods for class management and analytics
- **Duplicate prevention** in attendance marking

### ✅ Controller Layer Enhancements
- **StudentController**: 8 new REST endpoints
- **TeacherController**: 10 new REST endpoints
- **Date parameter support** with `@DateTimeFormat`

---

## 📡 API Endpoints

### Student Endpoints

#### 1. Get Attendance by Subject
```http
GET /api/student/attendance/subject?email=student@example.com&subjectId=507f1f77bcf86cd799439011
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

**Use Case:** Student views attendance for "Mathematics" only

---

#### 2. Get Attendance Percentage by Subject
```http
GET /api/student/attendance/subject/percentage?email=student@example.com&subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: 85.5
```

**Use Case:** "You have 85% attendance in Mathematics"

---

#### 3. Get Attendance Between Dates
```http
GET /api/student/attendance/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-15",
    "present": true
  },
  {
    "id": "507f191e810c19729de860eb",
    "studentEmail": "student@example.com",
    "subjectId": "507f1f77bcf86cd799439012",
    "date": "2026-01-20",
    "present": false
  }
]
```

**Use Case:** "Show my attendance for January 2026"

---

#### 4. Get Attendance for Subject Between Dates
```http
GET /api/student/attendance/subject/daterange?email=student@example.com&subjectId=507f1f77bcf86cd799439011&startDate=2026-01-01&endDate=2026-01-31
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-15",
    "present": true
  }
]
```

**Use Case:** "Show my Mathematics attendance for last month"

---

#### 5. Get Attendance Percentage Between Dates
```http
GET /api/student/attendance/percentage/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31
Authorization: Bearer <JWT_TOKEN>

Response: 78.5
```

**Use Case:** "What's my attendance percentage for January?"

---

#### 6. Get Total Present Days
```http
GET /api/student/attendance/present/count?email=student@example.com
Authorization: Bearer <JWT_TOKEN>

Response: 42
```

**Use Case:** Count total days student was present

---

#### 7. Get Total Absent Days
```http
GET /api/student/attendance/absent/count?email=student@example.com
Authorization: Bearer <JWT_TOKEN>

Response: 8
```

**Use Case:** Count total days student was absent

---

#### 8. Check if Attendance Marked Today
```http
GET /api/student/attendance/today/check?email=student@example.com&subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: true
```

**Use Case:** Check if attendance is already marked for today

---

### Teacher Endpoints

#### 1. Get All Attendance for Subject
```http
GET /api/teacher/attendance/subject?subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student1@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": true
  },
  {
    "id": "507f191e810c19729de860eb",
    "studentEmail": "student2@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": false
  }
]
```

**Use Case:** Teacher views all attendance for "Mathematics"

---

#### 2. Get Attendance by Subject and Date
```http
GET /api/teacher/attendance/subject/date?subjectId=507f1f77bcf86cd799439011&date=2026-01-21
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student1@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": true
  }
]
```

**Use Case:** "Who was present in Mathematics on Jan 21, 2026?"

---

#### 3. Get Attendance for Subject Between Dates
```http
GET /api/teacher/attendance/subject/daterange?subjectId=507f1f77bcf86cd799439011&startDate=2026-01-01&endDate=2026-01-31
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student1@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-15",
    "present": true
  },
  {
    "id": "507f191e810c19729de860eb",
    "studentEmail": "student1@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-20",
    "present": true
  }
]
```

**Use Case:** "Show Mathematics attendance for January 2026"

---

#### 4. Get Present Students by Subject
```http
GET /api/teacher/attendance/subject/present?subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student1@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": true
  }
]
```

**Use Case:** "List of students present in Mathematics"

---

#### 5. Get Absent Students by Subject
```http
GET /api/teacher/attendance/subject/absent?subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860eb",
    "studentEmail": "student2@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": false
  }
]
```

**Use Case:** "List of students absent in Mathematics"

---

#### 6. Get Student Attendance in Subject
```http
GET /api/teacher/attendance/student?studentEmail=student@example.com&subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-15",
    "present": true
  }
]
```

**Use Case:** Teacher checks a particular student's attendance

---

#### 7. Get Student Attendance Percentage in Subject
```http
GET /api/teacher/attendance/student/percentage?studentEmail=student@example.com&subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: 85.5
```

**Use Case:** "What's John's attendance in Mathematics?"

---

#### 8. Get Today's Attendance
```http
GET /api/teacher/attendance/today?subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f191e810c19729de860ea",
    "studentEmail": "student1@example.com",
    "subjectId": "507f1f77bcf86cd799439011",
    "date": "2026-01-21",
    "present": true
  }
]
```

**Use Case:** Quick view of today's attendance

---

#### 9. Check if Attendance Marked Today
```http
GET /api/teacher/attendance/today/check?studentEmail=student@example.com&subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: true
```

**Use Case:** Check if attendance already marked for a student today

---

#### 10. Get Total Attendance Count
```http
GET /api/teacher/attendance/count?subjectId=507f1f77bcf86cd799439011
Authorization: Bearer <JWT_TOKEN>

Response: 150
```

**Use Case:** Count total attendance records for a subject

---

#### 11. Get Subjects by Teacher
```http
GET /api/teacher/subjects?teacherEmail=teacher@example.com
Authorization: Bearer <JWT_TOKEN>

Response: [
  {
    "id": "507f1f77bcf86cd799439011",
    "name": "Mathematics",
    "teacherEmail": "teacher@example.com"
  },
  {
    "id": "507f1f77bcf86cd799439012",
    "name": "Physics",
    "teacherEmail": "teacher@example.com"
  }
]
```

**Use Case:** List all subjects taught by a teacher

---

## 🔧 Repository Methods

### AttendanceRepository

```java
// Basic Queries
List<Attendance> findBySubjectId(String subjectId);
List<Attendance> findByStudentEmail(String studentEmail);

// Complex Queries
List<Attendance> findByStudentEmailAndSubjectId(String studentEmail, String subjectId);
Optional<Attendance> findByStudentEmailAndSubjectIdAndDate(String studentEmail, String subjectId, LocalDate date);
List<Attendance> findBySubjectIdAndDate(String subjectId, LocalDate date);
List<Attendance> findByStudentEmailAndDateBetween(String studentEmail, LocalDate startDate, LocalDate endDate);
List<Attendance> findBySubjectIdAndDateBetween(String subjectId, LocalDate startDate, LocalDate endDate);
List<Attendance> findByStudentEmailAndSubjectIdAndDateBetween(String studentEmail, String subjectId, LocalDate startDate, LocalDate endDate);
List<Attendance> findByStudentEmailAndPresent(String studentEmail, boolean present);
List<Attendance> findBySubjectIdAndPresent(String subjectId, boolean present);

// Custom MongoDB Query
@Query("{ 'studentEmail': ?0, 'subjectId': ?1, 'date': { $gte: ?2, $lte: ?3 }, 'present': ?4 }")
List<Attendance> findByCustomFilters(String studentEmail, String subjectId, LocalDate startDate, LocalDate endDate, boolean present);

// Count Queries
long countByStudentEmailAndSubjectId(String studentEmail, String subjectId);
long countByStudentEmailAndSubjectIdAndPresent(String studentEmail, String subjectId, boolean present);

// Delete Query
void deleteByDateBefore(LocalDate date);
```

---

## 💡 Key Features

### 1. **Duplicate Prevention**
The `markAttendance` method now checks if attendance already exists for the day and updates it instead of creating a duplicate:

```java
Optional<Attendance> existing = attendanceRepository
    .findByStudentEmailAndSubjectIdAndDate(
        request.getStudentEmail(),
        request.getSubjectId(),
        LocalDate.now()
    );

if (existing.isPresent()) {
    // Update existing record
    Attendance attendance = existing.get();
    attendance.setPresent(request.isPresent());
    return attendanceRepository.save(attendance);
}
```

### 2. **Date Range Filtering**
All date-based queries use Spring Data's `Between` keyword for efficient MongoDB range queries:

```java
List<Attendance> findByStudentEmailAndDateBetween(
    String studentEmail, 
    LocalDate startDate, 
    LocalDate endDate
);
```

### 3. **Custom MongoDB Queries**
Using `@Query` annotation for complex filters:

```java
@Query("{ 'studentEmail': ?0, 'subjectId': ?1, 'date': { $gte: ?2, $lte: ?3 }, 'present': ?4 }")
List<Attendance> findByCustomFilters(
    String studentEmail,
    String subjectId,
    LocalDate startDate,
    LocalDate endDate,
    boolean present
);
```

### 4. **Count Queries**
Efficient counting without loading all records:

```java
long countByStudentEmailAndSubjectIdAndPresent(
    String studentEmail, 
    String subjectId, 
    boolean present
);
```

---

## 🧪 Testing Examples

### Using cURL

#### Test 1: Get Attendance by Subject
```bash
curl -X GET "http://localhost:8081/api/student/attendance/subject?email=student@example.com&subjectId=507f1f77bcf86cd799439011" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Test 2: Get Attendance Between Dates
```bash
curl -X GET "http://localhost:8081/api/student/attendance/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Test 3: Get Today's Attendance (Teacher)
```bash
curl -X GET "http://localhost:8081/api/teacher/attendance/today?subjectId=507f1f77bcf86cd799439011" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📊 MongoDB Query Examples

### 1. Find by Student and Subject
```javascript
db.attendance.find({
  "studentEmail": "student@example.com",
  "subjectId": "507f1f77bcf86cd799439011"
})
```

### 2. Find by Date Range
```javascript
db.attendance.find({
  "studentEmail": "student@example.com",
  "date": {
    "$gte": ISODate("2026-01-01"),
    "$lte": ISODate("2026-01-31")
  }
})
```

### 3. Find Present Students
```javascript
db.attendance.find({
  "subjectId": "507f1f77bcf86cd799439011",
  "present": true
})
```

### 4. Count Present Days
```javascript
db.attendance.countDocuments({
  "studentEmail": "student@example.com",
  "subjectId": "507f1f77bcf86cd799439011",
  "present": true
})
```

---

## 🎯 Use Cases

### For Students
1. ✅ View attendance in specific subjects
2. ✅ Check attendance for a date range (monthly reports)
3. ✅ Calculate subject-wise attendance percentage
4. ✅ Count total present/absent days
5. ✅ Verify if attendance is marked for today

### For Teachers
1. ✅ View all students' attendance for a subject
2. ✅ Check who was present/absent on a specific date
3. ✅ Generate attendance reports for date ranges
4. ✅ View present/absent student lists
5. ✅ Check individual student attendance in their subject
6. ✅ Calculate student's attendance percentage in their subject
7. ✅ Quick view of today's attendance
8. ✅ Prevent duplicate attendance marking
9. ✅ View all subjects they teach

---

## 🔍 Performance Considerations

### Indexing Recommendations
```java
@Document(collection = "attendance")
@CompoundIndex(name = "student_subject_idx", def = "{'studentEmail': 1, 'subjectId': 1}")
@CompoundIndex(name = "subject_date_idx", def = "{'subjectId': 1, 'date': -1}")
@CompoundIndex(name = "student_date_idx", def = "{'studentEmail': 1, 'date': -1}")
public class Attendance {
    // ...
}
```

### Query Optimization
- Use `countBy` methods instead of loading all records when only count is needed
- Date range queries use MongoDB's `$gte` and `$lte` operators for efficiency
- Compound indexes on frequently queried field combinations

---

## 📝 Next Steps

### Potential Enhancements
1. **Pagination**: Add pagination for large result sets
2. **Sorting**: Add sorting options (by date, name, etc.)
3. **Aggregation**: Add aggregate queries for advanced analytics
4. **Caching**: Implement caching for frequently accessed data
5. **Batch Operations**: Add bulk attendance marking
6. **Export**: Add CSV/PDF export functionality

---

## 🎉 Summary

### What Was Implemented
- ✅ **15+ repository query methods**
- ✅ **20 new service methods**
- ✅ **18 new REST endpoints**
- ✅ **Date-based filtering**
- ✅ **Subject-specific queries**
- ✅ **Duplicate prevention**
- ✅ **Count queries**
- ✅ **Custom MongoDB queries**

### Benefits
- 📊 **Advanced Analytics**: Detailed attendance reports
- 🎯 **Flexible Filtering**: Query by subject, date, or combination
- ⚡ **Performance**: Efficient MongoDB queries
- 🔒 **Data Integrity**: Prevents duplicate attendance
- 📈 **Scalability**: Ready for large datasets

---

**Implementation Complete!** ✅  
All complex query features are now live and ready to use.

*Branch: shreyanshdev*  
*Date: January 21, 2026*
