# 🧪 Postman Testing Guide - Complex Queries

**Testing Date:** January 21, 2026  
**Collection:** Complex_Queries_API.postman_collection.json  
**Purpose:** Test all complex query endpoints systematically

---

## 📋 Prerequisites

### 1. Import the Collection
1. Open Postman
2. Click **Import** button
3. Select `Complex_Queries_API.postman_collection.json`
4. Collection will appear in your workspace

### 2. Verify Backend is Running
- Backend URL: `http://localhost:8081`
- MongoDB: Running on `localhost:27017`
- Check status: Backend should show "Started AttendanceSystemApplication"

---

## 🎯 Testing Workflow

### Step 1: Authentication (Run in Order)

#### 1.1 Register Teacher
```
POST http://localhost:8081/api/auth/register
Body:
{
  "name": "Dr. Smith",
  "email": "teacher@example.com",
  "password": "password123",
  "role": "TEACHER"
}

Expected Response: "User registered successfully"
```

#### 1.2 Register Student
```
POST http://localhost:8081/api/auth/register
Body:
{
  "name": "John Doe",
  "email": "student@example.com",
  "password": "password123",
  "role": "STUDENT"
}

Expected Response: "User registered successfully"
```

#### 1.3 Login Teacher
```
POST http://localhost:8081/api/auth/login
Body:
{
  "email": "teacher@example.com",
  "password": "password123"
}

Expected Response: JWT token (automatically saved to {{teacher_token}})
Example: "eyJhbGciOiJIUzI1NiJ9..."
```

#### 1.4 Login Student
```
POST http://localhost:8081/api/auth/login
Body:
{
  "email": "student@example.com",
  "password": "password123"
}

Expected Response: JWT token (automatically saved to {{student_token}})
```

---

### Step 2: Setup - Create Subject

#### 2.1 Create Mathematics Subject
```
POST http://localhost:8081/api/teacher/subject
Headers:
  Authorization: Bearer {{teacher_token}}
Body:
{
  "name": "Mathematics",
  "teacherEmail": "teacher@example.com"
}

Expected Response:
{
  "id": "67901234abcd5678ef901234",
  "name": "Mathematics",
  "teacherEmail": "teacher@example.com"
}

Note: Subject ID is automatically saved to {{subject_id}}
```

---

### Step 3: Setup - Mark Sample Attendance

#### 3.1 Mark Attendance - Present
```
POST http://localhost:8081/api/teacher/attendance
Headers:
  Authorization: Bearer {{teacher_token}}
Body:
{
  "studentEmail": "student@example.com",
  "subjectId": "{{subject_id}}",
  "present": true
}

Expected Response:
{
  "id": "67901234abcd5678ef901235",
  "studentEmail": "student@example.com",
  "subjectId": "67901234abcd5678ef901234",
  "date": "2026-01-21",
  "present": true
}
```

---

### Step 4: Test Student Complex Queries

#### 4.1 Get Attendance by Subject ✅
```
GET http://localhost:8081/api/student/attendance/subject?email=student@example.com&subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: Array of attendance records for that subject
[
  {
    "id": "...",
    "studentEmail": "student@example.com",
    "subjectId": "...",
    "date": "2026-01-21",
    "present": true
  }
]

✅ Test: Should return only Mathematics attendance
```

#### 4.2 Get Attendance Percentage by Subject ✅
```
GET http://localhost:8081/api/student/attendance/subject/percentage?email=student@example.com&subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: 100.0 (if all present)

✅ Test: Should calculate percentage correctly
```

#### 4.3 Get Attendance Between Dates ✅
```
GET http://localhost:8081/api/student/attendance/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: Array of attendance records in January

✅ Test: Should filter by date range
```

#### 4.4 Get Attendance by Subject and Date Range ✅
```
GET http://localhost:8081/api/student/attendance/subject/daterange?email=student@example.com&subjectId={{subject_id}}&startDate=2026-01-01&endDate=2026-01-31
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: Mathematics attendance in January

✅ Test: Should combine subject and date filtering
```

