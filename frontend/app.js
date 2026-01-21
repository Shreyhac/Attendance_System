// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// State Management
let currentUser = null;
let authToken = null;

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
});

// Check if user is already logged in
function checkAuth() {
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');
    
    console.log('Checking auth - Token exists:', !!token);
    console.log('Checking auth - User exists:', !!user);
    
    if (token && user) {
        authToken = token;
        currentUser = JSON.parse(user);
        console.log('User logged in:', currentUser);
        showDashboard(currentUser.role);
    } else {
        console.log('No user logged in');
    }
}

// Page Navigation
function showPage(pageId) {
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    document.getElementById(pageId).classList.add('active');
}

function showLanding() {
    showPage('landing-page');
}

function showLogin() {
    showPage('login-page');
    clearForm('login-form');
    hideMessage('login-error');
}

function showRegister() {
    showPage('register-page');
    clearForm('register-form');
    hideMessage('register-error');
    hideMessage('register-success');
}

function showDashboard(role) {
    if (role === 'TEACHER') {
        showPage('teacher-dashboard');
        document.getElementById('teacher-name').textContent = currentUser.name;
        loadTeacherSubjects();
    } else if (role === 'STUDENT') {
        showPage('student-dashboard');
        document.getElementById('student-name').textContent = currentUser.name;
        loadStudentAttendance();
    }
}

// Authentication Handlers
async function handleRegister(event) {
    event.preventDefault();
    
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const role = document.getElementById('register-role').value;
    
    showSpinner('register-spinner');
    hideMessage('register-error');
    hideMessage('register-success');
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, email, password, role })
        });
        
        if (response.ok) {
            const message = await response.text();
            showMessage('register-success', message);
            
            // Auto-login after 1.5 seconds
            setTimeout(() => {
                document.getElementById('login-email').value = email;
                document.getElementById('login-password').value = password;
                showLogin();
            }, 1500);
        } else {
            const error = await response.text();
            showMessage('register-error', error || 'Registration failed');
        }
    } catch (error) {
        showMessage('register-error', 'Network error. Please check if the server is running.');
    } finally {
        hideSpinner('register-spinner');
    }
}

async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    
    showSpinner('login-spinner');
    hideMessage('login-error');
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const token = await response.text();
            authToken = token.replace(/"/g, ''); // Remove quotes
            
            // Decode JWT to get user info
            const payload = JSON.parse(atob(authToken.split('.')[1]));
            currentUser = {
                email: payload.sub,
                role: payload.role,
                name: email.split('@')[0] // Simple name extraction
            };
            
            // Save to localStorage
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            // Show appropriate dashboard
            showDashboard(currentUser.role);
        } else {
            showMessage('login-error', 'Invalid email or password');
        }
    } catch (error) {
        showMessage('login-error', 'Network error. Please check if the server is running.');
    } finally {
        hideSpinner('login-spinner');
    }
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    authToken = null;
    currentUser = null;
    showLanding();
}

// Teacher Functions
async function handleCreateSubject(event) {
    event.preventDefault();
    
    const name = document.getElementById('subject-name').value;
    
    showSpinner('subject-spinner');
    hideMessage('subject-error');
    hideMessage('subject-success');
    
    try {
        console.log('=== Creating Subject ===');
        console.log('Token:', authToken ? authToken.substring(0, 20) + '...' : 'NO TOKEN');
        console.log('Current User:', currentUser);
        console.log('Request payload:', { name, teacherEmail: currentUser.email });
        
        const response = await fetch(`${API_BASE_URL}/teacher/subject`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                name,
                teacherEmail: currentUser.email
            })
        });
        
        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        
        if (response.ok) {
            const subject = await response.json();
            console.log('Subject created:', subject);
            showMessage('subject-success', `Subject "${subject.name}" created successfully!`);
            document.getElementById('subject-name').value = '';
            
            // Reload subjects list
            loadTeacherSubjects();
            
            // Update subject dropdown
            const option = document.createElement('option');
            option.value = subject.id;
            option.textContent = subject.name;
            document.getElementById('subject-select').appendChild(option);
        } else {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            showMessage('subject-error', `Error ${response.status}: ${errorText || 'Failed to create subject'}`);
        }
    } catch (error) {
        console.error('Network error details:', error);
        console.error('Error name:', error.name);
        console.error('Error message:', error.message);
        
        // More specific error message
        let errorMsg = 'Network error: ';
        if (error.message.includes('Failed to fetch')) {
            errorMsg += 'Cannot connect to server. Make sure backend is running on port 8081.';
        } else {
            errorMsg += error.message;
        }
        showMessage('subject-error', errorMsg);
    } finally {
        hideSpinner('subject-spinner');
    }
}

