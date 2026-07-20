import { useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import FormPanel from '../../components/FormPanel';
import StatusBadge from '../../components/StatusBadge';
import CopyButton from '../../components/CopyButton';
import { bankingApi } from '../../services/bankingApi';

const initialForm = { accountType: 'SAVING', currency: 'VND', initialBalance: 0 };

export default function AccountsPage() {
  const [form, setForm] = useState(initialForm);
  const [status, setStatus] = useState('');
  const [message, setMessage] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);

  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  const createAccount = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.createAccount({ ...form, initialBalance: Number(form.initialBalance) });
      setMessage({ type: 'success', text: 'Account created successfully.' });
      setForm(initialForm);
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  const changeStatus = async (id, nextStatus) => {
    try {
      await bankingApi.updateAccountStatus(id, nextStatus);
      setMessage({ type: 'success', text: `Account moved to ${nextStatus}.` });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <section>
      <PageHeader title="Accounts" description="Create accounts, filter by status, and update account state through REST v1 APIs." />
      <FormPanel title="Create account" description="POST /api/v1/accounts with backend validation.">
        <form className="form-grid compact" onSubmit={createAccount}>
          <label>Type<select name="accountType" value={form.accountType} onChange={handleChange}><option>PAYMENT</option><option>SAVING</option><option>LOAN</option></select></label>
          <label>Currency<input name="currency" value={form.currency} onChange={handleChange} maxLength="3" /></label>
          <label>Initial balance<input name="initialBalance" type="number" min="0" value={form.initialBalance} onChange={handleChange} /></label>
          <button><i className="bi bi-plus-circle" /> Create</button>
        </form>
      </FormPanel>
      {message && <div className={`state-card ${message.type}`}>{message.text}</div>}
      <div className="toolbar split">
        <label>Status filter<select value={status} onChange={(event) => setStatus(event.target.value)}><option value="">All</option><option>ACTIVE</option><option>LOCKED</option><option>CLOSED</option><option>PENDING</option></select></label>
      </div>
      <PaginatedTable
        fetcher={bankingApi.accounts}
        query={{ status: status || undefined }}
        reloadKey={reloadKey}
        columns={[
          { key: 'accountNumber', label: 'Account No.', render: (row) => <span className="copy-cell">{row.accountNumber}<CopyButton value={row.id} /></span> },
          { key: 'accountType', label: 'Type' },
          { key: 'currency', label: 'Currency' },
          { key: 'balance', label: 'Balance', render: (row) => Number(row.balance ?? 0).toLocaleString() },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'actions', label: 'Actions', render: (row) => <div className="row-actions"><button className="btn mini" onClick={() => changeStatus(row.id, 'ACTIVE')}>Active</button><button className="btn mini danger" onClick={() => changeStatus(row.id, 'LOCKED')}>Lock</button></div> },
        ]}
      />
    </section>
  );
}
