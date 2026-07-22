import { useMemo, useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import FormPanel from '../../components/FormPanel';
import StatusBadge from '../../components/StatusBadge';
import { bankingApi } from '../../services/bankingApi';

export default function TransfersPage() {
  const [form, setForm] = useState({ fromAccountId: '', toAccountId: '', amount: '', description: '' });
  const [message, setMessage] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);
  const idempotencyKey = useMemo(() => crypto.randomUUID?.() || String(Date.now()), [reloadKey]);

  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.transfer({ ...form, amount: Number(form.amount) }, idempotencyKey);
      setMessage({ type: 'success', text: 'Transfer created. Backend guarantees ACID transaction + idempotency.' });
      setForm({ fromAccountId: '', toAccountId: '', amount: '', description: '' });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <section>
      <PageHeader title="Transfers" description="Money movement flow with Idempotency-Key, pessimistic lock, transaction rollback, and audit logs." />
      <FormPanel title="Create transfer" description={`Idempotency-Key: ${idempotencyKey}`}>
        <form className="form-grid compact" onSubmit={handleSubmit}>
          <label>From account UUID<input name="fromAccountId" placeholder="Debit account id" value={form.fromAccountId} onChange={handleChange} /></label>
          <label>To account UUID<input name="toAccountId" placeholder="Credit account id" value={form.toAccountId} onChange={handleChange} /></label>
          <label>Amount<input name="amount" type="number" min="1" placeholder="Amount" value={form.amount} onChange={handleChange} /></label>
          <label>Description<input name="description" placeholder="Transfer note" value={form.description} onChange={handleChange} /></label>
          <button><i className="bi bi-send-check" /> Transfer</button>
        </form>
      </FormPanel>
      {message && <div className={`state-card ${message.type}`}>{message.text}</div>}
      <PaginatedTable
        fetcher={bankingApi.transactions}
        reloadKey={reloadKey}
        columns={[
          { key: 'transactionCode', label: 'Code' },
          { key: 'amount', label: 'Amount', render: (row) => Number(row.amount ?? 0).toLocaleString() },
          { key: 'currency', label: 'Currency' },
          { key: 'type', label: 'Type' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'createdAt', label: 'Created at' },
        ]}
      />
    </section>
  );
}
