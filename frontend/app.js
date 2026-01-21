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
        loadWeather('teacher-weather');
    } else if (role === 'STUDENT') {
        showPage('student-dashboard');
        document.getElementById('student-name').textContent = currentUser.name;
        loadStudentAttendance();
        loadWeather('student-weather');
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
            const errorData = await handleResponseError(response);
            showMessage('register-error', errorData.message || 'Registration failed');
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
            
            // Fetch full profile (name, location)
            await fetchUserProfile();
        } else {
            const errorData = await handleResponseError(response);
            showMessage('login-error', errorData.message || 'Invalid email or password');
        }
    } catch (error) {
        showMessage('login-error', 'Network error. Please check if the server is running.');
    } finally {
        hideSpinner('login-spinner');
    }
}

async function fetchUserProfile() {
    try {
        const response = await fetch(`${API_BASE_URL}/users/profile`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            currentUser = await response.json();
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            showDashboard(currentUser.role);
        }
    } catch (error) {
        console.error('Error fetching profile:', error);
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
            const errorData = await handleResponseError(response);
            console.error('Error response:', errorData);
            showMessage('subject-error', errorData.message || 'Failed to create subject');
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
            const errorData = await handleResponseError(response);
            showMessage('attendance-error', errorData.message || 'Failed to mark attendance');
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

// Helper to handle API error responses
async function handleResponseError(response) {
    try {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            
            // If it's a validation error response with fieldErrors
            if (data.fieldErrors && data.fieldErrors.length > 0) {
                const messages = data.fieldErrors.map(err => `${err.field}: ${err.message}`);
                return {
                    message: messages.join(' | '),
                    data: data
                };
            }
            
            return {
                message: data.message || data.error || 'Request failed',
                data: data
            };
        } else {
            const text = await response.text();
            return {
                message: text || `Error ${response.status}`,
                data: null
            };
        }
    } catch (e) {
        return {
            message: `Error ${response.status}`,
            data: null
        };
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

// ==================== ANALYTICS FUNCTIONS ====================

// Navigation Functions
function showStudentDashboard() {
    showPage('student-dashboard');
    loadStudentAttendance();
}

function showTeacherDashboard() {
    showPage('teacher-dashboard');
    loadTeacherSubjects();
}

function showStudentAnalytics() {
    showPage('student-analytics');
    document.getElementById('student-analytics-name').textContent = currentUser.name;
    loadStudentAnalyticsData();
}

function showTeacherAnalytics() {
    showPage('teacher-analytics');
    document.getElementById('teacher-analytics-name').textContent = currentUser.name;
    loadTeacherSubjectsForAnalytics();
}

// Student Analytics Functions
let studentTrendChart = null;

async function loadStudentAnalyticsData() {
    try {
        const response = await fetch(
            `${API_BASE_URL}/analytics/student/${encodeURIComponent(currentUser.email)}/overview`,
            {
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            }
        );

        if (response.ok) {
            const data = await response.json();
            displayStudentAnalytics(data);
            loadStudentTrends();
        } else {
            console.error('Failed to load student analytics');
        }
    } catch (error) {
        console.error('Error loading student analytics:', error);
    }
}

function displayStudentAnalytics(data) {
    // Update stats cards
    document.getElementById('analytics-overall-percentage').textContent = 
        `${data.overallPercentage.toFixed(1)}%`;
    document.getElementById('analytics-total-classes').textContent = data.totalClasses;
    document.getElementById('analytics-attended-classes').textContent = data.attendedClasses;

    // Display subject-wise performance
    const subjectGrid = document.getElementById('subject-performance-grid');
    
    if (!data.subjectWiseAttendance || data.subjectWiseAttendance.length === 0) {
        subjectGrid.innerHTML = '<div class="empty-state">No subject data available</div>';
        return;
    }

    subjectGrid.innerHTML = data.subjectWiseAttendance.map(subject => `
        <div class="subject-performance-card">
            <div class="subject-header">
                <div class="subject-title">${subject.subjectName}</div>
                <div class="subject-percentage">${subject.percentage.toFixed(1)}%</div>
            </div>
            <div class="progress-bar">
                <div class="progress-fill" style="width: ${subject.percentage}%"></div>
            </div>
            <div class="subject-stats">
                <div class="subject-stat">
                    <div class="subject-stat-label">Total</div>
                    <div class="subject-stat-value">${subject.totalClasses}</div>
                </div>
                <div class="subject-stat">
                    <div class="subject-stat-label">Attended</div>
                    <div class="subject-stat-value">${subject.attendedClasses}</div>
                </div>
                <div class="subject-stat">
                    <div class="subject-stat-label">Missed</div>
                    <div class="subject-stat-value">${subject.totalClasses - subject.attendedClasses}</div>
                </div>
            </div>
        </div>
    `).join('');
}

async function loadStudentTrends() {
    try {
        const response = await fetch(
            `${API_BASE_URL}/analytics/student/${encodeURIComponent(currentUser.email)}/trends?days=30`,
            {
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            }
        );

        if (response.ok) {
            const trends = await response.json();
            renderStudentTrendChart(trends);
        }
    } catch (error) {
        console.error('Error loading student trends:', error);
    }
}

function renderStudentTrendChart(trends) {
    const ctx = document.getElementById('studentTrendChart');
    
    if (!ctx) return;

    // Destroy existing chart if it exists
    if (studentTrendChart) {
        studentTrendChart.destroy();
    }

    // Sort trends by date
    trends.sort((a, b) => new Date(a.date) - new Date(b.date));

    const labels = trends.map(t => formatDate(t.date));
    const data = trends.map(t => t.attendancePercentage);

    studentTrendChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Attendance %',
                data: data,
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 4,
                pointHoverRadius: 6,
                pointBackgroundColor: '#667eea',
                pointBorderColor: '#fff',
                pointBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: '#1e293b',
                    titleColor: '#f1f5f9',
                    bodyColor: '#cbd5e1',
                    borderColor: '#334155',
                    borderWidth: 1,
                    padding: 12,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            return `Attendance: ${context.parsed.y.toFixed(1)}%`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        color: '#94a3b8',
                        callback: function(value) {
                            return value + '%';
                        }
                    },
                    grid: {
                        color: '#334155'
                    }
                },
                x: {
                    ticks: {
                        color: '#94a3b8',
                        maxRotation: 45,
                        minRotation: 45
                    },
                    grid: {
                        color: '#334155'
                    }
                }
            }
        }
    });
}

