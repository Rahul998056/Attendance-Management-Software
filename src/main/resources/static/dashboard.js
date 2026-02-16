const { useState, useEffect } = React;

// --- Components ---

function Sidebar({ mobileOpen, toggleMobile }) {
    return (
        <aside className={`sidebar ${mobileOpen ? 'active' : ''}`}>
            <div className="sidebar-header">
                <div className="logo">
                    <span className="material-icons-round">token</span>
                    <h2>Neo</h2>
                </div>
            </div>

            <div className="sidebar-menu">
                <div className="menu-category">Main</div>
                <a href="#" className="menu-item active">
                    <span className="material-icons-round">dashboard</span>
                    <span>Dashboard</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">apps</span>
                    <span>Apps</span>
                </a>

                <div className="menu-category">Employee</div>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">people</span>
                    <span>Employees</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">person</span>
                    <span>Clients</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">work</span>
                    <span>Projects</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">confirmation_number</span>
                    <span>Tickets</span>
                </a>

                <div className="menu-category">HR</div>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">account_balance_wallet</span>
                    <span>Accounts</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">monetization_on</span>
                    <span>Payroll</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">policy</span>
                    <span>Policies</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">assessment</span>
                    <span>Reports</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">trending_up</span>
                    <span>Performance</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">flag</span>
                    <span>Goals</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">model_training</span>
                    <span>Training</span>
                </a>
                <a href="#" className="menu-item">
                    <span className="material-icons-round">publish</span>
                    <span>Promotion</span>
                </a>
            </div>
        </aside>
    );
}

function Header({ toggleMenu }) {
    return (
        <header className="top-header">
            <button className="menu-toggle" onClick={toggleMenu}>
                <span className="material-icons-round">menu</span>
            </button>
            <div className="header-right">
                <div className="search-bar">
                    <input type="text" placeholder="search here..." />
                    <span className="material-icons-round">search</span>
                </div>
                <div className="header-actions">
                    <button className="icon-btn"><span className="material-icons-round">notifications</span></button>
                    <button className="icon-btn"><span className="material-icons-round">chat</span></button>
                    <div className="user-profile">
                        <img src="https://ui-avatars.com/api/?name=Admin&background=random" alt="Admin" />
                        <span>Admin</span>
                        <span className="material-icons-round">expand_more</span>
                    </div>
                </div>
            </div>
        </header>
    );
}

function TimesheetCard() {
    const [punchedIn, setPunchedIn] = useState(true);

    return (
        <div className="card timesheet-card">
            <div className="card-header">
                <h3>Timesheet</h3>
                <span className="date">11 Mar 2019</span>
            </div>
            <div className="punch-status">
                <p>{punchedIn ? 'Punch In at' : 'Punched Out'}</p>
                <span>{punchedIn ? 'Wed, 11th Mar 2019 10.00 AM' : '--'}</span>
            </div>
            <div className="timer-circle">
                <svg viewBox="0 0 36 36" className="circular-chart">
                    <path className="circle-bg" d="M18 2.0845
                        a 15.9155 15.9155 0 0 1 0 31.831
                        a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <path className="circle" strokeDasharray="30, 100" d="M18 2.0845
                        a 15.9155 15.9155 0 0 1 0 31.831
                        a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <text x="18" y="20.35" className="percentage">3.45 hrs</text>
                </svg>
            </div>
            <button
                className="btn-punch"
                onClick={() => setPunchedIn(!punchedIn)}
                style={!punchedIn ? { background: '#e0e0e0', color: '#333', boxShadow: 'none' } : {}}
            >
                {punchedIn ? 'Punch Out' : 'Punch In'}
            </button>
            <div className="timesheet-footer">
                <div className="stat">
                    <span className="label">BREAK</span>
                    <span className="value">1.21 hrs</span>
                </div>
                <div className="stat">
                    <span className="label">Overtime</span>
                    <span className="value">3 hrs</span>
                </div>
            </div>
        </div>
    );
}

function StatisticsCard() {
    return (
        <div className="card statistics-card">
            <div className="card-header">
                <h3>Statistics</h3>
            </div>
            <div className="stat-bars">
                <div className="stat-bar-item">
                    <div className="stat-info">
                        <span>Today</span>
                        <span>3.45 / 8 hrs</span>
                    </div>
                    <div className="progress-bar">
                        <div className="progress" style={{ width: '43%', backgroundColor: '#03dac6' }}></div>
                    </div>
                </div>
                <div className="stat-bar-item">
                    <div className="stat-info">
                        <span>This Week</span>
                        <span>28 / 40 hrs</span>
                    </div>
                    <div className="progress-bar">
                        <div className="progress" style={{ width: '70%', backgroundColor: '#ff9800' }}></div>
                    </div>
                </div>
                <div className="stat-bar-item">
                    <div className="stat-info">
                        <span>This Month</span>
                        <span>90 / 160 hrs</span>
                    </div>
                    <div className="progress-bar">
                        <div className="progress" style={{ width: '56%', backgroundColor: '#2196f3' }}></div>
                    </div>
                </div>
                <div className="stat-bar-item">
                    <div className="stat-info">
                        <span>Remaining</span>
                        <span>90 / 160 hrs</span>
                    </div>
                    <div className="progress-bar">
                        <div className="progress" style={{ width: '56%', backgroundColor: '#f44336' }}></div>
                    </div>
                </div>
                <div className="stat-bar-item">
                    <div className="stat-info">
                        <span>Overtime</span>
                        <span>5 hrs</span>
                    </div>
                    <div className="progress-bar">
                        <div className="progress" style={{ width: '20%', backgroundColor: '#cddc39' }}></div>
                    </div>
                </div>
            </div>
        </div>
    );
}

