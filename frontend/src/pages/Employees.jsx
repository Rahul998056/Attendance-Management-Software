import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Users, Plus, Search, X, Trash2 } from 'lucide-react';

const Employees = () => {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    // Form States
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        designation: '',
        department: '',
        phone: '',
        salary: '',
        joiningDate: new Date().toISOString().split('T')[0]
    });

    useEffect(() => {
        fetchEmployees(false);

        // Keep UI in sync even if data changes outside the UI (e.g., DB edits)
        const intervalId = setInterval(() => fetchEmployees(true), 15000);
        return () => clearInterval(intervalId);
    }, []);

    const fetchEmployees = async (silent = false) => {
        try {
            if (!silent) setLoading(true);
            const data = await api.employee.getAll();
            setEmployees(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error('Error fetching employees:', err);
            if (!silent) setError(err.message);
        } finally {
            if (!silent) setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Send data to backend
            const created = await api.employee.create(formData);

            // Close modal and clear form
            setShowModal(false);
            setFormData({
                firstName: '',
                lastName: '',
                designation: '',
                department: '',
                phone: '',
                salary: '',
                joiningDate: new Date().toISOString().split('T')[0]
            });

            // Update UI immediately, then sync from backend
            if (created && created.id) {
                setEmployees((prev) => [created, ...prev]);
            } else {
                fetchEmployees(true);
            }
            alert('Employee Added Successfully to Database!');
        } catch (err) {
            alert('Error adding employee: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this employee? This will reflect in the Database and UI immediately.')) {
            try {
                await api.employee.delete(id);
                setEmployees((prev) => prev.filter((emp) => emp.id !== id));
                alert('Employee Deleted Successfully!');
            } catch (err) {
                alert('Error deleting employee: ' + err.message);
            }
        }
    };

    const filteredEmployees = employees.filter(emp => {
        const fullName = `${emp.firstName || ''} ${emp.lastName || ''}`.toLowerCase();
        const search = searchTerm.toLowerCase();
        return fullName.includes(search) ||
            (emp.department || '').toLowerCase().includes(search) ||
            (emp.designation || '').toLowerCase().includes(search);
    });

    if (loading) {
        return (
            <div className="content">
                <div style={{ textAlign: 'center', padding: '50px' }}>
                    <div style={{ fontSize: '18px', color: '#718096' }}>Loading employees...</div>
                </div>
            </div>
        );
    }

    return (
        <main className="content">
            <div className="content-header">
                <h1><Users size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />Employees</h1>
                <p className="breadcrumb">Dashboard / <span>Employees</span></p>
            </div>

            <div className="card" style={{ marginBottom: '20px' }}>
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>Employee Directory</span>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <div className="search-bar" style={{ margin: 0 }}>
                            <Search size={16} color="#718096" />
                            <input
                                type="text"
                                placeholder="Search employees..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                style={{ width: '200px' }}
                            />
                        </div>
                        <button
                            onClick={() => setShowModal(true)}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '8px',
                                background: '#3476E1',
                                color: 'white',
                                padding: '10px 18px',
                                borderRadius: '8px',
                                border: 'none',
                                fontWeight: '600',
                                cursor: 'pointer',
                                transition: '0.2s'
                            }}
                        >
                            <Plus size={18} /> Add Employee
                        </button>
                    </div>
                </div>

                {/* MODAL OVERLAY */}
                {showModal && (
                    <div style={{
                        position: 'fixed',
                        top: 0, left: 0, right: 0, bottom: 0,
                        backgroundColor: 'rgba(0,0,0,0.5)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000,
                        padding: '20px'
                    }}>
                        <div className="card" style={{ width: '100%', maxWidth: '500px', position: 'relative' }}>
                            <button
                                onClick={() => setShowModal(false)}
                                style={{ position: 'absolute', right: '20px', top: '20px', border: 'none', background: 'none', cursor: 'pointer', color: '#718096' }}
                            >
                                <X size={20} />
                            </button>

                            <h2 style={{ marginBottom: '20px' }}>Add New Employee</h2>

                            <form onSubmit={handleSubmit}>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px', fontWeight: '500' }}>First Name</label>
                                        <input type="text" name="firstName" required value={formData.firstName} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px', fontWeight: '500' }}>Last Name</label>
                                        <input type="text" name="lastName" required value={formData.lastName} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                </div>

                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px', fontWeight: '500' }}>Designation</label>
                                    <input type="text" name="designation" required value={formData.designation} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                </div>

                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px', fontWeight: '500' }}>Department</label>
                                    <select name="department" value={formData.department} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}>
                                        <option value="">Select Department</option>
                                        <option value="IT">IT</option>
                                        <option value="HR">HR</option>
                                        <option value="Finance">Finance</option>
                                        <option value="Operations">Operations</option>
                                        <option value="Sales">Sales</option>
                                    </select>
                                </div>

                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px', fontWeight: '500' }}>Phone</label>
                                        <input type="text" name="phone" value={formData.phone} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px', fontWeight: '500' }}>Salary</label>
                                        <input type="number" name="salary" value={formData.salary} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                </div>

                                <button type="submit" className="punch-btn" style={{ marginBottom: 0 }}>Save Employee to Database</button>
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
                                    <th>Name</th>
                                    <th>Designation</th>
                                    <th>Department</th>
                                    <th>Phone</th>
                                    <th>Joining Date</th>
                                    <th>Salary</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredEmployees.map((emp) => (
                                    <tr key={emp.id}>
                                        <td>{emp.id}</td>
                                        <td style={{ fontWeight: '600' }}>
                                            {`${emp.firstName || ''} ${emp.lastName || ''}`.trim() || 'N/A'}
                                        </td>
                                        <td>{emp.designation || 'N/A'}</td>
                                        <td>{emp.department || 'N/A'}</td>
                                        <td>{emp.phone || 'N/A'}</td>
                                        <td>{emp.joiningDate || 'N/A'}</td>
                                        <td>${emp.salary ? emp.salary.toLocaleString() : 'N/A'}</td>
                                        <td>
                                            <button
                                                onClick={() => handleDelete(emp.id)}
                                                style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer', padding: '5px' }}
                                                title="Delete Employee"
                                            >
                                                <Trash2 size={18} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {filteredEmployees.length === 0 && (
                                    <tr>
                                        <td colSpan="8" style={{ textAlign: 'center', padding: '30px', color: '#718096' }}>
                                            {searchTerm ? 'No employees found matching your search' : 'No employees found'}
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}

                <div style={{ padding: '15px', borderTop: '1px solid #e2e8f0', color: '#718096', fontSize: '14px' }}>
                    Total Employees: <strong>{filteredEmployees.length}</strong>
                </div>
            </div>
        </main>
    );
};

export default Employees;
