import { useEffect, useMemo, useState } from 'react';
import DataState from './DataState';
import PaginationBar from './PaginationBar';

export default function PaginatedTable({ fetcher, columns, query = {}, reloadKey = 0, pageSize = 10 }) {
  const [pageData, setPageData] = useState(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const queryKey = useMemo(() => JSON.stringify(query), [query]);

  useEffect(() => {
    setPage(0);
  }, [queryKey]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    fetcher({ page, size: pageSize, ...query })
      .then((data) => {
        if (!cancelled) setPageData(data);
      })
      .catch((err) => {
        if (!cancelled) setError(err.message);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, [fetcher, page, pageSize, reloadKey, queryKey]);

  const rows = pageData?.content || [];

  return (
    <DataState loading={loading} error={error} empty={!loading && rows.length === 0}>
      <div className="table-card">
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                {columns.map((column) => <th key={column.key}>{column.label}</th>)}
              </tr>
            </thead>
            <tbody>
              {rows.map((row, index) => (
                <tr key={row.id || row.transactionCode || row.accountNumber || index}>
                  {columns.map((column) => (
                    <td key={column.key}>{column.render ? column.render(row) : (row[column.key] ?? '-')}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <PaginationBar page={pageData?.page ?? page} totalPages={pageData?.totalPages ?? 1} onPageChange={setPage} />
      </div>
    </DataState>
  );
}
