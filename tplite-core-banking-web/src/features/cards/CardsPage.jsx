import { useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import FormPanel from '../../components/FormPanel';
import StatusBadge from '../../components/StatusBadge';
import CopyButton from '../../components/CopyButton';
import { bankingApi } from '../../services/bankingApi';

const initialForm = { accountId: '', cardType: 'DEBIT', dailyLimit: 5000000 };

export default function CardsPage() {
  const [form, setForm] = useState(initialForm);
  const [status, setStatus] = useState('');
  const [message, setMessage] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);
  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  const createCard = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.createCard({ ...form, dailyLimit: Number(form.dailyLimit) });
      setMessage({ type: 'success', text: 'Card issued successfully.' });
      setForm(initialForm);
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  const changeStatus = async (id, nextStatus) => {
    try {
      await bankingApi.updateCardStatus(id, nextStatus);
      setMessage({ type: 'success', text: `Card moved to ${nextStatus}.` });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <section>
      <PageHeader title="Cards" description="Issue cards from an account UUID and manage card lifecycle." />
      <FormPanel title="Issue new card" description="POST /api/v1/cards; enum inputs are validated as strings on backend.">
        <form className="form-grid compact" onSubmit={createCard}>
          <label>Account UUID<input name="accountId" value={form.accountId} onChange={handleChange} placeholder="Copy account id from Accounts" /></label>
          <label>Card type<select name="cardType" value={form.cardType} onChange={handleChange}><option>DEBIT</option><option>CREDIT</option><option>PREPAID</option></select></label>
          <label>Daily limit<input name="dailyLimit" type="number" min="0" value={form.dailyLimit} onChange={handleChange} /></label>
          <button><i className="bi bi-credit-card" /> Issue</button>
        </form>
      </FormPanel>
      {message && <div className={`state-card ${message.type}`}>{message.text}</div>}
      <div className="toolbar split"><label>Status filter<select value={status} onChange={(event) => setStatus(event.target.value)}><option value="">All</option><option>ACTIVE</option><option>LOCKED</option><option>EXPIRED</option><option>CANCELLED</option></select></label></div>
      <PaginatedTable
        fetcher={bankingApi.cards}
        query={{ status: status || undefined }}
        reloadKey={reloadKey}
        columns={[
          { key: 'cardNumberMasked', label: 'Card', render: (row) => <span className="copy-cell">{row.cardNumberMasked}<CopyButton value={row.id} /></span> },
          { key: 'cardType', label: 'Type' },
          { key: 'dailyLimit', label: 'Daily limit', render: (row) => Number(row.dailyLimit ?? 0).toLocaleString() },
          { key: 'expiredAt', label: 'Expire' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'actions', label: 'Actions', render: (row) => <div className="row-actions"><button className="btn mini" onClick={() => changeStatus(row.id, 'ACTIVE')}>Active</button><button className="btn mini danger" onClick={() => changeStatus(row.id, 'LOCKED')}>Lock</button></div> },
        ]}
      />
    </section>
  );
}