#### 4.5 Get Attendance Percentage Between Dates ✅
```
GET http://localhost:8081/api/student/attendance/percentage/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: Percentage for January (e.g., 85.5)

✅ Test: Should calculate period-based percentage
```

#### 4.6 Get Total Present Days ✅
```
GET http://localhost:8081/api/student/attendance/present/count?email=student@example.com
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: 1 (or more if multiple attendance marked)

✅ Test: Should count only present days
```

#### 4.7 Get Total Absent Days ✅
```
GET http://localhost:8081/api/student/attendance/absent/count?email=student@example.com
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: 0 (or count of absent days)

✅ Test: Should count only absent days
```

#### 4.8 Check if Attendance Marked Today ✅
```
GET http://localhost:8081/api/student/attendance/today/check?email=student@example.com&subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{student_token}}

Expected Response: true

✅ Test: Should return true if marked today
```

---

### Step 5: Test Teacher Complex Queries

#### 5.1 Get All Attendance for Subject ✅
```
GET http://localhost:8081/api/teacher/attendance/subject?subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: All attendance records for Mathematics

✅ Test: Should return all students' attendance
```

#### 5.2 Get Attendance by Subject and Date ✅
```
GET http://localhost:8081/api/teacher/attendance/subject/date?subjectId={{subject_id}}&date=2026-01-21
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: Today's attendance for Mathematics

✅ Test: Should filter by specific date
```

#### 5.3 Get Attendance by Subject and Date Range ✅
```
GET http://localhost:8081/api/teacher/attendance/subject/daterange?subjectId={{subject_id}}&startDate=2026-01-01&endDate=2026-01-31
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: January attendance for Mathematics

✅ Test: Should return attendance for date range
```

#### 5.4 Get Present Students by Subject ✅
```
GET http://localhost:8081/api/teacher/attendance/subject/present?subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: Only present attendance records

✅ Test: Should filter only present students
```

#### 5.5 Get Absent Students by Subject ✅
```
GET http://localhost:8081/api/teacher/attendance/subject/absent?subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: Only absent attendance records

✅ Test: Should filter only absent students
```

#### 5.6 Get Student Attendance in Subject ✅
```
GET http://localhost:8081/api/teacher/attendance/student?studentEmail=student@example.com&subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: Specific student's attendance

✅ Test: Should return only that student's records
```

#### 5.7 Get Student Attendance Percentage in Subject ✅
```
GET http://localhost:8081/api/teacher/attendance/student/percentage?studentEmail=student@example.com&subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: 100.0

✅ Test: Should calculate student's percentage
```

#### 5.8 Get Today's Attendance ✅
```
GET http://localhost:8081/api/teacher/attendance/today?subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: Today's attendance records

✅ Test: Should return only today's records
```

#### 5.9 Check if Attendance Marked Today ✅
```
GET http://localhost:8081/api/teacher/attendance/today/check?studentEmail=student@example.com&subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: true

✅ Test: Should confirm attendance is marked
```

#### 5.10 Get Total Attendance Count ✅
```
GET http://localhost:8081/api/teacher/attendance/count?subjectId={{subject_id}}
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response: 1 (or total count)

✅ Test: Should count all attendance records
```

#### 5.11 Get Subjects by Teacher ✅
```
GET http://localhost:8081/api/teacher/subjects?teacherEmail=teacher@example.com
Headers:
  Authorization: Bearer {{teacher_token}}

Expected Response:
[
  {
    "id": "...",
    "name": "Mathematics",
    "teacherEmail": "teacher@example.com"
  }
]

✅ Test: Should list all teacher's subjects
```

---

### Step 6: Test Duplicate Prevention