// Teacher Analytics Functions
let distributionChart = null;

async function loadTeacherSubjectsForAnalytics() {
    const select = document.getElementById('analytics-subject-select');
    select.innerHTML = '<option value="">Loading subjects...</option>';

    try {
        // Fetch all subjects (public endpoint)
        const response = await fetch(`${API_BASE_URL}/subjects/all`);
        
        if (response.ok) {
            const subjects = await response.json();
            
            // Filter subjects by current teacher's email
            const teacherSubjects = subjects.filter(subject => 
                subject.teacherEmail === currentUser.email
            );
            
            select.innerHTML = '<option value="">Choose a subject...</option>';
            
            if (teacherSubjects.length === 0) {
                select.innerHTML += '<option value="" disabled>No subjects found - Create subjects in dashboard</option>';
            } else {
                teacherSubjects.forEach(subject => {
                    const option = document.createElement('option');
                    option.value = subject.id;
                    option.textContent = subject.name;
                    select.appendChild(option);
                });
            }
        } else {
            select.innerHTML = '<option value="">Error loading subjects</option>';
        }
    } catch (error) {
        console.error('Error loading teacher subjects:', error);
        select.innerHTML = '<option value="">Error loading subjects</option>';
    }
}

async function loadTeacherAnalytics() {
    const subjectId = document.getElementById('analytics-subject-select').value;
    
    if (!subjectId) {
        document.getElementById('teacher-analytics-content').style.display = 'none';
        return;
    }

    document.getElementById('teacher-analytics-content').style.display = 'block';

    try {
        const response = await fetch(
            `${API_BASE_URL}/analytics/teacher/subject/${subjectId}/summary?threshold=75`,
            {
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            }
        );

        if (response.ok) {
            const data = await response.json();
            displayTeacherAnalytics(data);
        }
    } catch (error) {
        console.error('Error loading teacher analytics:', error);
    }
}

