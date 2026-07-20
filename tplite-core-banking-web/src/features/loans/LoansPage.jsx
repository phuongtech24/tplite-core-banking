import { useState } from 'react';
import PageHeader from '../../components/PageHeader';
import PaginatedTable from '../../components/PaginatedTable';
import FormPanel from '../../components/FormPanel';
import StatusBadge from '../../components/StatusBadge';
import CopyButton from '../../components/CopyButton';
import { bankingApi } from '../../services/bankingApi';

const productInitial = { code: '', name: '', interestRate: 8.5, minAmount: 1000000, maxAmount: 50000000, minTermMonths: 6, maxTermMonths: 36 };
const loanInitial = { loanProductId: '', principalAmount: 10000000, termMonths: 12 };

export default function LoansPage() {
  const [product, setProduct] = useState(productInitial);
  const [loan, setLoan] = useState(loanInitial);
  const [message, setMessage] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);

  const createProduct = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.createLoanProduct({ ...product, interestRate: Number(product.interestRate), minAmount: Number(product.minAmount), maxAmount: Number(product.maxAmount), minTermMonths: Number(product.minTermMonths), maxTermMonths: Number(product.maxTermMonths) });
      setMessage({ type: 'success', text: 'Loan product created.' });
      setProduct(productInitial);
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  const applyLoan = async (event) => {
    event.preventDefault();
    setMessage(null);
    try {
      await bankingApi.createLoan({ ...loan, principalAmount: Number(loan.principalAmount), termMonths: Number(loan.termMonths) });
      setMessage({ type: 'success', text: 'Loan application submitted.' });
      setLoan(loanInitial);
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  const reviewLoan = async (id, action) => {
    try {
      if (action === 'approve') await bankingApi.approveLoan(id);
      else await bankingApi.rejectLoan(id);
      setMessage({ type: 'success', text: `Loan ${action}d.` });
      setReloadKey((value) => value + 1);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <section>
      <PageHeader title="Loans" description="Create loan products, apply for loans, and approve/reject loan applications." />
      <div className="two-column">
        <FormPanel title="Create loan product" description="Admin API: POST /api/v1/admin/loan-products">
          <form className="form-stack" onSubmit={createProduct}>
            <div className="form-grid two"><input placeholder="Code" value={product.code} onChange={(e) => setProduct({ ...product, code: e.target.value })} /><input placeholder="Name" value={product.name} onChange={(e) => setProduct({ ...product, name: e.target.value })} /></div>
            <div className="form-grid three"><input type="number" step="0.01" placeholder="Interest %" value={product.interestRate} onChange={(e) => setProduct({ ...product, interestRate: e.target.value })} /><input type="number" placeholder="Min amount" value={product.minAmount} onChange={(e) => setProduct({ ...product, minAmount: e.target.value })} /><input type="number" placeholder="Max amount" value={product.maxAmount} onChange={(e) => setProduct({ ...product, maxAmount: e.target.value })} /></div>
            <div className="form-grid two"><input type="number" placeholder="Min term" value={product.minTermMonths} onChange={(e) => setProduct({ ...product, minTermMonths: e.target.value })} /><input type="number" placeholder="Max term" value={product.maxTermMonths} onChange={(e) => setProduct({ ...product, maxTermMonths: e.target.value })} /></div>
            <button>Create product</button>
          </form>
        </FormPanel>
        <FormPanel title="Apply for loan" description="Customer API: POST /api/v1/loans">
          <form className="form-stack" onSubmit={applyLoan}>
            <input placeholder="Loan product UUID" value={loan.loanProductId} onChange={(e) => setLoan({ ...loan, loanProductId: e.target.value })} />
            <div className="form-grid two"><input type="number" placeholder="Principal amount" value={loan.principalAmount} onChange={(e) => setLoan({ ...loan, principalAmount: e.target.value })} /><input type="number" placeholder="Term months" value={loan.termMonths} onChange={(e) => setLoan({ ...loan, termMonths: e.target.value })} /></div>
            <button>Apply loan</button>
          </form>
        </FormPanel>
      </div>
      {message && <div className={`state-card ${message.type}`}>{message.text}</div>}
      <h3 className="section-title">Active Loan Products</h3>
      <PaginatedTable
        fetcher={bankingApi.loanProducts}
        reloadKey={reloadKey}
        columns={[
          { key: 'code', label: 'Code', render: (row) => <span className="copy-cell">{row.code}<CopyButton value={row.id} /></span> },
          { key: 'name', label: 'Product' },
          { key: 'interestRate', label: 'Rate' },
          { key: 'minAmount', label: 'Min', render: (row) => Number(row.minAmount ?? 0).toLocaleString() },
          { key: 'maxAmount', label: 'Max', render: (row) => Number(row.maxAmount ?? 0).toLocaleString() },
        ]}
      />
      <h3 className="section-title">Loan Applications</h3>
      <PaginatedTable
        fetcher={bankingApi.staffLoans}
        reloadKey={reloadKey}
        columns={[
          { key: 'loanCode', label: 'Code' },
          { key: 'principalAmount', label: 'Principal', render: (row) => Number(row.principalAmount ?? 0).toLocaleString() },
          { key: 'outstandingBalance', label: 'Outstanding', render: (row) => Number(row.outstandingBalance ?? 0).toLocaleString() },
          { key: 'termMonths', label: 'Term' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'actions', label: 'Actions', render: (row) => <div className="row-actions"><button className="btn mini" onClick={() => reviewLoan(row.id, 'approve')}>Approve</button><button className="btn mini danger" onClick={() => reviewLoan(row.id, 'reject')}>Reject</button></div> },
        ]}
      />
    </section>
  );
}
