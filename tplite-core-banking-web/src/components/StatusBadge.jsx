const toneByStatus = {
  ACTIVE: 'success',
  APPROVED: 'success',
  COMPLETED: 'success',
  SUCCESS: 'success',
  READ: 'muted',
  PENDING: 'warning',
  PENDING_REVIEW: 'warning',
  SUBMITTED: 'warning',
  BLOCKED: 'danger',
  REJECTED: 'danger',
  FAILED: 'danger',
  INACTIVE: 'muted',
  UNREAD: 'primary',
};

export default function StatusBadge({ value }) {
  if (!value) return <span className="badge muted">-</span>;
  const tone = toneByStatus[value] || 'primary';
  return <span className={`badge ${tone}`}>{value}</span>;
}
