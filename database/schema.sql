-- ========================================
-- ATTENDANCE MANAGEMENT SYSTEM DATABASE
-- ========================================
-- This schema supports the complete UI including:
-- - User Authentication (Login System)
-- - Employee Management
-- - Attendance Tracking (Punch In/Out)
-- - Break Time Management
-- - Overtime Calculation
-- - Project Assignment
-- - Payroll Processing
-- - Leave Management
-- ========================================

-- 1. ROLES TABLE
-- For managing user roles (ADMIN, HR, EMPLOYEE)
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default roles
INSERT INTO roles (role_name) VALUES 
    ('ADMIN'),
    ('HR'),
    ('EMPLOYEE');

-- 2. USERS TABLE
-- Authentication and login system
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. EMPLOYEES TABLE
-- Employee information linked to user accounts
CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    designation VARCHAR(100),
    department VARCHAR(100),
    joining_date DATE,
    salary DECIMAL(10,2),
    avatar VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_department (department),
    INDEX idx_designation (designation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. ATTENDANCE TABLE
-- Core attendance tracking with punch in/out
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    punch_in DATETIME,
    punch_out DATETIME,
    total_hours DECIMAL(5,2),
    overtime_hours DECIMAL(5,2),
    status VARCHAR(20), -- PRESENT, ABSENT, HALF_DAY
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_status (status),
    UNIQUE KEY unique_employee_date (employee_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. BREAKS TABLE
-- Track employee break times
CREATE TABLE breaks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    attendance_id BIGINT NOT NULL,
    break_start DATETIME,
    break_end DATETIME,
    break_duration DECIMAL(5,2), -- in hours
    FOREIGN KEY (attendance_id) REFERENCES attendance(id) ON DELETE CASCADE,
    INDEX idx_attendance_id (attendance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. PROJECTS TABLE
-- Project management
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(150),
    client_name VARCHAR(150),
    start_date DATE,
    end_date DATE,
    status VARCHAR(50), -- ACTIVE, COMPLETED, ON_HOLD
    INDEX idx_status (status),
    INDEX idx_client_name (client_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. EMPLOYEE_PROJECTS TABLE
-- Many-to-many relationship between employees and projects
CREATE TABLE employee_projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT,
    project_id BIGINT,
    assigned_date DATE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_project_id (project_id),
    UNIQUE KEY unique_employee_project (employee_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. PAYROLL TABLE
-- Salary and payroll management
CREATE TABLE payroll (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT,
    month VARCHAR(20),
    year INT,
    base_salary DECIMAL(10,2),
    overtime_pay DECIMAL(10,2),
    deductions DECIMAL(10,2),
    net_salary DECIMAL(10,2),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_month_year (month, year),
    UNIQUE KEY unique_employee_month_year (employee_id, month, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. LEAVES TABLE
-- Leave management system
CREATE TABLE leaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT,
    leave_type VARCHAR(50), -- SICK, CASUAL, ANNUAL, UNPAID
    start_date DATE,
    end_date DATE,
    reason TEXT,
    status VARCHAR(20), -- PENDING, APPROVED, REJECTED
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_leave_type (leave_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- SAMPLE DATA FOR TESTING
-- ========================================

-- Sample Users (Password should be hashed in production)
INSERT INTO users (username, email, password, role_id) VALUES
    ('admin', 'admin@company.com', 'admin123', 1),
    ('hr_manager', 'hr@company.com', 'hr123', 2),
    ('john.doe', 'john.doe@company.com', 'employee123', 3),
    ('jane.smith', 'jane.smith@company.com', 'employee123', 3);

-- Sample Employees
INSERT INTO employees (user_id, first_name, last_name, phone, designation, department, joining_date, salary) VALUES
    (3, 'John', 'Doe', '1234567890', 'Software Engineer', 'IT', '2023-01-15', 75000.00),
    (4, 'Jane', 'Smith', '0987654321', 'Senior Developer', 'IT', '2022-06-01', 95000.00);

-- Sample Projects
INSERT INTO projects (project_name, client_name, start_date, end_date, status) VALUES
    ('E-Commerce Platform', 'ABC Corp', '2024-01-01', '2024-12-31', 'ACTIVE'),
    ('Mobile App Development', 'XYZ Ltd', '2024-03-01', '2024-09-30', 'ACTIVE');

-- Sample Employee-Project Assignments
INSERT INTO employee_projects (employee_id, project_id, assigned_date) VALUES
    (1, 1, '2024-01-15'),
    (2, 1, '2024-01-15'),
    (2, 2, '2024-03-01');

-- ========================================
-- USEFUL QUERIES
-- ========================================

-- Get today's attendance with employee details
-- SELECT e.first_name, e.last_name, e.department, a.punch_in, a.punch_out, a.total_hours, a.overtime_hours, a.status
-- FROM attendance a
-- JOIN employees e ON a.employee_id = e.id
-- WHERE a.attendance_date = CURDATE();

-- Get employee's monthly attendance summary
-- SELECT 
--     e.first_name, e.last_name,
--     COUNT(*) as days_present,
--     SUM(a.total_hours) as total_hours_worked,
--     SUM(a.overtime_hours) as total_overtime
-- FROM attendance a
-- JOIN employees e ON a.employee_id = e.id
-- WHERE MONTH(a.attendance_date) = MONTH(CURDATE())
--   AND YEAR(a.attendance_date) = YEAR(CURDATE())
--   AND a.status = 'PRESENT'
-- GROUP BY e.id;

-- Get employee's break time for a specific date
-- SELECT 
--     e.first_name, e.last_name,
--     b.break_start, b.break_end, b.break_duration
-- FROM breaks b
-- JOIN attendance a ON b.attendance_id = a.id
-- JOIN employees e ON a.employee_id = e.id
-- WHERE a.attendance_date = CURDATE();

-- Get pending leave requests
-- SELECT 
--     e.first_name, e.last_name, e.department,
--     l.leave_type, l.start_date, l.end_date, l.reason
-- FROM leaves l
-- JOIN employees e ON l.employee_id = e.id
-- WHERE l.status = 'PENDING'
-- ORDER BY l.start_date;
