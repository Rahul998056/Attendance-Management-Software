import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Clock, Fingerprint, LogIn, LogOut, Coffee } from 'lucide-react';

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

    return (
        <div className="card" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Fingerprint size={20} color="#3476E1" />
                <span>Attendance Punch</span>
            </div>

            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '10px 0' }}>
                <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                    <div style={{ fontSize: '42px', fontWeight: 'bold', letterSpacing: '2px', color: '#2d3748' }}>
                        {stats.time}
                    </div>
                    <div style={{
                        display: 'inline-block',
                        padding: '4px 12px',
                        borderRadius: '20px',
                        fontSize: '12px',
                        fontWeight: 'bold',
                        background: currentAttendance ? (currentAttendance.punchOut ? '#FC818120' : '#48bb7820') : '#E2E8F0',
                        color: currentAttendance ? (currentAttendance.punchOut ? '#FC8181' : '#48bb78') : '#718096'
                    }}>
                        {stats.status}
                    </div>
                </div>

                <div style={{ marginBottom: '20px' }}>
                    <label style={{ display: 'block', fontSize: '12px', color: '#718096', marginBottom: '6px' }}>Verify Employee</label>
                    <select
                        value={selectedEmployeeId}
                        onChange={(e) => setSelectedEmployeeId(e.target.value)}
                        className="form-control"
                        style={{ width: '100%', padding: '12px', borderRadius: '10px', border: '2px solid #F7FAFC', background: '#FAFBFF' }}
                    >
                        <option value="">-- Choose Name --</option>
                        {employees.map(emp => (
                            <option key={emp.id} value={emp.id}>{emp.firstName} {emp.lastName}</option>
                        ))}
                    </select>
                </div>

                <button
                    onClick={handlePunch}
                    disabled={loading || !selectedEmployeeId || (currentAttendance && currentAttendance.punchOut)}
                    style={{
                        width: '100%',
                        padding: '16px',
                        borderRadius: '12px',
                        border: 'none',
                        background: 'linear-gradient(135deg, #3476E1 0%, #2D3748 100%)',
                        color: 'white',
                        fontWeight: 'bold',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        gap: '10px',
                        cursor: (loading || !selectedEmployeeId || (currentAttendance && currentAttendance.punchOut)) ? 'not-allowed' : 'pointer',
                        opacity: (loading || !selectedEmployeeId || (currentAttendance && currentAttendance.punchOut)) ? 0.7 : 1,
                        transition: 'all 0.3s ease',
                        boxShadow: '0 4px 15px rgba(52, 118, 225, 0.3)'
                    }}
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

                {(currentAttendance && currentAttendance.punchOut) && (
                    <p style={{ textAlign: 'center', fontSize: '11px', color: '#718096', marginTop: '12px' }}>
                        Shift completed for today.
                    </p>
                )}
            </div>
        </div>
    );
};

export default PunchCard;
