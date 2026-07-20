import { useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import { bankingApi } from '../../services/bankingApi';

export default function AuditPage() {
  const [keyword, setKeyword] = useState('');
  return (
    <section>
      <PageHeader title="Audit Logs" description="Admin audit trail with pageable search." />
      <div className="toolbar">
        <input placeholder="Search action/resource..." value={keyword} onChange={(event) => setKeyword(event.target.value)} />
      </div>
      <PaginatedTable
        fetcher={bankingApi.auditLogs}
        query={{ keyword }}
        columns={[
          { key: 'action', label: 'Action' },
          { key: 'resourceType', label: 'Resource' },
          { key: 'ipAddress', label: 'IP' },
          { key: 'description', label: 'Description' },
          { key: 'createdAt', label: 'Created at' },
        ]}
      />
    </section>
  );
}
