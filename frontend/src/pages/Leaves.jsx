import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { FileText, CheckCircle, XCircle, Clock, Plus, X, Trash2 } from 'lucide-react';

const Leaves = () => {
    const [leaves, setLeaves] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filterStatus, setFilterStatus] = useState('ALL');

    // Form States
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        employeeId: '',
        leaveType: 'CASUAL',
        startDate: new Date().toISOString().split('T')[0],
        endDate: '',
        reason: ''
    });

    useEffect(() => {
        fetchLeaves();
        fetchEmployees();
    }, []);

    const fetchLeaves = async () => {
        try {
            setLoading(true);
            const data = await api.leave.getAll();
            setLeaves(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error('Error fetching leaves:', err);
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

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Re-fetch to ensure the employee object is correct on backend (depending on how API handles it)
            // But usually we just send employee: {id: ...}
            const payload = {
                employee: { id: parseInt(formData.employeeId) },
                leaveType: formData.leaveType,
                startDate: formData.startDate,
                endDate: formData.endDate,
                reason: formData.reason,
                status: 'PENDING'
            };
            await api.leave.create(payload);
            setShowModal(false);
            setFormData({ employeeId: '', leaveType: 'CASUAL', startDate: new Date().toISOString().split('T')[0], endDate: '', reason: '' });
            fetchLeaves();
            alert('Leave Request Submitted!');
        } catch (err) {
            alert('Error applying leave: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this leave request?')) {
            try {
                await api.leave.delete(id);
                fetchLeaves();
                alert('Request Deleted!');
            } catch (err) {
                alert('Error deleting leave: ' + err.message);
            }
        }
    };

    const handleApprove = async (id) => {
        try {
            await api.leave.approve(id);
            fetchLeaves(); // Refresh
        } catch (err) {
            alert('Error approving leave: ' + err.message);
        }
    };

    const handleReject = async (id) => {
        try {
            await api.leave.reject(id);
            fetchLeaves(); // Refresh
        } catch (err) {
            alert('Error rejecting leave: ' + err.message);
        }
    };

    const filteredLeaves = leaves.filter(leave =>
        filterStatus === 'ALL' || leave.status === filterStatus
    );

    const getStatusColor = (status) => {
        switch (status) {
            case 'APPROVED': return '#48bb78';
            case 'REJECTED': return '#f56565';
            case 'PENDING': return '#ed8936';
            default: return '#718096';
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'APPROVED': return <CheckCircle size={16} />;
            case 'REJECTED': return <XCircle size={16} />;
            case 'PENDING': return <Clock size={16} />;
            default: return null;
        }
    };

    const statusCounts = {
        total: leaves.length,
        pending: leaves.filter(l => l.status === 'PENDING').length,
        approved: leaves.filter(l => l.status === 'APPROVED').length,
        rejected: leaves.filter(l => l.status === 'REJECTED').length,
    };

    if (loading) {
        return (
            <div className="content">
                <div style={{ textAlign: 'center', padding: '50px' }}>
                    <div style={{ fontSize: '18px', color: '#718096' }}>Loading leaves...</div>
                </div>
            </div>
        );
    }

    return (
        <main className="content">
            <div className="content-header">
                <h1><FileText size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />Leave Management</h1>
                <p className="breadcrumb">Dashboard / <span>Leaves</span></p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px', marginBottom: '20px' }}>
                <div className="card" style={{ padding: '15px' }}>
                    <p style={{ fontSize: '12px', color: '#718096', marginBottom: '5px' }}>Total Requests</p>
                    <h3 style={{ fontSize: '24px', margin: 0 }}>{statusCounts.total}</h3>
                </div>
                <div className="card" style={{ padding: '15px', borderLeft: '4px solid #ed8936' }}>
                    <p style={{ fontSize: '12px', color: '#718096', marginBottom: '5px' }}>Pending</p>
                    <h3 style={{ fontSize: '24px', margin: 0, color: '#ed8936' }}>{statusCounts.pending}</h3>
                </div>
                <div className="card" style={{ padding: '15px', borderLeft: '4px solid #48bb78' }}>
                    <p style={{ fontSize: '12px', color: '#718096', marginBottom: '5px' }}>Approved</p>
                    <h3 style={{ fontSize: '24px', margin: 0, color: '#48bb78' }}>{statusCounts.approved}</h3>
                </div>
                <div className="card" style={{ padding: '15px', borderLeft: '4px solid #f56565' }}>
                    <p style={{ fontSize: '12px', color: '#718096', marginBottom: '5px' }}>Rejected</p>
                    <h3 style={{ fontSize: '24px', margin: 0, color: '#f56565' }}>{statusCounts.rejected}</h3>
                </div>
            </div>

            <div className="card">
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>Leave Requests</span>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <select
                            value={filterStatus}
                            onChange={(e) => setFilterStatus(e.target.value)}
                            style={{ padding: '8px 12px', borderRadius: '6px', border: '2px solid #e2e8f0', fontSize: '14px' }}
                        >
                            <option value="ALL">All Status</option>
                            <option value="PENDING">Pending</option>
                            <option value="APPROVED">Approved</option>
                            <option value="REJECTED">Rejected</option>
                        </select>
                        <button
                            onClick={() => setShowModal(true)}
                            style={{
                                display: 'flex', alignItems: 'center', gap: '8px', background: '#3476E1', color: 'white',
                                padding: '10px 18px', borderRadius: '8px', border: 'none', fontWeight: '600', cursor: 'pointer'
                            }}
                        >
                            <Plus size={18} /> Apply Leave
                        </button>
                    </div>
                </div>

                {/* MODAL */}
                {showModal && (
                    <div style={{
                        position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                        backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '20px'
                    }}>
                        <div className="card" style={{ width: '100%', maxWidth: '500px', position: 'relative' }}>
                            <button onClick={() => setShowModal(false)} style={{ position: 'absolute', right: '20px', top: '20px', border: 'none', background: 'none', cursor: 'pointer', color: '#718096' }}>
                                <X size={20} />
                            </button>
                            <h2 style={{ marginBottom: '20px' }}>Apply For Leave</h2>
                            <form onSubmit={handleSubmit}>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Select Employee</label>
                                    <select name="employeeId" required value={formData.employeeId} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}>
                                        <option value="">-- Select --</option>
                                        {employees.map(emp => <option key={emp.id} value={emp.id}>{emp.firstName} {emp.lastName}</option>)}
                                    </select>
                                </div>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Leave Type</label>
                                    <select name="leaveType" required value={formData.leaveType} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}>
                                        <option value="CASUAL">Casual Leave</option>
                                        <option value="SICK">Sick Leave</option>
                                        <option value="PAID">Paid Leave</option>
                                    </select>
                                </div>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Start Date</label>
                                        <input type="date" name="startDate" required value={formData.startDate} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>End Date</label>
                                        <input type="date" name="endDate" required value={formData.endDate} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                </div>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Reason</label>
                                    <textarea name="reason" rows="3" value={formData.reason} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0', resize: 'none' }}></textarea>
                                </div>
                                <button type="submit" className="punch-btn" style={{ marginBottom: 0 }}>Apply Leave</button>
                            </form>
                        </div>
                    </div>
                )}

                {error ? (
                    <div style={{ textAlign: 'center', padding: '20px', color: '#FC8181' }}>
                        Error: {error}
                    </div>
                ) : (
                    <div className="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Employee</th>
                                    <th>Type</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredLeaves.map((leave) => (
                                    <tr key={leave.id}>
                                        <td>{leave.id}</td>
                                        <td style={{ fontWeight: '600' }}>
                                            {leave.employee
                                                ? `${leave.employee.firstName || ''} ${leave.employee.lastName || ''}`.trim()
                                                : 'N/A'}
                                        </td>
                                        <td>{leave.leaveType || 'N/A'}</td>
                                        <td>{leave.startDate || 'N/A'}</td>
                                        <td>{leave.endDate || 'N/A'}</td>
                                        <td style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                                            {leave.reason || 'N/A'}
                                        </td>
                                        <td>
                                            <span style={{
                                                padding: '4px 12px',
                                                borderRadius: '12px',
                                                fontSize: '12px',
                                                fontWeight: '600',
                                                background: getStatusColor(leave.status) + '20',
                                                color: getStatusColor(leave.status),
                                                display: 'inline-flex',
                                                alignItems: 'center',
                                                gap: '4px'
                                            }}>
                                                {getStatusIcon(leave.status)}
                                                {leave.status || 'N/A'}
                                            </span>
                                        </td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '5px' }}>
                                                {leave.status === 'PENDING' && (
                                                    <>
                                                        <button
                                                            onClick={() => handleApprove(leave.id)}
                                                            style={{ padding: '4px 8px', borderRadius: '6px', border: 'none', background: '#48bb78', color: 'white', fontSize: '11px', cursor: 'pointer' }}
                                                        >
                                                            Approve
                                                        </button>
                                                        <button
                                                            onClick={() => handleReject(leave.id)}
                                                            style={{ padding: '4px 8px', borderRadius: '6px', border: 'none', background: '#f56565', color: 'white', fontSize: '11px', cursor: 'pointer' }}
                                                        >
                                                            Reject
                                                        </button>
                                                    </>
                                                )}
                                                <button onClick={() => handleDelete(leave.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                                    <Trash2 size={16} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                {filteredLeaves.length === 0 && (
                                    <tr>
                                        <td colSpan="8" style={{ textAlign: 'center', padding: '30px', color: '#718096' }}>
                                            No leave requests found
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}

                <div style={{ padding: '15px', borderTop: '1px solid #e2e8f0', color: '#718096', fontSize: '14px' }}>
                    Total Requests: <strong>{filteredLeaves.length}</strong>
                </div>
            </div>
        </main>
    );
};

export default Leaves;
