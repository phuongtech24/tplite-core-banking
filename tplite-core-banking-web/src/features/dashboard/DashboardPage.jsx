import { useEffect, useState } from 'react';
import { bankingApi } from '../../services/bankingApi';
import PageHeader from '../../components/PageHeader';
import DataState from '../../components/DataState';

export default function DashboardPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    bankingApi.dashboard()
      .then(setData)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const cards = [
    ['Customers', data?.totalCustomers, 'bi-people-fill', 'primary'],
    ['Accounts', data?.totalAccounts, 'bi-bank2', 'success'],
    ['Transactions', data?.totalTransactions, 'bi-arrow-left-right', 'warning'],
    ['Loans', data?.totalLoans, 'bi-file-earmark-text-fill', 'danger'],
  ];

  return (
    <section>
      <PageHeader title="Dashboard" description="A single console for banking operations: customer, account, transfer, loan, notification and audit." />
      <DataState loading={loading} error={error}>
        <div className="hero-card">
          <div>
            <span className="eyebrow">Production-style demo</span>
            <h3>Core Banking control center</h3>
            <p>REST v1 APIs, JWT/RBAC, ACID transactions, idempotency, Kafka-style notification flow and pageable data tables.</p>
          </div>
          <i className="bi bi-shield-lock-fill" />
        </div>
        <div className="metric-grid">
          {cards.map(([label, value, icon, tone]) => (
            <div className={`metric-card ${tone}`} key={label}>
              <i className={`bi ${icon}`} />
              <span>{label}</span>
              <strong>{value ?? '-'}</strong>
            </div>
          ))}
        </div>
      </DataState>
    </section>
  );
}
