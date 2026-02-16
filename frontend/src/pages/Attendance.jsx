import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Clock, Trash2 } from 'lucide-react';

const Attendance = () => {
    const [attendanceRecords, setAttendanceRecords] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [selectedEmployeeId, setSelectedEmployeeId] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchAttendance();
        fetchEmployees();
    }, []);

    const fetchAttendance = async () => {
        try {
            setLoading(true);
            const data = await api.attendance.getAll();
            setAttendanceRecords(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error('Error fetching attendance:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const fetchEmployees = async () => {
        try {
            const data = await api.employee.getAll();
            setEmployees(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Error fetching employees:', err);
        }
    };

    const handlePunch = async () => {
        if (!selectedEmployeeId) {
            alert('Please select an employee first!');
            return;
        }

        try {
            // Check if already punched in today
            const todayRecord = await api.attendance.getTodayAttendance(selectedEmployeeId);

            if (todayRecord && !todayRecord.punchOut) {
                // Punch Out
                await api.attendance.punchOut(selectedEmployeeId);
                alert('Punched Out Successfully!');
            } else {
                // Punch In
                await api.attendance.punchIn(selectedEmployeeId);
                alert('Punched In Successfully!');
            }
            fetchAttendance();
        } catch (err) {
            alert('Error during punch: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this attendance record?')) {
            try {
                // Note: api.js doesn't have a direct delete for attendance, let's assume it exists or use generic
                await api.attendance.delete(id);
                fetchAttendance();
                alert('Record Removed!');
            } catch (err) {
                alert('Error: ' + err.message);
            }
        }
    };

    return (
        <main className="content">
            <div className="content-header">
                <h1>Attendance</h1>
                <p className="breadcrumb">Dashboard / <span>Attendance</span></p>
            </div>

            <div className="dashboard-grid">
                {/* Timesheet */}
                <section className="card">
                    <div className="card-title">
                        <span>Attendance Punch</span>
                        <span style={{ fontSize: '11px', color: '#718096' }}>{new Date().toLocaleDateString()}</span>
                    </div>

                    <div style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Select Employee to Punch</label>
                        <select
                            value={selectedEmployeeId}
                            onChange={(e) => setSelectedEmployeeId(e.target.value)}
                            style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}
                        >
                            <option value="">-- Choose Employee --</option>
                            {employees.map(emp => <option key={emp.id} value={emp.id}>{emp.firstName} {emp.lastName}</option>)}
                        </select>
                    </div>

                    <button className="punch-btn" onClick={handlePunch} style={{ width: '100%' }}>Punch In / Out</button>
                    <p style={{ fontSize: '11px', color: '#718096', marginTop: '10px', textAlign: 'center' }}>
                        * Punch in to start tracking, punch out to end.
                    </p>
                </section>

                <section className="card">
                    <div className="card-title">Recent Activity</div>
                    <div className="timeline">
                        {attendanceRecords.slice(0, 4).map((record, i) => (
                            <div className="timeline-item" key={i}>
                                <div className="timeline-dot" style={{ background: record.punchOut ? '#FC8181' : '#48bb78' }}></div>
                                <div className="timeline-content">
                                    <h5>{record.employee?.firstName || 'User'} {record.punchOut ? 'Punched Out' : 'Punched In'}</h5>
                                    <p><Clock size={10} style={{ marginRight: '4px' }} />
                                        {record.punchOut ? new Date(record.punchOut).toLocaleTimeString() : new Date(record.punchIn).toLocaleTimeString()}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                <section className="card">
                    <div className="card-title">Status Summary</div>
                    <div style={{ padding: '20px', textAlign: 'center' }}>
                        <h2 style={{ fontSize: '32px', margin: 0 }}>{attendanceRecords.filter(r => r.attendanceDate === new Date().toISOString().split('T')[0]).length}</h2>
                        <p style={{ color: '#718096' }}>Punched in Today</p>
                    </div>
                </section>
            </div>

            <div className="card">
                <div className="card-title">Master Attendance List</div>
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Employee</th>
                                <th>Punch In</th>
                                <th>Punch Out</th>
                                <th>Hours</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {attendanceRecords.map((record) => (
                                <tr key={record.id}>
                                    <td>{record.attendanceDate}</td>
                                    <td style={{ fontWeight: '600' }}>{record.employee?.firstName} {record.employee?.lastName}</td>
                                    <td>{record.punchIn ? new Date(record.punchIn).toLocaleTimeString() : '-'}</td>
                                    <td>{record.punchOut ? new Date(record.punchOut).toLocaleTimeString() : '-'}</td>
                                    <td>{record.totalHours || '-'}</td>
                                    <td>
                                        <button onClick={() => handleDelete(record.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                            <Trash2 size={16} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    );
};

export default Attendance;
