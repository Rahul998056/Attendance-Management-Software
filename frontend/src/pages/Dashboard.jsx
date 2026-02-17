import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import {
    Users,
    Briefcase,
    Clock,
    DollarSign,
    CheckCircle,
    Trash2,
    Plus
} from 'lucide-react';

import PunchCard from '../components/PunchCard';

const Dashboard = () => {
    const [stats, setStats] = useState({
        totalEmployees: 0,
        activeProjects: 0,
        pendingLeaves: 0,
        todayAttendance: 0,
        totalPayroll: 0
    });
    const [recentAttendance, setRecentAttendance] = useState([]);
    const [loading, setLoading] = useState(true);

    const [dashboardEntries, setDashboardEntries] = useState([]);
    const [entriesLoading, setEntriesLoading] = useState(true);
    const [entryTitle, setEntryTitle] = useState('');
    const [entryMessage, setEntryMessage] = useState('');
    const [entrySaving, setEntrySaving] = useState(false);

    useEffect(() => {
        const fetchStats = async () => {
            setLoading(true);
            try {
                const currentMonth = new Intl.DateTimeFormat('en-US', { month: 'long' }).format(new Date());
                const currentYear = new Date().getFullYear();

                // Fetch stats individually
                const [employeesRes, projectsRes, leavesRes, attendanceRes, payrollTotalRes] = await Promise.allSettled([
                    api.employee.getAll(),
                    api.project.getActive(),
                    api.leave.getPending(),
                    api.attendance.getByDate(new Date().toLocaleDateString('en-CA')),
                    api.payroll.getTotalForMonth(currentMonth, currentYear)
                ]);

                setStats({
                    totalEmployees: employeesRes.status === 'fulfilled' ? (Array.isArray(employeesRes.value) ? employeesRes.value.length : 0) : 'Error',
                    activeProjects: projectsRes.status === 'fulfilled' ? (Array.isArray(projectsRes.value) ? projectsRes.value.length : 0) : 'Error',
                    pendingLeaves: leavesRes.status === 'fulfilled' ? (Array.isArray(leavesRes.value) ? leavesRes.value.length : 0) : 'Error',
                    todayAttendance: attendanceRes.status === 'fulfilled' ? (Array.isArray(attendanceRes.value) ? attendanceRes.value.length : 0) : 'Error',
                    totalPayroll: payrollTotalRes.status === 'fulfilled' ? (Number(payrollTotalRes.value?.totalPayroll) || 0) : 0
                });

                if (attendanceRes.status === 'fulfilled') {
                    setRecentAttendance(Array.isArray(attendanceRes.value) ? attendanceRes.value.slice(0, 5) : []);
                }
            } catch (err) {
                console.error('Critical Error in dashboard:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

    useEffect(() => {
        const fetchEntries = async () => {
            setEntriesLoading(true);
            try {
                const data = await api.dashboard.getEntries();
                setDashboardEntries(Array.isArray(data) ? data : []);
            } catch (err) {
                console.error('Error fetching dashboard entries:', err);
                setDashboardEntries([]);
            } finally {
                setEntriesLoading(false);
            }
        };

        fetchEntries();
    }, []);

    const handleAddEntry = async (e) => {
        e.preventDefault();
        const title = entryTitle.trim();
        const message = entryMessage.trim();

        if (!title) {
            alert('Title is required.');
            return;
        }

        setEntrySaving(true);
        try {
            const created = await api.dashboard.createEntry({ title, message: message || null });
            if (created) {
                setDashboardEntries((prev) => [created, ...prev]);
            }
            setEntryTitle('');
            setEntryMessage('');
        } catch (err) {
            alert('Error adding entry: ' + err.message);
        } finally {
            setEntrySaving(false);
        }
    };

    const handleDeleteEntry = async (id) => {
        if (!window.confirm('Delete this dashboard entry?')) return;
        try {
            await api.dashboard.deleteEntry(id);
            setDashboardEntries((prev) => prev.filter((e) => e.id !== id));
        } catch (err) {
            alert('Error deleting entry: ' + err.message);
        }
    };

    const cards = [
        { title: 'Total Employees', value: stats.totalEmployees, icon: <Users color="#4FD1C5" />, bg: '#4FD1C520', link: '/employees' },
        { title: 'Active Projects', value: stats.activeProjects, icon: <Briefcase color="#4299e1" />, bg: '#4299e120', link: '/projects' },
        { title: 'Monthly Payroll', value: `$${stats.totalPayroll.toLocaleString()}`, icon: <DollarSign color="#3476E1" />, bg: '#3476E120', link: '/payroll' },
        { title: 'Today Attendance', value: stats.todayAttendance, icon: <Clock color="#48bb78" />, bg: '#48bb7820', link: '/attendance' }
    ];

    const navigate = useNavigate();

    return (
        <main className="content">
            <div className="content-header">
                <h1>AMS Dashboard</h1>
                <p className="breadcrumb">Main / <span>Dashboard</span></p>
            </div>

            <div className="dashboard-stat-cards">
                {cards.map((card, i) => (
                    <Link to={card.link} key={i} className="card stat-card">
                        <div className="stat-card-content">
                            <div className="stat-card-text">
                                <p className="stat-card-label">{card.title}</p>
                                <h2 className="stat-card-value">{loading ? '...' : card.value}</h2>
                            </div>
                            <div className="stat-card-icon" style={{ background: card.bg }}>
                                {React.cloneElement(card.icon, { size: 24 })}
                            </div>
                        </div>
                    </Link>
                ))}
            </div>

            <div className="dashboard-grid">
                <div className="dashboard-left">
                    {/* Recent Activity Table */}
                    <section className="card">
                        <div className="card-title-row">
                            <span>Recent Attendance Activity</span>
                            <Link to="/attendance" className="view-all-link">View All</Link>
                        </div>
                        <div className="table-wrapper">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Employee</th>
                                        <th>Time</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {recentAttendance.map((record, idx) => (
                                        <tr key={idx}>
                                            <td className="employee-name">{record.employee?.firstName} {record.employee?.lastName}</td>
                                            <td className="punch-time">{record.punchIn ? new Date(record.punchIn).toLocaleTimeString() : '-'}</td>
                                            <td className="status-cell">
                                                <span className={`status-badge ${record.punchOut ? 'status-out' : 'status-active'}`}>
                                                    {record.punchOut ? 'Punched Out' : 'Active'}
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                    {recentAttendance.length === 0 && (
                                        <tr><td colSpan="3" className="empty-message">No entries for today yet</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section className="card">
                        <div className="card-title">System Status</div>
                        <div className="system-status-content">
                            <CheckCircle size={48} color="#48bb78" />
                            <h3>All Systems Operational</h3>
                            <p>Backend connected to MySQL database</p>
                        </div>
                    </section>
                </div>

                <div className="dashboard-right">
                    <PunchCard />

                    <section className="card">
                        <div className="card-title">Quick Actions</div>
                        <div className="quick-actions-container">
                            <button className="punch-btn btn-primary" onClick={() => navigate('/employees')}>Add New Employee</button>
                            <button className="punch-btn btn-secondary" onClick={() => navigate('/projects')}>Create Project</button>
                        </div>
                    </section>

                    <section className="card">
                        <div className="card-title">Dashboard Entries</div>

                        <form onSubmit={handleAddEntry} className="dashboard-entry-form">
                            <label className="form-label">Title</label>
                            <input
                                value={entryTitle}
                                onChange={(e) => setEntryTitle(e.target.value)}
                                placeholder="e.g., Team meeting at 3 PM"
                                className="form-control"
                            />

                            <label className="form-label">Message (optional)</label>
                            <textarea
                                value={entryMessage}
                                onChange={(e) => setEntryMessage(e.target.value)}
                                placeholder="Add a short note..."
                                rows={3}
                                className="form-textarea"
                            />

                            <button
                                type="submit"
                                className="punch-btn btn-primary btn-submit"
                                disabled={entrySaving}
                            >
                                <Plus size={18} />
                                {entrySaving ? 'Saving...' : 'Add Entry'}
                            </button>
                        </form>

                        <div className="entries-list">
                            {entriesLoading && (
                                <div className="loading-message">Loading entries...</div>
                            )}

                            {!entriesLoading && dashboardEntries.length === 0 && (
                                <div className="empty-message">No entries yet</div>
                            )}

                            {!entriesLoading && dashboardEntries.map((entry) => (
                                <div key={entry.id} className="entry-item">
                                    <div className="entry-content">
                                        <div className="entry-title">
                                            {entry.title}
                                        </div>
                                        {entry.message && (
                                            <div className="entry-message">
                                                {entry.message}
                                            </div>
                                        )}
                                        {entry.createdAt && (
                                            <div className="entry-time">
                                                {String(entry.createdAt).replace('T', ' ')}
                                            </div>
                                        )}
                                    </div>

                                    <button
                                        type="button"
                                        onClick={() => handleDeleteEntry(entry.id)}
                                        title="Delete entry"
                                        className="delete-btn"
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </section>

                    <section className="card">
                        <div className="card-title">Summary Statistics</div>
                        <div className="summary-stats">
                            <div className="summary-item">
                                <span className="stat-label">Database Link</span>
                                <span className="stat-status active">Active</span>
                            </div>
                            <div className="summary-item">
                                <span className="stat-label">API Server</span>
                                <span className="stat-status active">Online</span>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </main>
    );
};

export default Dashboard;
