# 🚀 Quick Test Commands - Complex Queries

## Using cURL (Command Line Testing)

### 1. Setup
```bash
# Set variables
TEACHER_EMAIL="teacher@example.com"
STUDENT_EMAIL="student@example.com"
PASSWORD="password123"
BASE_URL="http://localhost:8081/api"
```

### 2. Register & Login
```bash
# Register Teacher
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Smith",
    "email": "'"$TEACHER_EMAIL"'",
    "password": "'"$PASSWORD"'",
    "role": "TEACHER"
  }'

# Register Student
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "'"$STUDENT_EMAIL"'",
    "password": "'"$PASSWORD"'",
    "role": "STUDENT"
  }'

# Login Teacher (save token)
TEACHER_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'"$TEACHER_EMAIL"'","password":"'"$PASSWORD"'"}' | tr -d '"')

# Login Student (save token)
STUDENT_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'"$STUDENT_EMAIL"'","password":"'"$PASSWORD"'"}' | tr -d '"')

echo "Teacher Token: $TEACHER_TOKEN"
echo "Student Token: $STUDENT_TOKEN"
```

### 3. Create Subject
```bash
# Create Mathematics subject
SUBJECT_RESPONSE=$(curl -s -X POST "$BASE_URL/teacher/subject" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TEACHER_TOKEN" \
  -d '{
    "name": "Mathematics",
    "teacherEmail": "'"$TEACHER_EMAIL"'"
  }')

# Extract subject ID (requires jq)
SUBJECT_ID=$(echo $SUBJECT_RESPONSE | jq -r '.id')
echo "Subject ID: $SUBJECT_ID"
```

### 4. Mark Attendance
```bash
# Mark attendance - Present
curl -X POST "$BASE_URL/teacher/attendance" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TEACHER_TOKEN" \
  -d '{
    "studentEmail": "'"$STUDENT_EMAIL"'",
    "subjectId": "'"$SUBJECT_ID"'",
    "present": true
  }'
```

### 5. Test Student Queries
```bash
# Get attendance by subject
curl -X GET "$BASE_URL/student/attendance/subject?email=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $STUDENT_TOKEN"

# Get attendance percentage by subject
curl -X GET "$BASE_URL/student/attendance/subject/percentage?email=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $STUDENT_TOKEN"

# Get attendance between dates
curl -X GET "$BASE_URL/student/attendance/daterange?email=$STUDENT_EMAIL&startDate=2026-01-01&endDate=2026-01-31" \
  -H "Authorization: Bearer $STUDENT_TOKEN"

# Get total present days
curl -X GET "$BASE_URL/student/attendance/present/count?email=$STUDENT_EMAIL" \
  -H "Authorization: Bearer $STUDENT_TOKEN"

# Get total absent days
curl -X GET "$BASE_URL/student/attendance/absent/count?email=$STUDENT_EMAIL" \
  -H "Authorization: Bearer $STUDENT_TOKEN"

# Check if attendance marked today
curl -X GET "$BASE_URL/student/attendance/today/check?email=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $STUDENT_TOKEN"
```

### 6. Test Teacher Queries
```bash
# Get all attendance for subject
curl -X GET "$BASE_URL/teacher/attendance/subject?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get attendance by subject and date
curl -X GET "$BASE_URL/teacher/attendance/subject/date?subjectId=$SUBJECT_ID&date=2026-01-21" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get attendance by subject and date range
curl -X GET "$BASE_URL/teacher/attendance/subject/daterange?subjectId=$SUBJECT_ID&startDate=2026-01-01&endDate=2026-01-31" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get present students
curl -X GET "$BASE_URL/teacher/attendance/subject/present?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get absent students
curl -X GET "$BASE_URL/teacher/attendance/subject/absent?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get student attendance in subject
curl -X GET "$BASE_URL/teacher/attendance/student?studentEmail=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get student percentage in subject
curl -X GET "$BASE_URL/teacher/attendance/student/percentage?studentEmail=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get today's attendance
curl -X GET "$BASE_URL/teacher/attendance/today?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get total attendance count
curl -X GET "$BASE_URL/teacher/attendance/count?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Get subjects by teacher
curl -X GET "$BASE_URL/teacher/subjects?teacherEmail=$TEACHER_EMAIL" \
  -H "Authorization: Bearer $TEACHER_TOKEN"
```

### 7. Test Duplicate Prevention
```bash
# Mark attendance again (should update, not duplicate)
curl -X POST "$BASE_URL/teacher/attendance" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TEACHER_TOKEN" \
  -d '{
    "studentEmail": "'"$STUDENT_EMAIL"'",
    "subjectId": "'"$SUBJECT_ID"'",
    "present": false
  }'

# Verify count is still 1
curl -X GET "$BASE_URL/teacher/attendance/count?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"
```

---

## 📋 Postman Quick Steps

### Import Collection
1. Open Postman
2. Click **Import**
3. Select `Complex_Queries_API.postman_collection.json`
4. Collection appears in sidebar

### Run Tests in Order
1. **Authentication** folder
   - Register Teacher
   - Register Student
   - Login Teacher (saves token)
   - Login Student (saves token)

2. **Setup - Create Subject** folder
   - Create Mathematics Subject (saves subject_id)

3. **Setup - Mark Sample Attendance** folder
   - Mark Attendance - Present

4. **Student Complex Queries** folder
   - Run all 8 requests

5. **Teacher Complex Queries** folder
   - Run all 11 requests

6. **Test Duplicate Prevention** folder
   - Mark Attendance Again

### View Results
- Green = Success (200 OK)
- Red = Error
- Check response body for data

---

## 🎯 Quick Validation

### After Running All Tests:

```bash
# Should return 1 (not 2, proving duplicate prevention works)
curl -X GET "$BASE_URL/teacher/attendance/count?subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $TEACHER_TOKEN"

# Should return 100.0 or 0.0 depending on last marking
curl -X GET "$BASE_URL/student/attendance/subject/percentage?email=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $STUDENT_TOKEN"

# Should return true
curl -X GET "$BASE_URL/student/attendance/today/check?email=$STUDENT_EMAIL&subjectId=$SUBJECT_ID" \
  -H "Authorization: Bearer $STUDENT_TOKEN"
```

---

## 📊 Expected Counts

After all tests:
- **Users**: 2 (1 teacher, 1 student)
- **Subjects**: 1 (Mathematics)
- **Attendance Records**: 1 (updated, not duplicated)
- **API Calls**: ~25 successful requests

---

## 🔍 Debugging

### Check Backend Logs
```bash
# In the terminal running the backend, look for:
# - "Started AttendanceSystemApplication"
# - "Tomcat started on port 8081"
# - No error stack traces
```

### Check MongoDB
```bash
# Connect to MongoDB
mongosh

# Use database
use attendance_db

# Check collections
db.users.countDocuments()      # Should be 2
db.subjects.countDocuments()   # Should be 1
db.attendance.countDocuments() # Should be 1

# View attendance
db.attendance.find().pretty()
```

---

## ✅ Success Indicators

- All Postman requests show green checkmarks
- Response times < 500ms
- No 401/403 errors (after login)
- Attendance count stays at 1 after duplicate test
- Percentages calculate correctly
- Date filtering returns correct records

---

**Quick Reference Complete!** 🎉

*Use this for rapid testing and validation*
