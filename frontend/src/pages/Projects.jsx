import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Briefcase, Search, Plus, X, Trash2 } from 'lucide-react';

const Projects = () => {
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterStatus, setFilterStatus] = useState('ALL');

    // Form States
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        projectName: '',
        clientName: '',
        startDate: new Date().toISOString().split('T')[0],
        endDate: '',
        status: 'ACTIVE'
    });

    useEffect(() => {
        fetchProjects();
    }, []);

    const fetchProjects = async () => {
        try {
            setLoading(true);
            const data = await api.project.getAll();
            setProjects(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            console.error('Error fetching projects:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.project.create(formData);
            setShowModal(false);
            setFormData({
                projectName: '',
                clientName: '',
                startDate: new Date().toISOString().split('T')[0],
                endDate: '',
                status: 'ACTIVE'
            });
            fetchProjects();
            alert('Project Added Successfully!');
        } catch (err) {
            alert('Error adding project: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this project?')) {
            try {
                await api.project.delete(id);
                fetchProjects();
                alert('Project Deleted!');
            } catch (err) {
                alert('Error deleting project: ' + err.message);
            }
        }
    };

    const filteredProjects = projects.filter(project => {
        const matchesSearch =
            (project.projectName || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
            (project.clientName || '').toLowerCase().includes(searchTerm.toLowerCase());

        const matchesStatus = filterStatus === 'ALL' || project.status === filterStatus;

        return matchesSearch && matchesStatus;
    });

    const getStatusColor = (status) => {
        switch (status) {
            case 'ACTIVE': return '#48bb78';
            case 'COMPLETED': return '#4299e1';
            case 'ON_HOLD': return '#ed8936';
            default: return '#718096';
        }
    };

    if (loading) {
        return (
            <div className="content">
                <div style={{ textAlign: 'center', padding: '50px' }}>
                    <div style={{ fontSize: '18px', color: '#718096' }}>Loading projects...</div>
                </div>
            </div>
        );
    }

    return (
        <main className="content">
            <div className="content-header">
                <h1><Briefcase size={28} style={{ marginRight: '10px', verticalAlign: 'middle' }} />Projects</h1>
                <p className="breadcrumb">Dashboard / <span>Projects</span></p>
            </div>

            <div className="card" style={{ marginBottom: '20px' }}>
                <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
                    <span>All Projects</span>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <select
                            value={filterStatus}
                            onChange={(e) => setFilterStatus(e.target.value)}
                            style={{
                                padding: '8px 12px',
                                borderRadius: '6px',
                                border: '2px solid #e2e8f0',
                                fontSize: '14px'
                            }}
                        >
                            <option value="ALL">All Status</option>
                            <option value="ACTIVE">Active</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="ON_HOLD">On Hold</option>
                        </select>
                        <div className="search-bar" style={{ margin: 0 }}>
                            <Search size={16} color="#718096" />
                            <input
                                type="text"
                                placeholder="Search projects..."
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
                            <Plus size={18} /> Add Project
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
                            <h2 style={{ marginBottom: '20px' }}>Add New Project</h2>
                            <form onSubmit={handleSubmit}>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Project Name</label>
                                    <input type="text" name="projectName" required value={formData.projectName} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                </div>
                                <div style={{ marginBottom: '15px' }}>
                                    <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Client Name</label>
                                    <input type="text" name="clientName" required value={formData.clientName} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                </div>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Start Date</label>
                                        <input type="date" name="startDate" required value={formData.startDate} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>End Date</label>
                                        <input type="date" name="endDate" value={formData.endDate} onChange={handleInputChange} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #E2E8F0' }} />
                                    </div>
                                </div>
                                <button type="submit" className="punch-btn" style={{ marginBottom: 0 }}>Create Project</button>
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
                                    <th>Project Name</th>
                                    <th>Client</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredProjects.map((project) => (
                                    <tr key={project.id}>
                                        <td>{project.id}</td>
                                        <td style={{ fontWeight: '600' }}>{project.projectName || 'N/A'}</td>
                                        <td>{project.clientName || 'N/A'}</td>
                                        <td>{project.startDate || 'N/A'}</td>
                                        <td>{project.endDate || 'N/A'}</td>
                                        <td>
                                            <span style={{
                                                padding: '4px 12px',
                                                borderRadius: '12px',
                                                fontSize: '12px',
                                                fontWeight: '600',
                                                background: getStatusColor(project.status) + '20',
                                                color: getStatusColor(project.status)
                                            }}>
                                                {project.status || 'N/A'}
                                            </span>
                                        </td>
                                        <td>
                                            <button onClick={() => handleDelete(project.id)} style={{ border: 'none', background: 'none', color: '#FC8181', cursor: 'pointer' }}>
                                                <Trash2 size={18} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                {filteredProjects.length === 0 && (
                                    <tr>
                                        <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#718096' }}>
                                            {searchTerm || filterStatus !== 'ALL' ? 'No projects found matching your filters' : 'No projects found'}
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}

                <div style={{ padding: '15px', borderTop: '1px solid #e2e8f0', color: '#718096', fontSize: '14px' }}>
                    Total Projects: <strong>{filteredProjects.length}</strong>
                </div>
            </div>
        </main>
    );
};

export default Projects;
