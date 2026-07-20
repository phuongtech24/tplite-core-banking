export default function FormPanel({ title, description, children, footer }) {
  return (
    <div className="form-panel">
      <div className="form-panel__head">
        <div>
          <h3>{title}</h3>
          {description && <p>{description}</p>}
        </div>
      </div>
      <div className="form-panel__body">{children}</div>
      {footer && <div className="form-panel__footer">{footer}</div>}
    </div>
  );
}
