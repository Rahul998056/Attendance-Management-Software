import React from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import {
    LayoutDashboard,
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
    LogIn,
    UserPlus,
    LogOut,
    X
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
import api from './services/api';
import { clearCurrentUser, getCurrentUser, setCurrentUser, subscribeCurrentUser, setAuthToken, clearAuthToken } from './services/auth';

import './index.css';

const Sidebar = ({ isOpen, onClose }) => (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
        <div className="sidebar-logo">
            <ShieldCheck size={32} />
            <span>AMS</span>
        </div>

        <div className="sidebar-menu">
            <div className="menu-section">
                <p className="menu-label">Main</p>
                <NavLink to="/" end onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <LayoutDashboard size={18} />
                    <span>Dashboard</span>
                </NavLink>
                <NavLink to="/attendance" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Clock size={18} />
                    <span>Attendance</span>
                </NavLink>
            </div>

            <div className="menu-section">
                <p className="menu-label">Administration</p>
                <NavLink to="/employees" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Users size={18} />
                    <span>Employees</span>
                </NavLink>
                <NavLink to="/projects" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Briefcase size={18} />
                    <span>Projects</span>
                </NavLink>
            </div>

            <div className="menu-section">
                <p className="menu-label">Human Resources</p>
                <NavLink to="/payroll" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Wallet size={18} />
                    <span>Payroll</span>
                </NavLink>
                <NavLink to="/leaves" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <FileText size={18} />
                    <span>Leaves</span>
                </NavLink>
            </div>

            <div className="menu-section">
                <p className="menu-label">Settings</p>
                <NavLink to="/users" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <Users size={18} color="rgba(255,255,255,0.6)" />
                    <span>Users</span>
                </NavLink>
                <NavLink to="/roles" onClick={onClose} className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <ShieldCheck size={18} color="rgba(255,255,255,0.6)" />
                    <span>Roles</span>
                </NavLink>
            </div>
        </div>
    </aside>
);

const initialAuthForm = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    usernameOrEmail: ''
};

