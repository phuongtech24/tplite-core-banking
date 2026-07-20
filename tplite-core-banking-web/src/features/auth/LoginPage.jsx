import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { login } from './authSlice';

export default function LoginPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { status, error } = useSelector((state) => state.auth);
  const [form, setForm] = useState({ email: 'admin@tplite.com', password: 'Admin@123' });

  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  const handleSubmit = async (event) => {
    event.preventDefault();
    const result = await dispatch(login(form));
    if (login.fulfilled.match(result)) navigate('/dashboard');
  };

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit}>
        <h1>TPLite Core Banking</h1>
        <p>Login with backend JWT API /api/v1/auth/login</p>
        <label>Email</label>
        <input name="email" value={form.email} onChange={handleChange} />
        <label>Password</label>
        <input name="password" type="password" value={form.password} onChange={handleChange} />
        {error && <div className="form-error">{error}</div>}
        <button disabled={status === 'loading'}>{status === 'loading' ? 'Signing in...' : 'Sign in'}</button>
      </form>
    </div>
  );
}