#### 6.1 Mark Attendance Again (Should Update) ✅
```
POST http://localhost:8081/api/teacher/attendance
Headers:
  Authorization: Bearer {{teacher_token}}
Body:
{
  "studentEmail": "student@example.com",
  "subjectId": "{{subject_id}}",
  "present": false
}

Expected Response: Updated attendance record with present: false

✅ Test: Should UPDATE existing record, not create duplicate
✅ Verify: Run "Get Total Attendance Count" - should still be 1
```

---

## 🎯 Test Checklist

### Authentication Tests
- [ ] Teacher registration successful
- [ ] Student registration successful
- [ ] Teacher login returns valid JWT
- [ ] Student login returns valid JWT
- [ ] Tokens are automatically saved to variables

### Setup Tests
- [ ] Subject created successfully
- [ ] Subject ID saved to variable
- [ ] Attendance marked successfully

### Student Query Tests
- [ ] Get attendance by subject works
- [ ] Get attendance percentage by subject works
- [ ] Get attendance between dates works
- [ ] Get attendance by subject and date range works
- [ ] Get attendance percentage between dates works
- [ ] Get total present days works
- [ ] Get total absent days works
- [ ] Check attendance marked today works

### Teacher Query Tests
- [ ] Get all attendance for subject works
- [ ] Get attendance by subject and date works
- [ ] Get attendance by subject and date range works
- [ ] Get present students works
- [ ] Get absent students works
- [ ] Get student attendance in subject works
- [ ] Get student attendance percentage works
- [ ] Get today's attendance works
- [ ] Check attendance marked today works
- [ ] Get total attendance count works
- [ ] Get subjects by teacher works

### Duplicate Prevention Test
- [ ] Marking attendance twice updates instead of duplicating
- [ ] Total count remains 1 after duplicate attempt

---

## 🐛 Troubleshooting

### Issue: 401 Unauthorized
**Solution:** 
- Ensure you've logged in first
- Check that token is saved to variable
- Token format should be: `Bearer eyJhbGciOiJIUzI1NiJ9...`

### Issue: 403 Forbidden
**Solution:**
- Verify you're using the correct role's token
- Teacher endpoints require teacher token
- Student endpoints require student token

### Issue: 404 Not Found
**Solution:**
- Verify backend is running on port 8081
- Check the endpoint URL is correct
- Ensure subject_id variable is set

### Issue: Empty Response []
**Solution:**
- Ensure attendance has been marked first
- Check date range includes today's date
- Verify subject_id is correct

### Issue: Date Format Error
**Solution:**
- Use ISO date format: `YYYY-MM-DD`
- Example: `2026-01-21`

---

## 📊 Expected Results Summary

After running all tests, you should have:

1. **2 Users Created**
   - 1 Teacher (teacher@example.com)
   - 1 Student (student@example.com)

2. **1 Subject Created**
   - Mathematics

3. **1 Attendance Record**
   - For student@example.com in Mathematics
   - Status updated to latest marking

4. **All Endpoints Tested**
   - 8 Student endpoints ✅
   - 11 Teacher endpoints ✅
   - 1 Duplicate prevention test ✅

---

## 🎉 Success Criteria

✅ All requests return 200 OK (except auth errors)  
✅ Data is correctly filtered by subject  
✅ Data is correctly filtered by date range  
✅ Percentages are calculated accurately  
✅ Counts are correct  
✅ Duplicate prevention works  
✅ No duplicate attendance records created  

---

## 📝 Notes

- **Environment Variables**: The collection automatically manages:
  - `{{teacher_token}}` - Teacher JWT token
  - `{{student_token}}` - Student JWT token
  - `{{subject_id}}` - Created subject ID

- **Date Ranges**: Adjust `startDate` and `endDate` in queries to test different periods

- **Multiple Students**: To test with more data, register additional students and mark their attendance

---

**Happy Testing!** 🚀

*Collection: Complex_Queries_API.postman_collection.json*  
*Date: January 21, 2026*