const Topbar = ({ user, onMenuToggle }) => {
    const [isUserMenuOpen, setUserMenuOpen] = React.useState(false);
    const [authMode, setAuthMode] = React.useState(null);
    const [authForm, setAuthForm] = React.useState(initialAuthForm);
    const [authError, setAuthError] = React.useState('');
    const [isSubmittingAuth, setSubmittingAuth] = React.useState(false);
    const userMenuRef = React.useRef(null);

    const isGuest = !user?.id;

    React.useEffect(() => {
        const handleClickOutside = (event) => {
            if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
                setUserMenuOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    React.useEffect(() => {
        if (!isGuest) {
            setAuthMode(null);
            setUserMenuOpen(false);
        }
    }, [isGuest]);

    const closeAuthModal = () => {
        setAuthMode(null);
        setAuthError('');
        setAuthForm(initialAuthForm);
    };
    const openAuthModal = (mode) => {
        setAuthError('');
        setAuthForm(initialAuthForm);
        setAuthMode(mode);
        setUserMenuOpen(false);
    };

    const handleAuthChange = (event) => {
        const { name, value } = event.target;
        setAuthForm((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const handleAuthSubmit = async (event) => {
        event.preventDefault();
        setAuthError('');

        if (authMode === 'signup' && authForm.password !== authForm.confirmPassword) {
            setAuthError('Password and confirm password do not match.');
            return;
        }

        setSubmittingAuth(true);
        try {
            if (authMode === 'signup') {
                const response = await api.adminAuth.signup({
                    username: authForm.username.trim(),
                    email: authForm.email.trim(),
                    password: authForm.password
                });
                if (response?.token) setAuthToken(response.token);
                setCurrentUser(response?.admin);
            } else {
                const response = await api.adminAuth.login({
                    usernameOrEmail: authForm.usernameOrEmail.trim(),
                    password: authForm.password
                });
                if (response?.token) setAuthToken(response.token);
                setCurrentUser(response?.admin);
            }
            closeAuthModal();
        } catch (error) {
            setAuthError((error && error.message) ? error.message : 'Authentication failed.');
        } finally {
            setSubmittingAuth(false);
        }
    };

    const handleLogout = () => {
        clearCurrentUser();
        clearAuthToken();
        setUserMenuOpen(false);
    };

    return (
        <header className="topbar">
            <div className="topbar-left">
                <button
                    type="button"
                    className="icon-btn menu-toggle"
                    onClick={onMenuToggle}
                    aria-label="Toggle navigation menu"
                >
                    <Menu size={18} />
                </button>
                <div className="search-bar">
                    <Search size={16} color="#718096" />
                    <input type="text" placeholder="Search here..." />
                </div>
            </div>

            <div className="topbar-actions">
                <button type="button" className="icon-btn static-action-btn" aria-label="Notifications" disabled>
                    <Bell size={20} />
                    <div className="badge-dot"></div>
                </button>
                <button type="button" className="icon-btn static-action-btn" aria-label="Comments" disabled>
                    <MessageSquare size={18} />
                    <div className="badge-dot"></div>
                </button>
                <div className="user-menu-wrapper" ref={userMenuRef}>
                    <button
                        type="button"
                        className="user-profile user-profile-btn"
                        onClick={() => setUserMenuOpen((prev) => !prev)}
                        aria-haspopup="menu"
                        aria-expanded={isUserMenuOpen}
                    >
                        <div className="avatar" style={{
                            backgroundImage: user.avatar ? `url(${user.avatar})` : 'none',
                            backgroundColor: '#E2E8F0',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 'bold',
                            color: '#4A5568'
                        }}>
                            {!user.avatar && (user.username || 'Guest').charAt(0).toUpperCase()}
                        </div>
                        <div style={{ textAlign: 'right' }}>
                            <p style={{ fontSize: '12px', fontWeight: '800', color: '#2D3748' }}>{user.username || 'Guest'}</p>
                            <p style={{ fontSize: '10px', color: '#718096', marginTop: '-2px' }}>{user.role || 'User'}</p>
                        </div>
                        <ChevronDown size={14} style={{ marginLeft: '10px', color: '#718096' }} />
                    </button>

                    {isUserMenuOpen && (
                        <div className="user-menu-dropdown" role="menu">
                            {isGuest ? (
                                <>
                                    <button type="button" className="user-menu-item" onClick={() => openAuthModal('login')}>
                                        <LogIn size={16} />
                                        <span>Admin Login</span>
                                    </button>
                                    <button type="button" className="user-menu-item" onClick={() => openAuthModal('signup')}>
                                        <UserPlus size={16} />
                                        <span>Admin Sign Up</span>
                                    </button>
                                </>
                            ) : (
                                <button type="button" className="user-menu-item danger" onClick={handleLogout}>
                                    <LogOut size={16} />
                                    <span>Logout</span>
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </div>

            {authMode && (
                <div className="auth-modal-backdrop" onClick={closeAuthModal}>
                    <div className="card auth-modal-card" onClick={(event) => event.stopPropagation()}>
                        <button type="button" className="auth-modal-close" onClick={closeAuthModal} aria-label="Close">
                            <X size={18} />
                        </button>
                        <h2>{authMode === 'login' ? 'Admin Login' : 'Admin Sign Up'}</h2>
                        <p className="auth-modal-subtitle">
                            {authMode === 'login'
                                ? 'Log in with your admin credentials.'
                                : 'Create an admin account and store credentials in database.'}
                        </p>

                        <form className="auth-modal-form" onSubmit={handleAuthSubmit}>
                            {authMode === 'signup' && (
                                <>
                                    <label className="form-label">Username</label>
                                    <input
                                        type="text"
                                        name="username"
                                        value={authForm.username}
                                        onChange={handleAuthChange}
                                        className="form-control"
                                        placeholder="Enter username"
                                        required
                                    />

                                    <label className="form-label">Email</label>
                                    <input
                                        type="email"
                                        name="email"
                                        value={authForm.email}
                                        onChange={handleAuthChange}
                                        className="form-control"
                                        placeholder="Enter email"
                                        required
                                    />
                                </>
                            )}

                            {authMode === 'login' && (
                                <>
                                    <label className="form-label">Username or Email</label>
                                    <input
                                        type="text"
                                        name="usernameOrEmail"
                                        value={authForm.usernameOrEmail}
                                        onChange={handleAuthChange}
                                        className="form-control"
                                        placeholder="Enter username or email"
                                        required
                                    />
                                </>
                            )}

                            <label className="form-label">Password</label>
                            <input
                                type="password"
                                name="password"
                                value={authForm.password}
                                onChange={handleAuthChange}
                                className="form-control"
                                placeholder="Enter password"
                                required
                            />

                            {authMode === 'signup' && (
                                <>
                                    <label className="form-label">Confirm Password</label>
                                    <input
                                        type="password"
                                        name="confirmPassword"
                                        value={authForm.confirmPassword}
                                        onChange={handleAuthChange}
                                        className="form-control"
                                        placeholder="Confirm password"
                                        required
                                    />
                                </>
                            )}

                            {authError && <p className="auth-error">{authError}</p>}

                            <button type="submit" className="punch-btn btn-primary auth-submit-btn" disabled={isSubmittingAuth}>
                                {isSubmittingAuth
                                    ? 'Please wait...'
                                    : authMode === 'login' ? 'Login as Admin' : 'Sign Up as Admin'}
                            </button>
                        </form>
                    </div>
                </div>
            )}
        </header>
    );
};

function App() {
    const [currentUser, setHeaderUser] = React.useState(() => getCurrentUser());
    const [isSidebarOpen, setSidebarOpen] = React.useState(false);

    React.useEffect(() => {
        const unsubscribe = subscribeCurrentUser(setHeaderUser);
        return unsubscribe;
    }, []);

    React.useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth > 992) {
                setSidebarOpen(false);
            }
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    React.useEffect(() => {
        if (!isSidebarOpen) return;

        const handleEscape = (event) => {
            if (event.key === 'Escape') {
                setSidebarOpen(false);
            }
        };

        window.addEventListener('keydown', handleEscape);
        return () => window.removeEventListener('keydown', handleEscape);
    }, [isSidebarOpen]);

    React.useEffect(() => {
        // Keep header data fresh if role/username changed in backend.
        if (!currentUser?.id || currentUser?.role === 'ADMIN') return;

        let cancelled = false;
        api.user.getById(currentUser.id)
            .then((user) => {
                if (!cancelled) {
                    setCurrentUser(user);
                }
            })
            .catch((err) => {
                console.error('Failed to refresh current user:', err);
            });

        return () => {
            cancelled = true;
        };
    }, [currentUser?.id]);

    return (
        <Router>
            <div className="layout-container">
                {isSidebarOpen && (
                    <button
                        type="button"
                        className="sidebar-overlay"
                        onClick={() => setSidebarOpen(false)}
                        aria-label="Close navigation menu"
                    />
                )}
                <Sidebar isOpen={isSidebarOpen} onClose={() => setSidebarOpen(false)} />
                <div className="main-area">
                    <Topbar user={currentUser} onMenuToggle={() => setSidebarOpen((prev) => !prev)} />
                    <Routes>
                        <Route path="/" element={<Dashboard />} />
                        <Route path="/attendance" element={<Attendance />} />
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