async function handleMarkAttendance(event) {
    event.preventDefault();
    
    const studentEmail = document.getElementById('student-email').value;
    const subjectId = document.getElementById('subject-select').value;
    const present = document.querySelector('input[name="attendance-status"]:checked').value === 'true';
    
    showSpinner('attendance-spinner');
    hideMessage('attendance-error');
    hideMessage('attendance-success');
    
    try {
        const response = await fetch(`${API_BASE_URL}/teacher/attendance`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                studentEmail,
                subjectId,
                present
            })
        });
        
        if (response.ok) {
            const attendance = await response.json();
            const status = attendance.present ? 'Present' : 'Absent';
            showMessage('attendance-success', `Attendance marked as ${status} for ${studentEmail}`);
            document.getElementById('student-email').value = '';
        } else {
            const error = await response.text();
            showMessage('attendance-error', error || 'Failed to mark attendance');
        }
    } catch (error) {
        showMessage('attendance-error', 'Network error. Please try again.');
    } finally {
        hideSpinner('attendance-spinner');
    }
}

async function loadTeacherSubjects() {
    const container = document.getElementById('subjects-list');
    container.innerHTML = '<div class="loading-state">Loading subjects...</div>';
    
    try {
        // Note: This endpoint doesn't exist in the backend yet
        // For now, we'll show a message
        container.innerHTML = `
            <div class="empty-state">
                <p>Subjects you create will appear here.</p>
                <p style="margin-top: 0.5rem; font-size: 0.875rem;">Create a subject using the form above.</p>
            </div>
        `;
    } catch (error) {
        container.innerHTML = '<div class="empty-state">Failed to load subjects</div>';
    }
}

// Student Functions
async function loadStudentAttendance() {
    const container = document.getElementById('attendance-records');
    container.innerHTML = '<div class="loading-state">Loading attendance records...</div>';
    
    try {
        const response = await fetch(
            `${API_BASE_URL}/student/attendance?email=${encodeURIComponent(currentUser.email)}`,
            {
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            }
        );
        
        if (response.ok) {
            const records = await response.json();
            displayAttendanceRecords(records);
            loadAttendancePercentage();
        } else {
            container.innerHTML = '<div class="empty-state">Failed to load attendance records</div>';
        }
    } catch (error) {
        container.innerHTML = '<div class="empty-state">Network error. Please try again.</div>';
    }
}

async function displayAttendanceRecords(records) {
    const container = document.getElementById('attendance-records');
    
    if (records.length === 0) {
        container.innerHTML = '<div class="empty-state">No attendance records found</div>';
        document.getElementById('total-records').textContent = '0';
        return;
    }
    
    document.getElementById('total-records').textContent = records.length;
    
    // Get unique subject IDs
    const subjectIds = [...new Set(records.map(r => r.subjectId))];
    const subjectNames = {};
    
    // Fetch subject names for all unique subject IDs
    try {
        const fetchPromises = subjectIds.map(async (subjectId) => {
            try {
                const response = await fetch(`${API_BASE_URL}/subjects/${subjectId}`);
                if (response.ok) {
                    const subject = await response.json();
                    subjectNames[subjectId] = subject.name || 'Unknown Subject';
                } else {
                    subjectNames[subjectId] = `Subject ${subjectId.substring(0, 8)}...`;
                }
            } catch (error) {
                console.error(`Error fetching subject ${subjectId}:`, error);
                subjectNames[subjectId] = `Subject ${subjectId.substring(0, 8)}...`;
            }
        });
        
        await Promise.all(fetchPromises);
    } catch (error) {
        console.error('Error fetching subject names:', error);
    }
    
    const table = `
        <table class="attendance-table">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Subject</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                ${records.map(record => `
                    <tr>
                        <td>${formatDate(record.date)}</td>
                        <td>${subjectNames[record.subjectId] || record.subjectId}</td>
                        <td>
                            <span class="status-badge ${record.present ? 'present' : 'absent'}">
                                ${record.present ? 'Present' : 'Absent'}
                            </span>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function loadAttendancePercentage() {
    try {
        const response = await fetch(
            `${API_BASE_URL}/student/attendance/percentage?email=${encodeURIComponent(currentUser.email)}`,
            {
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            }
        );
        
        if (response.ok) {
            const percentage = await response.text();
            document.getElementById('attendance-percentage').textContent = 
                `${parseFloat(percentage).toFixed(1)}%`;
        }
    } catch (error) {
        document.getElementById('attendance-percentage').textContent = '--';
    }
}

// Utility Functions
function showMessage(elementId, message) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.classList.add('show');
}

function hideMessage(elementId) {
    const element = document.getElementById(elementId);
    element.classList.remove('show');
}

function showSpinner(spinnerId) {
    const spinner = document.getElementById(spinnerId);
    const button = spinner.closest('button');
    spinner.classList.add('show');
    button.disabled = true;
}

function hideSpinner(spinnerId) {
    const spinner = document.getElementById(spinnerId);
    const button = spinner.closest('button');
    spinner.classList.remove('show');
    button.disabled = false;
}

function clearForm(formId) {
    document.getElementById(formId).reset();
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}
