import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logoutLocal } from '../features/auth/authSlice';

const navItems = [
  ['Dashboard', '/dashboard', 'bi-grid-1x2-fill'],
  ['Customers', '/customers', 'bi-people-fill'],
  ['Accounts', '/accounts', 'bi-bank2'],
  ['Cards', '/cards', 'bi-credit-card-2-front-fill'],
  ['Transfers', '/transfers', 'bi-arrow-left-right'],
  ['Loans', '/loans', 'bi-file-earmark-text-fill'],
  ['Notifications', '/notifications', 'bi-bell-fill'],
  ['Audit', '/audit', 'bi-shield-check'],
];

export default function AppLayout() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);

  const handleLogout = () => {
    dispatch(logoutLocal());
    navigate('/login');
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark"><i className="bi bi-lightning-charge-fill" /></div>
          <div>
            <strong>TPLite</strong>
            <span>Core Banking</span>
          </div>
        </div>
        <nav>
          {navItems.map(([label, to, icon]) => (
            <NavLink key={to} to={to} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <i className={`bi ${icon}`} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="main-panel">
        <header className="topbar">
          <div>
            <span className="eyebrow">Spring Boot ? React Redux ? REST v1</span>
            <h1>Banking Operations Console</h1>
            <p>Manage customers, accounts, cards, transfers, loans, notifications and audit logs.</p>
          </div>
          <div className="user-box">
            <div className="avatar"><i className="bi bi-person-fill" /></div>
            <div>
              <strong>{user?.fullName || user?.email || 'Logged in'}</strong>
              <span>{user?.roles?.join(', ') || 'Authenticated user'}</span>
            </div>
            <button className="btn ghost" onClick={handleLogout}>Logout</button>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  );
}
