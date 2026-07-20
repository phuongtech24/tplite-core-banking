import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { authApi } from './authApi';
import { tokenStorage } from '../../services/tokenStorage';

export const login = createAsyncThunk('auth/login', async (payload) => {
  const data = await authApi.login(payload);
  tokenStorage.saveSession({
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
    user: {
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      roles: data.roles,
    },
  });
  return data;
});

export const loadMe = createAsyncThunk('auth/loadMe', async () => authApi.me());

const initialState = {
  user: tokenStorage.getUser(),
  token: tokenStorage.getAccessToken(),
  status: 'idle',
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logoutLocal: (state) => {
      tokenStorage.clear();
      state.user = null;
      state.token = null;
      state.status = 'idle';
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.token = action.payload.accessToken;
        state.user = {
          userId: action.payload.userId,
          email: action.payload.email,
          fullName: action.payload.fullName,
          roles: action.payload.roles,
        };
      })
      .addCase(login.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(loadMe.fulfilled, (state, action) => {
        state.user = action.payload;
      });
  },
});

export const { logoutLocal } = authSlice.actions;
export default authSlice.reducer;
