// DOM Elements
const sidebar = document.querySelector('.sidebar');
const menuToggle = document.querySelector('.menu-toggle');
const mainContent = document.querySelector('.main-content');

// Sidebar Toggle for Mobile
if (menuToggle) {
    menuToggle.addEventListener('click', () => {
        sidebar.classList.toggle('active');
    });
}

// Close sidebar when clicking outside on mobile
document.addEventListener('click', (e) => {
    if (window.innerWidth <= 768) {
        if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
            sidebar.classList.remove('active');
        }
    }
});

// Punch Out Button Interaction (Mock)
const punchBtn = document.querySelector('.btn-punch');
if (punchBtn) {
    punchBtn.addEventListener('click', () => {
        if (punchBtn.textContent.trim() === 'Punch Out') {
            punchBtn.textContent = 'Punch In';
            punchBtn.style.backgroundColor = '#ddd';
            punchBtn.style.color = '#333';
            punchBtn.style.boxShadow = 'none';
        } else {
            punchBtn.textContent = 'Punch Out';
            punchBtn.style.backgroundColor = 'var(--secondary-color)';
            punchBtn.style.color = 'white';
            punchBtn.style.boxShadow = '0 4px 10px rgba(0, 230, 118, 0.3)';
        }
    });
}

// Simple Greeting based on time
const pageTitle = document.querySelector('.page-title h1');
if (pageTitle) {
    const hour = new Date().getHours();
    let greeting = 'Good Morning';
    if (hour >= 12 && hour < 17) greeting = 'Good Afternoon';
    if (hour >= 17) greeting = 'Good Evening';
    // We can append this greeting or just leave it as is. 
    // pageTitle.textContent = `${greeting}, Admin`; 
}

// Initialize Material Icons (if needed, they load via CSS/Font)