function TodayActivityCard() {
    return (
        <div className="card activity-card">
            <div className="card-header">
                <h3>Today Activity</h3>
            </div>
            <div className="activity-timeline">
                <div className="timeline-item">
                    <div className="timeline-icon"></div>
                    <div className="timeline-content">
                        <div className="timeline-title">Punch In at</div>
                        <div className="timeline-time"><span className="material-icons-round tiny">schedule</span> 10.00 AM</div>
                    </div>
                </div>
                <div className="timeline-item">
                    <div className="timeline-icon"></div>
                    <div className="timeline-content">
                        <div className="timeline-title">Punch Out at</div>
                        <div className="timeline-time"><span className="material-icons-round tiny">schedule</span> 11.00 AM</div>
                    </div>
                </div>
                <div className="timeline-item">
                    <div className="timeline-icon"></div>
                    <div className="timeline-content">
                        <div className="timeline-title">Punch In at</div>
                        <div className="timeline-time"><span className="material-icons-round tiny">schedule</span> 11.30 AM</div>
                    </div>
                </div>
                <div className="timeline-item">
                    <div className="timeline-icon"></div>
                    <div className="timeline-content">
                        <div className="timeline-title">Punch Out at</div>
                        <div className="timeline-time"><span className="material-icons-round tiny">schedule</span> 01.30 PM</div>
                    </div>
                </div>
            </div>
        </div>
    );
}

function AttendanceList() {
    return (
        <div className="card attendance-list-card">
            <div className="card-header">
                <h3>Attendance List</h3>
            </div>
            <div className="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>S. No</th>
                            <th>Date</th>
                            <th>Punch In</th>
                            <th>Punch Out</th>
                            <th>Production</th>
                            <th>Break</th>
                            <th>Overtime</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>1</td>
                            <td>19 Feb 2019</td>
                            <td>10 AM</td>
                            <td>7 PM</td>
                            <td>9 hrs</td>
                            <td>1 hrs</td>
                            <td>2 hrs</td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>20 Feb 2019</td>
                            <td>10 AM</td>
                            <td>7 PM</td>
                            <td>9 hrs</td>
                            <td>1 hrs</td>
                            <td>0 hrs</td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>21 Feb 2019</td>
                            <td>10 AM</td>
                            <td>7 PM</td>
                            <td>9 hrs</td>
                            <td>1 hrs</td>
                            <td>0 hrs</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function ChartCard() {
    return (
        <div className="card chart-card">
            <div className="card-header">
                <h3>Daily Records</h3>
            </div>
            <div className="chart-box">
                <div className="bar-chart">
                    <div className="bar-col">
                        <div className="bar" style={{ height: '60%', backgroundColor: '#2196f3' }}></div>
                        <span className="label">29</span>
                    </div>
                    <div className="bar-col">
                        <div className="bar" style={{ height: '40%', backgroundColor: '#03dac6' }}></div>
                        <span className="label">30</span>
                    </div>
                    <div className="bar-col">
                        <div className="bar" style={{ height: '80%', backgroundColor: '#2196f3' }}></div>
                        <span className="label">01</span>
                    </div>
                    <div className="bar-col">
                        <div className="bar" style={{ height: '30%', backgroundColor: '#03dac6' }}></div>
                        <span className="label">02</span>
                    </div>
                    <div className="bar-col">
                        <div className="bar" style={{ height: '90%', backgroundColor: '#2196f3' }}></div>
                        <span className="label">03</span>
                    </div>
                    <div className="bar-col">
                        <div className="bar" style={{ height: '50%', backgroundColor: '#03dac6' }}></div>
                        <span className="label">04</span>
                    </div>
                </div>
            </div>
        </div>
    );
}

function Dashboard() {
    return (
        <div className="dashboard-container">
            <div className="page-title">
                <h1>Attendance</h1>
                <div className="breadcrumb">Dashboard / Attendance</div>
            </div>

            <div className="widgets-grid">
                <TimesheetCard />
                <StatisticsCard />
                <TodayActivityCard />
            </div>

            <div className="widgets-grid-bottom">
                <AttendanceList />
                <ChartCard />
            </div>
        </div>
    );
}

function App() {
    const [mobileOpen, setMobileOpen] = useState(false);

    return (
        <div className="app-container">
            <Sidebar mobileOpen={mobileOpen} toggleMobile={() => setMobileOpen(!mobileOpen)} />
            <main className="main-content">
                <Header toggleMenu={() => setMobileOpen(!mobileOpen)} />
                <Dashboard />
            </main>
            {/* Overlay for mobile sidebar */}
            {mobileOpen && (
                <div
                    style={{
                        position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                        background: 'rgba(0,0,0,0.5)', zIndex: 90
                    }}
                    onClick={() => setMobileOpen(false)}
                ></div>
            )}
        </div>
    );
}

// Render the App
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