function displayTeacherAnalytics(data) {
    // Update stats
    document.getElementById('teacher-total-students').textContent = data.totalStudents;
    document.getElementById('teacher-total-classes').textContent = data.totalClasses;
    document.getElementById('teacher-avg-attendance').textContent = 
        `${data.averageAttendance.toFixed(1)}%`;

    // Render distribution chart
    renderDistributionChart(data.distribution);

    // Display defaulters
    displayDefaulters(data.defaulters);
}

function renderDistributionChart(distribution) {
    const ctx = document.getElementById('distributionChart');
    
    if (!ctx) return;

    // Destroy existing chart
    if (distributionChart) {
        distributionChart.destroy();
    }

    distributionChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Excellent (75-100%)', 'Good (50-75%)', 'Poor (0-50%)'],
            datasets: [{
                data: [distribution.excellent, distribution.good, distribution.poor],
                backgroundColor: [
                    '#10b981',
                    '#f59e0b',
                    '#ef4444'
                ],
                borderWidth: 0,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: '#1e293b',
                    titleColor: '#f1f5f9',
                    bodyColor: '#cbd5e1',
                    borderColor: '#334155',
                    borderWidth: 1,
                    padding: 12,
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            return `${label}: ${value} students`;
                        }
                    }
                }
            }
        }
    });
}

function displayDefaulters(defaulters) {
    const container = document.getElementById('defaulters-list');
    
    if (!defaulters || defaulters.length === 0) {
        container.innerHTML = `
            <div class="no-defaulters">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke-width="2"/>
                </svg>
                <h3>Great! No students below 75% attendance</h3>
                <p>All students are maintaining good attendance</p>
            </div>
        `;
        return;
    }

    container.innerHTML = defaulters.map(student => `
        <div class="defaulter-card">
            <div class="defaulter-info">
                <h4>${student.studentName}</h4>
                <p>${student.studentEmail}</p>
            </div>
            <div class="defaulter-stats">
                <div class="defaulter-percentage">${student.attendancePercentage.toFixed(1)}%</div>
                <div class="defaulter-attendance">${student.attendedClasses}/${student.totalClasses} classes</div>
            </div>
        </div>
    `).join('');
}

