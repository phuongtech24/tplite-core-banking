import { apiClient, unwrapData } from './apiClient';

export const bankingApi = {
  dashboard: async () => unwrapData(await apiClient.get('/admin/dashboard')),

  me: async () => unwrapData(await apiClient.get('/me')),
  updateMe: async (payload) => unwrapData(await apiClient.put('/me', payload)),

  customers: async (params) => unwrapData(await apiClient.get('/staff/customers', { params })),
  myCustomer: async () => unwrapData(await apiClient.get('/customers/me')),
  upsertMyCustomer: async (payload) => unwrapData(await apiClient.put('/customers/me', payload)),
  createMyCustomer: async (payload) => unwrapData(await apiClient.post('/customers/me', payload)),
  kycDocuments: async (params) => unwrapData(await apiClient.get('/customers/me/kyc-documents', { params })),
  createKycDocument: async (payload) => unwrapData(await apiClient.post('/customers/me/kyc-documents', payload)),
  staffKycDocuments: async (params) => unwrapData(await apiClient.get('/staff/kyc-documents', { params })),
  reviewKycDocument: async (id, status) => unwrapData(await apiClient.patch(`/staff/kyc-documents/${id}/review`, { status })),

  accounts: async (params) => unwrapData(await apiClient.get('/accounts/my', { params })),
  accountDetail: async (id) => unwrapData(await apiClient.get(`/accounts/${id}`)),
  createAccount: async (payload) => unwrapData(await apiClient.post('/accounts', payload)),
  updateAccountStatus: async (id, status) => unwrapData(await apiClient.patch(`/accounts/${id}/status`, { status })),

  cards: async (params) => unwrapData(await apiClient.get('/cards/my', { params })),
  cardDetail: async (id) => unwrapData(await apiClient.get(`/cards/${id}`)),
  createCard: async (payload) => unwrapData(await apiClient.post('/cards', payload)),
  updateCardStatus: async (id, status) => unwrapData(await apiClient.patch(`/cards/${id}/status`, { status })),

  transactions: async (params) => unwrapData(await apiClient.get('/transactions/my', { params })),
  transfer: async (payload, idempotencyKey) => unwrapData(await apiClient.post('/transfers', payload, {
    headers: idempotencyKey ? { 'Idempotency-Key': idempotencyKey } : {},
  })),

  loans: async (params) => unwrapData(await apiClient.get('/loans/my', { params })),
  staffLoans: async (params) => unwrapData(await apiClient.get('/staff/loans', { params })),
  loanProducts: async (params) => unwrapData(await apiClient.get('/loan-products', { params })),
  adminLoanProducts: async (params) => unwrapData(await apiClient.get('/admin/loan-products', { params })),
  createLoanProduct: async (payload) => unwrapData(await apiClient.post('/admin/loan-products', payload)),
  createLoan: async (payload) => unwrapData(await apiClient.post('/loans', payload)),
  approveLoan: async (id) => unwrapData(await apiClient.patch(`/admin/loans/${id}/approve`)),
  rejectLoan: async (id) => unwrapData(await apiClient.patch(`/admin/loans/${id}/reject`)),

  notifications: async (params) => unwrapData(await apiClient.get('/notifications/my', { params })),
  markNotificationRead: async (id) => unwrapData(await apiClient.patch(`/notifications/${id}/read`)),

  auditLogs: async (params) => unwrapData(await apiClient.get('/admin/audit-logs', { params })),
};
