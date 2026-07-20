import { useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import FormPanel from '../../components/FormPanel';
import StatusBadge from '../../components/StatusBadge';
import { bankingApi } from '../../services/bankingApi';

const profileInitial = { fullName: '', dateOfBirth: '', gender: 'MALE', phone: '', email: '' };
const kycInitial = { documentType: 'NATIONAL_ID', documentNumber: '', issuedDate: '', expiredDate: '', issuedBy: '' };

export default function CustomersPage() {
  const [keyword, setKeyword] = useState('');
  const [profile, setProfile] = useState(profileInitial);
  const [kyc, setKyc] = useState(kycInitial);
  const [message, setMessage] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);

  const updateProfile = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.upsertMyCustomer(profile);
      setMessage({ type: 'success', text: 'Customer profile saved.' });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  const submitKyc = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.createKycDocument(kyc);
      setMessage({ type: 'success', text: 'KYC document submitted.' });
      setKyc(kycInitial);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  const reviewKyc = async (id, status) => {
    try {
      await bankingApi.reviewKycDocument(id, status);
      setMessage({ type: 'success', text: `KYC ${status.toLowerCase()}.` });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <section>
      <PageHeader title="Customers & KYC" description="Manage customer profile, submit KYC documents, and review customer/KYC data." />
      <div className="two-column">
        <FormPanel title="My customer profile" description="PUT /api/v1/customers/me">
          <form className="form-stack" onSubmit={updateProfile}>
            <input placeholder="Full name" value={profile.fullName} onChange={(e) => setProfile({ ...profile, fullName: e.target.value })} />
            <div className="form-grid two">
              <input type="date" value={profile.dateOfBirth} onChange={(e) => setProfile({ ...profile, dateOfBirth: e.target.value })} />
              <select value={profile.gender} onChange={(e) => setProfile({ ...profile, gender: e.target.value })}><option>MALE</option><option>FEMALE</option><option>OTHER</option></select>
            </div>
            <div className="form-grid two">
              <input placeholder="Phone" value={profile.phone} onChange={(e) => setProfile({ ...profile, phone: e.target.value })} />
              <input placeholder="Email" value={profile.email} onChange={(e) => setProfile({ ...profile, email: e.target.value })} />
            </div>
            <button>Save profile</button>
          </form>
        </FormPanel>
        <FormPanel title="Submit KYC document" description="POST /api/v1/customers/me/kyc-documents">
          <form className="form-stack" onSubmit={submitKyc}>
            <div className="form-grid two"><select value={kyc.documentType} onChange={(e) => setKyc({ ...kyc, documentType: e.target.value })}><option>NATIONAL_ID</option><option>PASSPORT</option><option>DRIVER_LICENSE</option></select><input placeholder="Document number" value={kyc.documentNumber} onChange={(e) => setKyc({ ...kyc, documentNumber: e.target.value })} /></div>
            <div className="form-grid two"><input type="date" value={kyc.issuedDate} onChange={(e) => setKyc({ ...kyc, issuedDate: e.target.value })} /><input type="date" value={kyc.expiredDate} onChange={(e) => setKyc({ ...kyc, expiredDate: e.target.value })} /></div>
            <input placeholder="Issued by" value={kyc.issuedBy} onChange={(e) => setKyc({ ...kyc, issuedBy: e.target.value })} />
            <button>Submit KYC</button>
          </form>
        </FormPanel>
      </div>
      {message && <div className={`state-card ${message.type}`}>{message.text}</div>}
      <div className="toolbar"><input placeholder="Search customer by keyword..." value={keyword} onChange={(e) => setKeyword(e.target.value)} /></div>
      <PaginatedTable
        fetcher={bankingApi.customers}
        query={{ keyword }}
        reloadKey={reloadKey}
        columns={[
          { key: 'customerCode', label: 'Code' },
          { key: 'fullName', label: 'Full name' },
          { key: 'email', label: 'Email' },
          { key: 'phone', label: 'Phone' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
        ]}
      />
      <h3 className="section-title">KYC Review Queue</h3>
      <PaginatedTable
        fetcher={bankingApi.staffKycDocuments}
        reloadKey={reloadKey}
        columns={[
          { key: 'documentType', label: 'Type' },
          { key: 'documentNumber', label: 'Number' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'issuedBy', label: 'Issued by' },
          { key: 'actions', label: 'Actions', render: (row) => <div className="row-actions"><button className="btn mini" onClick={() => reviewKyc(row.id, 'VERIFIED')}>Approve</button><button className="btn mini danger" onClick={() => reviewKyc(row.id, 'REJECTED')}>Reject</button></div> },
        ]}
      />
    </section>
  );
}
