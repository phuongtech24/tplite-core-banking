package com.tplite.core_banking.module.admin.dto;

public class AdminDashboardResponse {
    private long totalUsers;
    private long totalCustomers;
    private long totalAccounts;
    private long totalCards;
    private long totalTransactions;
    private long totalLoans;
    private long totalNotifications;
    private long totalAuditLogs;

    public AdminDashboardResponse(
            long totalUsers,
            long totalCustomers,
            long totalAccounts,
            long totalCards,
            long totalTransactions,
            long totalLoans,
            long totalNotifications,
            long totalAuditLogs
    ) {
        this.totalUsers = totalUsers;
        this.totalCustomers = totalCustomers;
        this.totalAccounts = totalAccounts;
        this.totalCards = totalCards;
        this.totalTransactions = totalTransactions;
        this.totalLoans = totalLoans;
        this.totalNotifications = totalNotifications;
        this.totalAuditLogs = totalAuditLogs;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getTotalAccounts() { return totalAccounts; }
    public void setTotalAccounts(long totalAccounts) { this.totalAccounts = totalAccounts; }
    public long getTotalCards() { return totalCards; }
    public void setTotalCards(long totalCards) { this.totalCards = totalCards; }
    public long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }
    public long getTotalLoans() { return totalLoans; }
    public void setTotalLoans(long totalLoans) { this.totalLoans = totalLoans; }
    public long getTotalNotifications() { return totalNotifications; }
    public void setTotalNotifications(long totalNotifications) { this.totalNotifications = totalNotifications; }
    public long getTotalAuditLogs() { return totalAuditLogs; }
    public void setTotalAuditLogs(long totalAuditLogs) { this.totalAuditLogs = totalAuditLogs; }
}
