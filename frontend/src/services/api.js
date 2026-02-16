// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Helper function for API calls
import { getAuthToken } from './auth';

const apiCall = async (endpoint, options = {}) => {
    try {
        const { headers: optionHeaders, ...restOptions } = options;
        const token = getAuthToken();
        const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            cache: 'no-store',
            ...restOptions,
            headers: {
                'Content-Type': 'application/json',
                ...(optionHeaders || {}),
                ...authHeader,
            },
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Request failed' }));
            throw new Error(error.message || `HTTP ${response.status}`);
        }

        // Some endpoints (DELETE/204) return no content. Avoid JSON parse errors.
        if (response.status === 204) return null;

        const contentType = response.headers.get('content-type') || '';
        if (contentType.includes('application/json')) {
            const text = await response.text();
            return text ? JSON.parse(text) : null;
        }

        return await response.text();
    } catch (error) {
        console.error(`API Error [${endpoint}]:`, error);
        throw error;
    }
};

// ==================== ATTENDANCE API ====================
export const attendanceAPI = {
    // Get all attendance records
    getAll: () => apiCall('/attendance'),

    // Get attendance by ID
    getById: (id) => apiCall(`/attendance/${id}`),

    // Get today's attendance for employee
    getTodayAttendance: (employeeId) => {
        const today = new Date().toISOString().split('T')[0];
        return apiCall(`/attendance/employee/${employeeId}/date/${today}`);
    },

    // Get attendance by employee
    getByEmployee: (employeeId) => apiCall(`/attendance/employee/${employeeId}`),

    // Get attendance by date
    getByDate: (date) => apiCall(`/attendance/date/${date}`),

    // Get attendance by date range
    getByDateRange: (startDate, endDate) =>
        apiCall(`/attendance/date-range?startDate=${startDate}&endDate=${endDate}`),

    // Punch in
    punchIn: (employeeId) => apiCall(`/attendance/punch-in?employeeId=${employeeId}`, { method: 'POST' }),

    // Punch out
    punchOut: (employeeId) => apiCall(`/attendance/punch-out?employeeId=${employeeId}`, { method: 'POST' }),

    // Get monthly statistics
    getMonthlyStats: (employeeId, month, year) =>
        apiCall(`/attendance/employee/${employeeId}/month/${month}/year/${year}/stats`),

    // Delete attendance record
    delete: (id) => apiCall(`/attendance/${id}`, { method: 'DELETE' }),
};

// ==================== EMPLOYEE API ====================
export const employeeAPI = {
    getAll: () => apiCall('/employees'),
    getById: (id) => apiCall(`/employees/${id}`),
    create: (employee) => apiCall('/employees', { method: 'POST', body: JSON.stringify(employee) }),
    update: (id, employee) => apiCall(`/employees/${id}`, { method: 'PUT', body: JSON.stringify(employee) }),
    delete: (id) => apiCall(`/employees/${id}`, { method: 'DELETE' }),
};

// ==================== USER API ====================
export const userAPI = {
    getAll: () => apiCall('/users'),
    getById: (id) => apiCall(`/users/${id}`),
    getActive: () => apiCall('/users/active'),
    create: (user) => apiCall('/users', { method: 'POST', body: JSON.stringify(user) }),
    update: (id, user) => apiCall(`/users/${id}`, { method: 'PUT', body: JSON.stringify(user) }),
    delete: (id) => apiCall(`/users/${id}`, { method: 'DELETE' }),

    // Authentication
    login: (credentials) => apiCall('/users/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
    }),
};

// ==================== ADMIN AUTH API ====================
export const adminAuthAPI = {
    signup: (adminDetails) => apiCall('/admin-auth/signup', {
        method: 'POST',
        body: JSON.stringify(adminDetails)
    }),
    login: (credentials) => apiCall('/admin-auth/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
    }),
};

// ==================== PROJECT API ====================
export const projectAPI = {
    getAll: () => apiCall('/projects'),
    getById: (id) => apiCall(`/projects/${id}`),
    getActive: () => apiCall('/projects/active'),
    getByStatus: (status) => apiCall(`/projects/status/${status}`),
    searchByClient: (term) => apiCall(`/projects/search/client?term=${term}`),
    searchByName: (term) => apiCall(`/projects/search/project?term=${term}`),
    create: (project) => apiCall('/projects', { method: 'POST', body: JSON.stringify(project) }),
    update: (id, project) => apiCall(`/projects/${id}`, { method: 'PUT', body: JSON.stringify(project) }),
    updateStatus: (id, status) => apiCall(`/projects/${id}/status?status=${status}`, { method: 'PATCH' }),
    delete: (id) => apiCall(`/projects/${id}`, { method: 'DELETE' }),
};

