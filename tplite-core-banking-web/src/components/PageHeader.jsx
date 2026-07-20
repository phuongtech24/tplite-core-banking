export default function PageHeader({ title, description, action }) {
  return (
    <div className="page-header">
      <div>
        <span className="eyebrow">TPLite Core Banking</span>
        <h2>{title}</h2>
        {description && <p>{description}</p>}
      </div>
      {action && <div className="page-actions">{action}</div>}
    </div>
  );
}
