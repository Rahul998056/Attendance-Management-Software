import React from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import {
    LayoutDashboard,
    Grid,
    Users,
    Briefcase,
    Wallet,
    FileText,
    ShieldCheck,
    Menu,
    Search,
    Bell,
    MessageSquare,
    ChevronDown,
    Clock,
    LogOut
} from 'lucide-react';

// Pages
import Dashboard from './pages/Dashboard';
import Employees from './pages/Employees';
import Projects from './pages/Projects';
import Payroll from './pages/Payroll';
import Leaves from './pages/Leaves';
import Attendance from './pages/Attendance';
import UsersManagement from './pages/Users';
import Roles from './pages/Roles';
import Breaks from './pages/Breaks';

import './index.css';

const Sidebar = () => (
    <aside className="sidebar">
        <div className="sidebar-logo">
            <ShieldCheck size={32} />
            <span>AMS</span>
        </div>

        <div className="sidebar-menu">
            <div className="menu-section">
                <p className="menu-label">Main</p>
                <NavLink to="/" end className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <LayoutDashboard size={18} />
                    <span>Dashboard</span>
                </NavLink>
                <NavLink to="/attendance" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Clock size={18} />
                    <span>Attendance</span>
                </NavLink>
                <NavLink to="/breaks" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Clock size={18} color="#F6AD55" />
                    <span>Breaks</span>
                </NavLink>
            </div>

            <div className="menu-section">
                <p className="menu-label">Administration</p>
                <NavLink to="/employees" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Users size={18} />
                    <span>Employees</span>
                </NavLink>
                <NavLink to="/projects" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Briefcase size={18} />
                    <span>Projects</span>
                </NavLink>
            </div>

            <div className="menu-section">
                <p className="menu-label">Human Resources</p>
                <NavLink to="/payroll" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Wallet size={18} />
                    <span>Payroll</span>
                </NavLink>
                <NavLink to="/leaves" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <FileText size={18} />
                    <span>Leaves</span>
                </NavLink>
            </div>

            <div className="menu-section">
                <p className="menu-label">Settings</p>
                <NavLink to="/users" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Users size={18} color="rgba(255,255,255,0.6)" />
                    <span>Users</span>
                </NavLink>
                <NavLink to="/roles" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <ShieldCheck size={18} color="rgba(255,255,255,0.6)" />
                    <span>Roles</span>
                </NavLink>
            </div>
        </div>
    </aside>
);

const Topbar = ({ user }) => (
    <header className="topbar">
        <div className="search-bar">
            <Menu size={18} className="icon-btn" />
            <Search size={16} color="#718096" />
            <input type="text" placeholder="Search here..." />
        </div>

        <div className="topbar-actions">
            <div className="icon-btn">
                <Bell size={20} />
                <div className="badge-dot"></div>
            </div>
            <div className="icon-btn">
                <MessageSquare size={18} />
                <div className="badge-dot"></div>
            </div>
            <div className="user-profile">
                <div className="avatar" style={{
                    backgroundImage: user.avatar ? `url(${user.avatar})` : 'none',
                    backgroundColor: '#E2E8F0',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 'bold',
                    color: '#4A5568'
                }}>
                    {!user.avatar && user.username?.charAt(0).toUpperCase()}
                </div>
                <div style={{ textAlign: 'right' }}>
                    <p style={{ fontSize: '12px', fontWeight: '800', color: '#2D3748' }}>{user.username || 'Guest'}</p>
                    <p style={{ fontSize: '10px', color: '#718096', marginTop: '-2px' }}>{user.role || 'User'}</p>
                </div>
                <ChevronDown size={14} style={{ marginLeft: '10px', color: '#718096' }} />
            </div>
        </div>
    </header>
);

function App() {
    // This would typically come from an Auth Provider/localStorage
    const [currentUser, setCurrentUser] = React.useState({
        username: 'Rahul Kumar',
        role: 'Administrator',
        avatar: 'https://i.pravatar.cc/150?u=rahul'
    });

    return (
        <Router>
            <div className="layout-container">
                <Sidebar />
                <div className="main-area">
                    <Topbar user={currentUser} />
                    <Routes>
                        <Route path="/" element={<Dashboard />} />
                        <Route path="/attendance" element={<Attendance />} />
                        <Route path="/breaks" element={<Breaks />} />
                        <Route path="/employees" element={<Employees />} />
                        <Route path="/projects" element={<Projects />} />
                        <Route path="/payroll" element={<Payroll />} />
                        <Route path="/leaves" element={<Leaves />} />
                        <Route path="/users" element={<UsersManagement />} />
                        <Route path="/roles" element={<Roles />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
