import { createBrowserRouter, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import AppLayout from '../layouts/AppLayout';
import LoginPage from '../features/auth/LoginPage';
import DashboardPage from '../features/dashboard/DashboardPage';
import CustomersPage from '../features/customers/CustomersPage';
import AccountsPage from '../features/accounts/AccountsPage';
import CardsPage from '../features/cards/CardsPage';
import TransfersPage from '../features/transfers/TransfersPage';
import LoansPage from '../features/loans/LoansPage';
import NotificationsPage from '../features/notifications/NotificationsPage';
import AuditPage from '../features/audit/AuditPage';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: '/',
        element: <AppLayout />,
        children: [
          { index: true, element: <Navigate to="/dashboard" replace /> },
          { path: 'dashboard', element: <DashboardPage /> },
          { path: 'customers', element: <CustomersPage /> },
          { path: 'accounts', element: <AccountsPage /> },
          { path: 'cards', element: <CardsPage /> },
          { path: 'transfers', element: <TransfersPage /> },
          { path: 'loans', element: <LoansPage /> },
          { path: 'notifications', element: <NotificationsPage /> },
          { path: 'audit', element: <AuditPage /> },
        ],
      },
    ],
  },
]);
