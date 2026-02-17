import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Clock, Fingerprint, LogIn, LogOut, Coffee } from 'lucide-react';
import '../styles/PunchCard.css';

const PunchCard = () => {
    const [employees, setEmployees] = useState([]);
    const [selectedEmployeeId, setSelectedEmployeeId] = useState('');
    const [currentAttendance, setCurrentAttendance] = useState(null);
    const [loading, setLoading] = useState(false);
    const [stats, setStats] = useState({ time: '--:--', status: 'Not Punched' });

    useEffect(() => {
        fetchEmployees();
    }, []);

    useEffect(() => {
        if (selectedEmployeeId) {
            checkStatus();
        } else {
            setCurrentAttendance(null);
            setStats({ time: '--:--', status: 'Select Employee' });
        }
    }, [selectedEmployeeId]);

    const fetchEmployees = async () => {
        try {
            const data = await api.employee.getAll();
            setEmployees(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Error fetching employees:', err);
        }
    };

    const checkStatus = async () => {
        try {
            const data = await api.attendance.getTodayAttendance(selectedEmployeeId);
            setCurrentAttendance(data);
            if (data) {
                if (data.punchOut) {
                    setStats({ time: new Date(data.punchOut).toLocaleTimeString(), status: 'Punched Out' });
                } else {
                    setStats({ time: new Date(data.punchIn).toLocaleTimeString(), status: 'Active' });
                }
            } else {
                setStats({ time: '--:--', status: 'Ready to Punch' });
            }
        } catch (err) {
            console.error('Error checking status:', err);
        }
    };

    const handlePunch = async () => {
        if (!selectedEmployeeId) return;
        setLoading(true);
        try {
            if (currentAttendance && !currentAttendance.punchOut) {
                await api.attendance.punchOut(selectedEmployeeId);
                alert('Goodbye! Punched out successfully.');
            } else {
                await api.attendance.punchIn(selectedEmployeeId);
                alert('Welcome! Punched in successfully.');
            }
            await checkStatus();
        } catch (err) {
            alert('Punch failed: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    const isShiftCompleted = currentAttendance && currentAttendance.punchOut;
    const isPunchDisabled = loading || !selectedEmployeeId || isShiftCompleted;

    return (
        <div className="card punch-card">
            <div className="card-title punch-card-title">
                <Fingerprint size={20} color="#3476E1" />
                <span>Attendance Punch</span>
            </div>

            <div className="punch-card-content">
                <div className="punch-card-time">
                    <div className="punch-time">{stats.time}</div>
                    <div className={`punch-status ${isShiftCompleted ? 'status-out' : currentAttendance ? 'status-active' : 'status-idle'}`}>
                        {stats.status}
                    </div>
                </div>

                <div className="punch-card-employee">
                    <label className="form-label">Verify Employee</label>
                    <select
                        value={selectedEmployeeId}
                        onChange={(e) => setSelectedEmployeeId(e.target.value)}
                        className="form-control"
                    >
                        <option value="">-- Choose Name --</option>
                        {employees.map(emp => (
                            <option key={emp.id} value={emp.id}>{emp.firstName} {emp.lastName}</option>
                        ))}
                    </select>
                </div>

                <button
                    onClick={handlePunch}
                    disabled={isPunchDisabled}
                    className={`punch-action-btn ${isShiftCompleted ? 'btn-disabled' : currentAttendance && !currentAttendance.punchOut ? 'btn-out' : 'btn-in'}`}
                >
                    {currentAttendance && !currentAttendance.punchOut ? (
                        <>
                            <LogOut size={20} />
                            Punch Out Now
                        </>
                    ) : (
                        <>
                            <LogIn size={20} />
                            Punch In Now
                        </>
                    )}
                </button>

                {isShiftCompleted && (
                    <p className="shift-completed-msg">
                        Shift completed for today.
                    </p>
                )}
            </div>
        </div>
    );
};

export default PunchCard;
