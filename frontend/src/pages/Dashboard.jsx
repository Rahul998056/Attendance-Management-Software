import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import {
    LayoutDashboard,
    Users,
    Briefcase,
    FileText,
    Clock,
    DollarSign,
    CheckCircle,
    AlertCircle
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
                    api.payroll.getTotal(currentMonth, currentYear)
                ]);

                setStats({
                    totalEmployees: employeesRes.status === 'fulfilled' ? (Array.isArray(employeesRes.value) ? employeesRes.value.length : 0) : 'Error',
                    activeProjects: projectsRes.status === 'fulfilled' ? (Array.isArray(projectsRes.value) ? projectsRes.value.length : 0) : 'Error',
                    pendingLeaves: leavesRes.status === 'fulfilled' ? (Array.isArray(leavesRes.value) ? leavesRes.value.length : 0) : 'Error',
                    todayAttendance: attendanceRes.status === 'fulfilled' ? (Array.isArray(attendanceRes.value) ? attendanceRes.value.length : 0) : 'Error',
                    totalPayroll: payrollTotalRes.status === 'fulfilled' ? (payrollTotalRes.value.totalPayroll || 0) : 0
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

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginBottom: '30px' }}>
                {cards.map((card, i) => (
                    <Link to={card.link} key={i} className="card" style={{ cursor: 'pointer', textDecoration: 'none', color: 'inherit' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div>
                                <p style={{ fontSize: '13px', color: '#718096', marginBottom: '8px', fontWeight: '500' }}>{card.title}</p>
                                <h2 style={{ fontSize: '28px', color: '#2d3748', margin: 0 }}>{loading ? '...' : card.value}</h2>
                            </div>
                            <div style={{
                                width: '50px',
                                height: '50px',
                                borderRadius: '12px',
                                background: card.bg,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center'
                            }}>
                                {React.cloneElement(card.icon, { size: 24 })}
                            </div>
                        </div>
                    </Link>
                ))}
            </div>

            <div className="dashboard-grid" style={{ gridTemplateColumns: '2fr 1fr' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    {/* Recent Activity Table */}
                    <section className="card">
                        <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span>Recent Attendance Activity</span>
                            <Link to="/attendance" style={{ fontSize: '12px', color: '#3476E1', textDecoration: 'none' }}>View All</Link>
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
                                            <td style={{ fontWeight: '600' }}>{record.employee?.firstName} {record.employee?.lastName}</td>
                                            <td>{record.punchIn ? new Date(record.punchIn).toLocaleTimeString() : '-'}</td>
                                            <td>
                                                <span style={{
                                                    padding: '3px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold',
                                                    background: record.punchOut ? '#FC818120' : '#48bb7820',
                                                    color: record.punchOut ? '#FC8181' : '#48bb78'
                                                }}>
                                                    {record.punchOut ? 'Punched Out' : 'Active'}
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                    {recentAttendance.length === 0 && (
                                        <tr><td colSpan="3" style={{ textAlign: 'center', padding: '20px', color: '#718096' }}>No entries for today yet</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section className="card">
                        <div className="card-title">System Status</div>
                        <div style={{ padding: '20px', textAlign: 'center' }}>
                            <div style={{ marginBottom: '20px' }}>
                                <CheckCircle size={48} color="#48bb78" style={{ marginBottom: '10px' }} />
                                <h3>All Systems Operational</h3>
                                <p style={{ color: '#718096' }}>Backend connected to MySQL database</p>
                            </div>
                        </div>
                    </section>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <PunchCard />

                    <section className="card">
                        <div className="card-title">Quick Actions</div>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                            <button className="punch-btn" style={{ width: '100%', marginBottom: 0 }} onClick={() => navigate('/employees')}>Add New Employee</button>
                            <button className="punch-btn" style={{ width: '100%', background: '#4299e1' }} onClick={() => navigate('/projects')}>Create Project</button>
                        </div>
                    </section>

                    <section className="card">
                        <div className="card-title">Summary Statistics</div>
                        <div style={{ padding: '5px' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                                <span style={{ color: '#718096' }}>Database Link</span>
                                <span style={{ color: '#48bb78', fontWeight: 'bold' }}>Active</span>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                                <span style={{ color: '#718096' }}>API Server</span>
                                <span style={{ color: '#48bb78', fontWeight: 'bold' }}>Online</span>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </main>
    );
};

export default Dashboard;
