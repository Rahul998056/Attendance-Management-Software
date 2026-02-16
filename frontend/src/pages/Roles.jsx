import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { ShieldCheck, Plus, X, Trash2 } from 'lucide-react';

const Roles = () => {
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [roleName, setRoleName] = useState('');

    useEffect(() => {
        fetchRoles();
    }, []);

    const fetchRoles = async () => {
        try {
            setLoading(true);
            const data = await api.role.getAll();
            setRoles(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.role.create({ roleName });
            setShowModal(false);
            setRoleName('');
            fetchRoles();
            alert('Role Created!');
        } catch (err) {
            alert('Error: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this role?')) {
            try {
                await api.role.delete(id);
                fetchRoles();
            } catch (err) {
                alert(err.message);
            }
        }
    };

    if (loading) return <div className="content">Loading...</div>;

    return (
        <main className="content">
            <div className="content-header">
                <h1><ShieldCheck size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />Roles</h1>
                <p className="breadcrumb">Dashboard / <span>Roles</span></p>
            </div>

            <div className="card" style={{ maxWidth: '600px' }}>
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>User Roles</span>
                    <button
                        onClick={() => setShowModal(true)}
                        style={{ background: '#3476E1', color: 'white', padding: '8px 15px', borderRadius: '6px', border: 'none', cursor: 'pointer' }}
                    >
                        <Plus size={18} /> Add Role
                    </button>
                </div>

                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Role Name</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roles.map(role => (
                                <tr key={role.id}>
                                    <td>{role.id}</td>
                                    <td><strong style={{ letterSpacing: '0.5px' }}>{role.roleName}</strong></td>
                                    <td>
                                        <button onClick={() => handleDelete(role.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                            <Trash2 size={18} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {showModal && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '20px' }}>
                    <div className="card" style={{ width: '100%', maxWidth: '350px', position: 'relative' }}>
                        <button onClick={() => setShowModal(false)} style={{ position: 'absolute', right: '15px', top: '15px', border: 'none', background: 'none' }}><X size={18} /></button>
                        <h2 style={{ marginBottom: '15px' }}>New Role</h2>
                        <form onSubmit={handleSubmit}>
                            <input
                                type="text"
                                value={roleName}
                                onChange={(e) => setRoleName(e.target.value.toUpperCase())}
                                placeholder="ROLE_NAME"
                                style={{ width: '100%', padding: '10px', marginBottom: '15px' }}
                                required
                            />
                            <button type="submit" className="punch-btn">Save Role</button>
                        </form>
                    </div>
                </div>
            )}
        </main>
    );
};

export default Roles;
