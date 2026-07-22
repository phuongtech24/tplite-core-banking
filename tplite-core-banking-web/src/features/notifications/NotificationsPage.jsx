import { useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import StatusBadge from '../../components/StatusBadge';
import { bankingApi } from '../../services/bankingApi';

export default function NotificationsPage() {
  const [status, setStatus] = useState('');
  const [message, setMessage] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);

  const markRead = async (id) => {
    try {
      await bankingApi.markNotificationRead(id);
      setMessage({ type: 'success', text: 'Notification marked as read.' });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <section>
      <PageHeader title="Notifications" description="Notification inbox. Kafka can publish transfer events; UI consumes persisted notification APIs." />
      <div className="toolbar split"><label>Status filter<select value={status} onChange={(event) => setStatus(event.target.value)}><option value="">All</option><option>PENDING</option><option>SENT</option><option>FAILED</option><option>READ</option></select></label></div>
      {message && <div className={`state-card ${message.type}`}>{message.text}</div>}
      <PaginatedTable
        fetcher={bankingApi.notifications}
        query={{ status: status || undefined }}
        reloadKey={reloadKey}
        columns={[
          { key: 'title', label: 'Title' },
          { key: 'content', label: 'Content' },
          { key: 'notificationType', label: 'Type' },
          { key: 'channel', label: 'Channel' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'actions', label: 'Actions', render: (row) => <button className="btn mini" disabled={row.status === 'READ'} onClick={() => markRead(row.id)}>Mark read</button> },
        ]}
      />
    </section>
  );
}