function renderWeather(container, data) {
    console.log(`Rendering weather for container ${container.id} with data:`, data);
    console.log(`Current user location state: ${currentUser?.location}`);
    const code = data.current_weather.weathercode;
    const temp = Math.round(data.current_weather.temperature);
    
    const weatherMap = {
        0:  { desc: 'Clear sky', icon: '01d' },
        1:  { desc: 'Mainly clear', icon: '02d' },
        2:  { desc: 'Partly cloudy', icon: '03d' },
        3:  { desc: 'Overcast', icon: '04d' },
        45: { desc: 'Fog', icon: '50d' },
        48: { desc: 'Depositing rime fog', icon: '50d' },
        51: { desc: 'Drizzle: Light', icon: '09d' },
        53: { desc: 'Drizzle: Moderate', icon: '09d' },
        55: { desc: 'Drizzle: Dense', icon: '09d' },
        61: { desc: 'Rain: Slight', icon: '10d' },
        63: { desc: 'Rain: Moderate', icon: '10d' },
        65: { desc: 'Rain: Heavy', icon: '10d' },
        71: { desc: 'Snow fall: Slight', icon: '13d' },
        73: { desc: 'Snow fall: Moderate', icon: '13d' },
        75: { desc: 'Snow fall: Heavy', icon: '13d' },
        80: { desc: 'Rain showers: Slight', icon: '09d' },
        81: { desc: 'Rain showers: Moderate', icon: '09d' },
        82: { desc: 'Rain showers: Violent', icon: '09d' },
        95: { desc: 'Thunderstorm', icon: '11d' }
    };

    const condition = weatherMap[code] || { desc: 'Unknown', icon: '01d' };
    const iconUrl = `https://openweathermap.org/img/wn/${condition.icon}.png`;
    
    container.innerHTML = `
        <div class="weather-main" onclick="toggleWeatherSearch('${container.id}')" title="Change Location">
            <img src="${iconUrl}" alt="${condition.desc}" class="weather-icon">
            <div class="weather-info">
                <span class="weather-temp">${temp}°C</span>
                <span class="weather-desc">${data.city || currentUser.location || 'London'}</span>
            </div>
        </div>
        <div class="weather-search" style="display: none;">
            <input type="text" placeholder="City..." class="weather-input" 
                   onkeyup="if(event.key === 'Enter') updateWeatherLocation('${container.id}', this.value)">
            <button class="weather-btn" onclick="const input = this.previousElementSibling; updateWeatherLocation('${container.id}', input.value)">OK</button>
        </div>
    `;
}

function toggleWeatherSearch(containerId) {
    const container = document.getElementById(containerId);
    const main = container.querySelector('.weather-main');
    const search = container.querySelector('.weather-search');
    
    if (search.style.display === 'none') {
        search.style.display = 'flex';
        main.style.display = 'none';
        search.querySelector('input').focus();
    } else {
        search.style.display = 'none';
        main.style.display = 'flex';
    }
}

async function updateWeatherLocation(containerId, newCity) {
    if (!newCity || newCity.trim() === '') return;
    
    const container = document.getElementById(containerId);
    container.innerHTML = '<span class="loading-dots">...</span>';
    
    console.log(`Updating weather location to: ${newCity}`);
    try {
        // 1. Save to backend
        const saveResponse = await fetch(`${API_BASE_URL}/users/profile/location`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ location: newCity })
        });
        
        if (saveResponse.ok) {
            const result = await saveResponse.json();
            currentUser.location = result.location;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            // 2. Fetch new weather with EXPLICIT city to avoid stale state issues
            await loadWeather(containerId, result.location);
        } else {
            console.error('Failed to save location preference');
            await loadWeather(containerId); // Refresh with old
        }
    } catch (error) {
        console.error('Error updating location:', error);
        await loadWeather(containerId);
    }
}

async function loadWeather(containerId, overrideCity = null) {
    const container = document.getElementById(containerId);
    if (!container) return;

    // Show loading state
    container.innerHTML = '<span class="loading-dots">...</span>';

    try {
        const city = overrideCity || currentUser?.location || 'London'; 
        console.log(`[Weather] Fetching for city: "${city}" (override: "${overrideCity}", user: "${currentUser?.location}")`);
        const response = await fetch(`${API_BASE_URL}/weather?city=${encodeURIComponent(city)}`);
        
        if (response.ok) {
            const data = await response.json();
            data.city = city; // Add city name for display
            console.log(`[Weather] Received data for: ${city}`, data);
            renderWeather(container, data);
        } else {
            console.error(`[Weather] API error for ${city}:`, response.status);
            container.innerHTML = `<span title="Failed to load weather" onclick="loadWeather('${containerId}')">⚠️</span>`;
        }
    } catch (error) {
        console.error('[Weather] Network error:', error);
        container.innerHTML = `<span title="Weather offline" onclick="loadWeather('${containerId}')">☁️</span>`;
    }
}
