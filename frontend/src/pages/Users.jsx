import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Users, Plus, Search, X, Trash2, Shield, Mail, User } from 'lucide-react';

const UsersManagement = () => {
    const [users, setUsers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    // Form States
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        roleId: '',
        isActive: true
    });

    useEffect(() => {
        fetchUsers();
        fetchRoles();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const data = await api.user.getAll();
            setUsers(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error('Error fetching users:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const fetchRoles = async () => {
        try {
            const data = await api.role.getAll();
            setRoles(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Error fetching roles:', err);
        }
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                ...formData,
                role: formData.roleId ? { id: formData.roleId } : null
            };
            delete payload.roleId;

            await api.user.create(payload);
            setShowModal(false);
            setFormData({
                username: '',
                email: '',
                password: '',
                roleId: '',
                isActive: true
            });
            fetchUsers();
            alert('User Created Successfully!');
        } catch (err) {
            alert('Error creating user: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await api.user.delete(id);
                fetchUsers();
                alert('User Deleted!');
            } catch (err) {
                alert('Error deleting user: ' + err.message);
            }
        }
    };

    const filteredUsers = users.filter(u => {
        const search = searchTerm.toLowerCase();
        return (u.username || '').toLowerCase().includes(search) ||
            (u.email || '').toLowerCase().includes(search) ||
            (u.role?.roleName || '').toLowerCase().includes(search);
    });

    if (loading) {
        return (
            <div className="content">
                <div style={{ textAlign: 'center', padding: '50px' }}>
                    <div style={{ fontSize: '18px', color: '#718096' }}>Loading users...</div>
                </div>
            </div>
        );
    }

    return (
        <main className="content">
            <div className="content-header">
                <h1><Shield size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />User Accounts</h1>
                <p className="breadcrumb">Dashboard / <span>Users</span></p>
            </div>

            <div className="card">
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>System Users</span>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <div className="search-bar" style={{ margin: 0 }}>
                            <Search size={16} color="#718096" />
                            <input
                                type="text"
                                placeholder="Search users..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                style={{ width: '200px' }}
                            />
                        </div>
                        <button
                            onClick={() => setShowModal(true)}
                            style={{
                                display: 'flex', alignItems: 'center', gap: '8px', background: '#3476E1', color: 'white',
                                padding: '10px 18px', borderRadius: '8px', border: 'none', fontWeight: '600', cursor: 'pointer'
                            }}
                        >
                            <Plus size={18} /> Add User
                        </button>
                    </div>
                </div>

                {showModal && (
                    <div style={{
                        position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                        backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '20px'
                    }}>
                        <div className="card" style={{ width: '100%', maxWidth: '450px', position: 'relative' }}>
                            <button onClick={() => setShowModal(false)} style={{ position: 'absolute', right: '20px', top: '20px', border: 'none', background: 'none', cursor: 'pointer', color: '#718096' }}>
                                <X size={20} />
                            </button>
                            <h2 style={{ marginBottom: '20px' }}>Create New Account</h2>
                            <form onSubmit={handleSubmit}>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Username</label>
                                    <div style={{ position: 'relative' }}>
                                        <User size={16} style={{ position: 'absolute', left: '10px', top: '12px', color: '#A0AEC0' }} />
                                        <input type="text" name="username" required value={formData.username} onChange={handleInputChange} style={{ width: '100%', padding: '10px 10px 10px 35px', borderRadius: '6px', border: '1px solid #E2E8F0' }} placeholder="e.g. john_doe" />
                                    </div>
                                </div>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Email Address</label>
                                    <div style={{ position: 'relative' }}>
                                        <Mail size={16} style={{ position: 'absolute', left: '10px', top: '12px', color: '#A0AEC0' }} />
                                        <input type="email" name="email" required value={formData.email} onChange={handleInputChange} style={{ width: '100%', padding: '10px 10px 10px 35px', borderRadius: '6px', border: '1px solid #E2E8F0' }} placeholder="email@example.com" />
                                    </div>
                                </div>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Password</label>
                                    <input type="password" name="password" required value={formData.password} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                </div>
                                <div style={{ marginBottom: '20px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Role</label>
                                    <select name="roleId" required value={formData.roleId} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }}>
                                        <option value="">Select Role</option>
                                        {roles.map(role => <option key={role.id} value={role.id}>{role.roleName}</option>)}
                                    </select>
                                </div>
                                <div style={{ marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                                    <input type="checkbox" name="isActive" checked={formData.isActive} onChange={handleInputChange} id="isActive" />
                                    <label htmlFor="isActive" style={{ fontSize: '14px' }}>Active Account</label>
                                </div>
                                <button type="submit" className="punch-btn" style={{ marginBottom: 0 }}>Create User</button>
                            </form>
                        </div>
                    </div>
                )}

                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Created At</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredUsers.map((u) => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td style={{ fontWeight: '600' }}>{u.username}</td>
                                    <td>{u.email}</td>
                                    <td>
                                        <span style={{ padding: '4px 8px', borderRadius: '4px', background: '#E2E8F0', fontSize: '11px', fontWeight: '700' }}>
                                            {u.role?.roleName || 'NONE'}
                                        </span>
                                    </td>
                                    <td>
                                        <span style={{ color: u.isActive ? '#48BB78' : '#F56565' }}>
                                            ● {u.isActive ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>{u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'N/A'}</td>
                                    <td>
                                        <button onClick={() => handleDelete(u.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                            <Trash2 size={18} />
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

export default UsersManagement;
