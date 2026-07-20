export default function PaginationBar({ page, totalPages, onPageChange }) {
  return (
    <div className="pagination-bar">
      <button disabled={page <= 0} onClick={() => onPageChange(page - 1)}>Previous</button>
      <span>Page {page + 1} / {Math.max(totalPages || 1, 1)}</span>
      <button disabled={page + 1 >= totalPages} onClick={() => onPageChange(page + 1)}>Next</button>
    </div>
  );
}
