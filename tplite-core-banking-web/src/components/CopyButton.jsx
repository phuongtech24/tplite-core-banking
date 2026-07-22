export default function CopyButton({ value }) {
  const copy = async () => {
    if (!value) return;
    await navigator.clipboard?.writeText(String(value));
  };

  return (
    <button className="icon-button" type="button" onClick={copy} title="Copy">
      <i className="bi bi-clipboard" />
    </button>
  );
}