// ==================== EMPLOYEE-PROJECT API ====================
export const employeeProjectAPI = {
    getAll: () => apiCall('/employee-projects'),
    getProjectsByEmployee: (employeeId) => apiCall(`/employee-projects/employee/${employeeId}/projects`),
    getEmployeesByProject: (projectId) => apiCall(`/employee-projects/project/${projectId}/employees`),
    assign: (employeeId, projectId) =>
        apiCall(`/employee-projects/assign?employeeId=${employeeId}&projectId=${projectId}`, { method: 'POST' }),
    remove: (employeeId, projectId) =>
        apiCall(`/employee-projects/remove?employeeId=${employeeId}&projectId=${projectId}`, { method: 'DELETE' }),
    getEmployeeCount: (projectId) => apiCall(`/employee-projects/project/${projectId}/count`),
    getProjectCount: (employeeId) => apiCall(`/employee-projects/employee/${employeeId}/count`),
};

// ==================== PAYROLL API ====================
export const payrollAPI = {
    getAll: () => apiCall('/payroll'),
    getById: (id) => apiCall(`/payroll/${id}`),
    getByEmployee: (employeeId) => apiCall(`/payroll/employee/${employeeId}`),
    getByMonth: (month, year) => apiCall(`/payroll/month/${month}/year/${year}`),
    getByEmployeeMonth: (employeeId, month, year) =>
        apiCall(`/payroll/employee/${employeeId}/month/${month}/year/${year}`),

    // Generate payroll automatically
    generate: (employeeId, month, year) =>
        apiCall(`/payroll/generate?employeeId=${employeeId}&month=${month}&year=${year}`, { method: 'POST' }),

    create: (payroll) => apiCall('/payroll', { method: 'POST', body: JSON.stringify(payroll) }),
    update: (id, payroll) => apiCall(`/payroll/${id}`, { method: 'PUT', body: JSON.stringify(payroll) }),
    delete: (id) => apiCall(`/payroll/${id}`, { method: 'DELETE' }),

    // Get total payroll
    getTotalForMonth: (month, year) => apiCall(`/payroll/total/month/${month}/year/${year}`),
};

// ==================== LEAVE API ====================
export const leaveAPI = {
    getAll: () => apiCall('/leaves'),
    getById: (id) => apiCall(`/leaves/${id}`),
    getByEmployee: (employeeId) => apiCall(`/leaves/employee/${employeeId}`),
    getByStatus: (status) => apiCall(`/leaves/status/${status}`),
    getPending: () => apiCall('/leaves/pending'),
    getByType: (type) => apiCall(`/leaves/type/${type}`),

    create: (leave) => apiCall('/leaves', { method: 'POST', body: JSON.stringify(leave) }),
    update: (id, leave) => apiCall(`/leaves/${id}`, { method: 'PUT', body: JSON.stringify(leave) }),
    delete: (id) => apiCall(`/leaves/${id}`, { method: 'DELETE' }),

    // Approve/Reject
    approve: (id) => apiCall(`/leaves/${id}/approve`, { method: 'PATCH' }),
    reject: (id) => apiCall(`/leaves/${id}/reject`, { method: 'PATCH' }),

    // Leave balance
    getBalance: (employeeId, year, quota = 20) =>
        apiCall(`/leaves/employee/${employeeId}/year/${year}/balance?quota=${quota}`),

    getTotalDays: (employeeId, year) =>
        apiCall(`/leaves/employee/${employeeId}/year/${year}/total-days`),
};

// ==================== ROLE API ====================
export const roleAPI = {
    getAll: () => apiCall('/roles'),
    getById: (id) => apiCall(`/roles/${id}`),
    getByName: (name) => apiCall(`/roles/name/${name}`),
    create: (role) => apiCall('/roles', { method: 'POST', body: JSON.stringify(role) }),
    update: (id, role) => apiCall(`/roles/${id}`, { method: 'PUT', body: JSON.stringify(role) }),
    delete: (id) => apiCall(`/roles/${id}`, { method: 'DELETE' }),
    initialize: () => apiCall('/roles/initialize', { method: 'POST' }),
};

// ==================== DASHBOARD API ====================
export const dashboardAPI = {
    // Persistent entries/notes for the Dashboard UI
    getEntries: () => apiCall('/dashboard/entries'),
    createEntry: (entry) => apiCall('/dashboard/entries', { method: 'POST', body: JSON.stringify(entry) }),
    deleteEntry: (id) => apiCall(`/dashboard/entries/${id}`, { method: 'DELETE' }),
};

// Export all APIs
export default {
    attendance: attendanceAPI,
    employee: employeeAPI,
    user: userAPI,
    adminAuth: adminAuthAPI,
    project: projectAPI,
    employeeProject: employeeProjectAPI,
    payroll: payrollAPI,
    leave: leaveAPI,
    role: roleAPI,
    dashboard: dashboardAPI,
};
