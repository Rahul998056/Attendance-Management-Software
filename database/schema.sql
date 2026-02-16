-- ========================================
-- ATTENDANCE MANAGEMENT SYSTEM DATABASE
-- ========================================
-- This schema supports the complete UI including:
-- - User Authentication (Login System)
-- - Employee Management
-- - Attendance Tracking (Punch In/Out)
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

-- 2. ADMIN_CREDENTIALS TABLE
-- Already defined above with the 2. ADMIN_CREDENTIALS section

-- 2. USERS TABLE
-- Authentication and login system (Sub-users managed by Admin)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL,
    INDEX idx_admin_id (admin_id),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. EMPLOYEES TABLE
-- Employee information linked to user accounts (belongs to Admin)
CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    user_id BIGINT UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    designation VARCHAR(100),
    department VARCHAR(100),
    joining_date DATE,
    salary DECIMAL(10,2),
    avatar VARCHAR(255),
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_user_id (user_id),
    INDEX idx_department (department),
    INDEX idx_designation (designation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.1 ADMIN_CREDENTIALS TABLE
-- Dedicated admin login/signup credentials (topbar Guest -> Admin auth)
CREATE TABLE admin_credentials (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_username (username),
    INDEX idx_admin_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. ATTENDANCE TABLE
-- Core attendance tracking with punch in/out (belongs to Admin via Employee)
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    punch_in DATETIME,
    punch_out DATETIME,
    total_hours DECIMAL(5,2),
    overtime_hours DECIMAL(5,2),
    status VARCHAR(20), -- PRESENT, ABSENT, HALF_DAY
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_status (status),
    UNIQUE KEY unique_employee_date (employee_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. PROJECTS TABLE
-- Project management (belongs to Admin)
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    project_name VARCHAR(150),
    client_name VARCHAR(150),
    start_date DATE,
    end_date DATE,
    status VARCHAR(50), -- ACTIVE, COMPLETED, ON_HOLD
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_status (status),
    INDEX idx_client_name (client_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. EMPLOYEE_PROJECTS TABLE
-- Many-to-many relationship between employees and projects (belongs to Admin)
CREATE TABLE employee_projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    employee_id BIGINT,
    project_id BIGINT,
    assigned_date DATE,
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_project_id (project_id),
    UNIQUE KEY unique_employee_project (employee_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. PAYROLL TABLE
-- Salary and payroll management (belongs to Admin via Employee)
CREATE TABLE payroll (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    employee_id BIGINT,
    month VARCHAR(20),
    year INT,
    base_salary DECIMAL(10,2),
    overtime_pay DECIMAL(10,2),
    deductions DECIMAL(10,2),
    net_salary DECIMAL(10,2),
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_month_year (month, year),
    UNIQUE KEY unique_employee_month_year (employee_id, month, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. LEAVES TABLE
-- Leave management system (belongs to Admin via Employee)
CREATE TABLE leaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    employee_id BIGINT,
    leave_type VARCHAR(50), -- SICK, CASUAL, ANNUAL, UNPAID
    start_date DATE,
    end_date DATE,
    reason TEXT,
    status VARCHAR(20), -- PENDING, APPROVED, REJECTED
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_leave_type (leave_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. DASHBOARD_ENTRIES TABLE
-- Simple persistent entries/notes displayed on the Dashboard UI (belongs to Admin)
CREATE TABLE dashboard_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_credentials(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id),
    INDEX idx_dashboard_entries_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- SAMPLE DATA FOR TESTING
-- ========================================

-- Sample Admin Credentials (Password should be hashed in production)
-- Note: The project includes a seeder that creates a default admin if missing.
-- Default values are configured in `src/main/resources/application.properties` under `admin.seed.*`
INSERT INTO admin_credentials (username, email, password) VALUES
    ('superadmin', 'superadmin@company.com', '$2a$10$DummyHash1234567890123456789'); -- DO NOT use in production

-- Sample Sub-Users (managed by Admin with id=1)
INSERT INTO users (admin_id, username, email, password, role_id) VALUES
    (1, 'hr_manager', 'hr@company.com', 'hr123', 2),
    (1, 'john.doe', 'john.doe@company.com', 'employee123', 3),
    (1, 'jane.smith', 'jane.smith@company.com', 'employee123', 3);

-- Sample Employees (belonging to Admin with id=1)
INSERT INTO employees (admin_id, user_id, first_name, last_name, phone, designation, department, joining_date, salary) VALUES
    (1, 2, 'John', 'Doe', '1234567890', 'Software Engineer', 'IT', '2023-01-15', 75000.00),
    (1, 3, 'Jane', 'Smith', '0987654321', 'Senior Developer', 'IT', '2022-06-01', 95000.00);

-- Sample Projects (belonging to Admin with id=1)
INSERT INTO projects (admin_id, project_name, client_name, start_date, end_date, status) VALUES
    (1, 'E-Commerce Platform', 'ABC Corp', '2024-01-01', '2024-12-31', 'ACTIVE'),
    (1, 'Mobile App Development', 'XYZ Ltd', '2024-03-01', '2024-09-30', 'ACTIVE');

-- Sample Employee-Project Assignments (belonging to Admin with id=1)
INSERT INTO employee_projects (admin_id, employee_id, project_id, assigned_date) VALUES
    (1, 1, 1, '2024-01-15'),
    (1, 2, 1, '2024-01-15'),
    (1, 2, 2, '2024-03-01');



