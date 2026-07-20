export default function DataState({ loading, error, empty, children }) {
  if (loading) return <div className="state-card">Loading data...</div>;
  if (error) return <div className="state-card error">{error}</div>;
  if (empty) return <div className="state-card">No data found.</div>;
  return children;
}
