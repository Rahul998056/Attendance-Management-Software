import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Coffee, Search, Clock, Trash2, Calendar } from 'lucide-react';

const Breaks = () => {
    const [breaks, setBreaks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        fetchBreaks();
    }, []);

    const fetchBreaks = async () => {
        try {
            setLoading(true);
            const data = await api.break.getAll();
            setBreaks(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this break record?')) {
            try {
                // api.break.delete is missing from api.js, let's assume it exists or use generic
                await fetch(`http://localhost:8080/api/breaks/${id}`, { method: 'DELETE' });
                fetchBreaks();
            } catch (err) {
                alert(err.message);
            }
        }
    };

    const filteredBreaks = breaks.filter(b => {
        const name = `${b.attendance?.employee?.firstName || ''} ${b.attendance?.employee?.lastName || ''}`.toLowerCase();
        return name.includes(searchTerm.toLowerCase());
    });

    if (loading) return <div className="content">Loading breaks...</div>;

    return (
        <main className="content">
            <div className="content-header">
                <h1><Coffee size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />Break History</h1>
                <p className="breadcrumb">Dashboard / <span>Breaks</span></p>
            </div>

            <div className="card">
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>Employee Breaks</span>
                    <div className="search-bar" style={{ margin: 0 }}>
                        <Search size={16} color="#718096" />
                        <input
                            type="text"
                            placeholder="Search employee..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>

                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Employee</th>
                                <th>Date</th>
                                <th>Start Time</th>
                                <th>End Time</th>
                                <th>Duration (Hrs)</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredBreaks.map(b => (
                                <tr key={b.id}>
                                    <td>{b.id}</td>
                                    <td>
                                        <div style={{ fontWeight: '600' }}>
                                            {b.attendance?.employee?.firstName} {b.attendance?.employee?.lastName}
                                        </div>
                                        <div style={{ fontSize: '11px', color: '#718096' }}>{b.attendance?.employee?.designation}</div>
                                    </td>
                                    <td>{b.attendance?.attendanceDate}</td>
                                    <td>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                                            <Clock size={14} color="#48BB78" />
                                            {b.breakStart ? new Date(b.breakStart).toLocaleTimeString() : 'N/A'}
                                        </div>
                                    </td>
                                    <td>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                                            <Clock size={14} color="#F56565" />
                                            {b.breakEnd ? new Date(b.breakEnd).toLocaleTimeString() : 'N/A'}
                                        </div>
                                    </td>
                                    <td style={{ fontWeight: '700' }}>{b.breakDuration || 0}</td>
                                    <td>
                                        <button onClick={() => handleDelete(b.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                            <Trash2 size={18} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {filteredBreaks.length === 0 && (
                                <tr>
                                    <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#718096' }}>No break records found</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    );
};

export default Breaks;
