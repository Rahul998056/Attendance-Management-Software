import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Wallet, DollarSign, TrendingUp, Plus, X, Trash2 } from 'lucide-react';

const Payroll = () => {
    const [payrolls, setPayrolls] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedMonth, setSelectedMonth] = useState('February');
    const [selectedYear, setSelectedYear] = useState(2026);

    // Form States
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        employeeId: '',
        month: 'February',
        year: 2026,
    });

    useEffect(() => {
        fetchPayrolls();
        fetchEmployees();
    }, []);

    const fetchPayrolls = async () => {
        try {
            setLoading(true);
            const data = await api.payroll.getAll();
            setPayrolls(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error('Error fetching payroll:', err);
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
            // Using the generate endpoint which calculates based on attendance/salary
            await api.payroll.generate(formData.employeeId, formData.month, formData.year);
            setShowModal(false);
            fetchPayrolls();
            alert('Payroll Generated Successfully!');
        } catch (err) {
            alert('Error generating payroll: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this payroll record?')) {
            try {
                await api.payroll.delete(id);
                fetchPayrolls();
                alert('Record Deleted!');
            } catch (err) {
                alert('Error deleting payroll: ' + err.message);
            }
        }
    };

    const filteredPayrolls = payrolls.filter(p => {
        if (selectedMonth === 'ALL' && selectedYear === 'ALL') return true;
        if (selectedMonth === 'ALL') return p.year === parseInt(selectedYear);
        if (selectedYear === 'ALL') return p.month === selectedMonth;
        return p.month === selectedMonth && p.year === parseInt(selectedYear);
    });

    const totalPayroll = filteredPayrolls.reduce((sum, p) => sum + (p.netSalary || 0), 0);
    const totalOvertime = filteredPayrolls.reduce((sum, p) => sum + (p.overtimePay || 0), 0);
    const totalDeductions = filteredPayrolls.reduce((sum, p) => sum + (p.deductions || 0), 0);

    const months = ['ALL', 'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];

    if (loading) {
        return (
            <div className="content">
                <div style={{ textAlign: 'center', padding: '50px' }}>
                    <div style={{ fontSize: '18px', color: '#718096' }}>Loading payroll...</div>
                </div>
            </div>
        );
    }

    return (
        <main className="content">
            <div className="content-header">
                <h1><Wallet size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />Payroll</h1>
                <p className="breadcrumb">Dashboard / <span>Payroll</span></p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '20px' }}>
                <div className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <p style={{ fontSize: '12px', color: '#718096', marginBottom: '8px' }}>Total Payroll</p>
                            <h2 style={{ fontSize: '28px', color: '#2d3748', margin: 0 }}>
                                ${totalPayroll.toLocaleString()}
                            </h2>
                        </div>
                        <div style={{ width: '50px', height: '50px', borderRadius: '12px', background: '#4FD1C520', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                            <DollarSign size={24} color="#4FD1C5" />
                        </div>
                    </div>
                </div>

                <div className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <p style={{ fontSize: '12px', color: '#718096', marginBottom: '8px' }}>Total Overtime</p>
                            <h2 style={{ fontSize: '28px', color: '#2d3748', margin: 0 }}>
                                ${totalOvertime.toLocaleString()}
                            </h2>
                        </div>
                        <div style={{ width: '50px', height: '50px', borderRadius: '12px', background: '#F6AD5520', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                            <TrendingUp size={24} color="#F6AD55" />
                        </div>
                    </div>
                </div>

                <div className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <p style={{ fontSize: '12px', color: '#718096', marginBottom: '8px' }}>Total Deductions</p>
                            <h2 style={{ fontSize: '28px', color: '#2d3748', margin: 0 }}>
                                ${totalDeductions.toLocaleString()}
                            </h2>
                        </div>
                        <div style={{ width: '50px', height: '50px', borderRadius: '12px', background: '#FC818120', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                            <DollarSign size={24} color="#FC8181" />
                        </div>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
                    <span>Payroll Records</span>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <select value={selectedMonth} onChange={(e) => setSelectedMonth(e.target.value)} style={{ padding: '8px 12px', borderRadius: '6px', border: '2px solid #e2e8f0', fontSize: '14px' }}>
                            {months.map(m => <option key={m} value={m}>{m}</option>)}
                        </select>
                        <select value={selectedYear} onChange={(e) => setSelectedYear(e.target.value)} style={{ padding: '8px 12px', borderRadius: '6px', border: '2px solid #e2e8f0', fontSize: '14px' }}>
                            <option value="ALL">All Years</option>
                            <option value="2024">2024</option>
                            <option value="2025">2025</option>
                            <option value="2026">2026</option>
                        </select>
                        <button
                            onClick={() => setShowModal(true)}
                            style={{
                                display: 'flex', alignItems: 'center', gap: '8px', background: '#3476E1', color: 'white',
                                padding: '10px 18px', borderRadius: '8px', border: 'none', fontWeight: '600', cursor: 'pointer'
                            }}
                        >
                            <Plus size={18} /> Generate Payroll
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
                            <h2 style={{ marginBottom: '20px' }}>Generate Employee Payroll</h2>
                            <form onSubmit={handleSubmit}>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Select Employee</label>
                                    <select name="employeeId" required value={formData.employeeId} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}>
                                        <option value="">-- Select --</option>
                                        {employees.map(emp => <option key={emp.id} value={emp.id}>{emp.firstName} {emp.lastName}</option>)}
                                    </select>
                                </div>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Month</label>
                                        <select name="month" value={formData.month} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}>
                                            {months.filter(m => m !== 'ALL').map(m => <option key={m} value={m}>{m}</option>)}
                                        </select>
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Year</label>
                                        <input type="number" name="year" value={formData.year} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                </div>
                                <p style={{ fontSize: '11px', color: '#718096', marginBottom: '20px', lineHeight: '1.4' }}>
                                    * Pro-rated salary for new joiners.<br />
                                    * Includes 10% Tax & 5% PF deductions.<br />
                                    * Deducts for UNPAID leaves from records.
                                </p>
                                <button type="submit" className="punch-btn" style={{ marginBottom: 0 }}>Generate Record</button>
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
                                    <th>Month</th>
                                    <th>Year</th>
                                    <th>Base Salary</th>
                                    <th>Overtime</th>
                                    <th>Deductions</th>
                                    <th>Net Salary</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredPayrolls.map((payroll) => (
                                    <tr key={payroll.id}>
                                        <td>{payroll.id}</td>
                                        <td style={{ fontWeight: '600' }}>
                                            {payroll.employee
                                                ? `${payroll.employee.firstName || ''} ${payroll.employee.lastName || ''}`.trim()
                                                : 'N/A'}
                                        </td>
                                        <td>{payroll.month}</td>
                                        <td>{payroll.year}</td>
                                        <td>${payroll.baseSalary?.toLocaleString() || '0'}</td>
                                        <td style={{ color: '#48bb78' }}>${payroll.overtimePay?.toLocaleString() || '0'}</td>
                                        <td style={{ color: '#FC8181' }}>${payroll.deductions?.toLocaleString() || '0'}</td>
                                        <td style={{ fontWeight: '700', color: '#2d3748' }}>
                                            ${payroll.netSalary?.toLocaleString() || '0'}
                                        </td>
                                        <td>
                                            <button onClick={() => handleDelete(payroll.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                                <Trash2 size={18} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {filteredPayrolls.length === 0 && (
                                    <tr>
                                        <td colSpan="9" style={{ textAlign: 'center', padding: '30px', color: '#718096' }}>
                                            No payroll records found
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}

                <div style={{ padding: '15px', borderTop: '1px solid #e2e8f0', color: '#718096', fontSize: '14px' }}>
                    Total Records: <strong>{filteredPayrolls.length}</strong>
                </div>
            </div>
        </main>
    );
};

export default Payroll;
