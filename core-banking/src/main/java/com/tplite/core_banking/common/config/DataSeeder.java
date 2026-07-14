package com.tplite.core_banking.common.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.entity.AccountType;
import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.auth.entity.Role;
import com.tplite.core_banking.module.auth.entity.UserRole;
import com.tplite.core_banking.module.auth.repository.RoleRepository;
import com.tplite.core_banking.module.auth.repository.UserRoleRepository;
import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.customer.entity.Gender;
import com.tplite.core_banking.module.customer.repository.CustomerRepository;
import com.tplite.core_banking.module.loan.entity.LoanProduct;
import com.tplite.core_banking.module.loan.entity.LoanProductStatus;
import com.tplite.core_banking.module.loan.repository.LoanProductRepository;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Component
public class DataSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String DEFAULT_PASSWORD = "Password@123";

    private final boolean seedEnabled;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanProductRepository loanProductRepository;

    public DataSeeder(
            @Value("${app.seed.enabled:true}") boolean seedEnabled,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            LoanProductRepository loanProductRepository
    ) {
        this.seedEnabled = seedEnabled;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.loanProductRepository = loanProductRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.info("Data seed is disabled");
            return;
        }

        User admin = seedUser("admin@tplite.vn", "System Admin", "ADMIN");
        User staff = seedUser("staff@tplite.vn", "Bank Staff", "STAFF");
        User customerUser = seedUser("customer@tplite.vn", "Demo Customer", "CUSTOMER");

        Customer customer = seedCustomer(customerUser);
        seedCustomerAccounts(customerUser, customer);
        seedLoanProduct();

        log.info("Seed data ready. Demo users: {}, {}, {}", admin.getEmail(), staff.getEmail(), customerUser.getEmail());
    }

    private User seedUser(String email, String fullName, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(email, passwordEncoder.encode(DEFAULT_PASSWORD), fullName);
                    return userRepository.save(newUser);
                });

        assignRoleIfMissing(user, roleName);
        return user;
    }

    private void assignRoleIfMissing(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Missing seed role: " + roleName));

        Set<String> currentRoles = userRoleRepository.findByUserId(user.getId())
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(java.util.stream.Collectors.toSet());

        if (currentRoles.contains(roleName)) {
            return;
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    private Customer seedCustomer(User user) {
        return customerRepository.findByUser(user)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setUser(user);
                    customer.setCustomerCode("CUS000000001");
                    customer.setFullName(user.getFullName());
                    customer.setDateOfBirth(LocalDate.of(1998, 1, 1));
                    customer.setGender(Gender.OTHER);
                    customer.setPhone("0900000000");
                    customer.setEmail(user.getEmail());
                    customer.setStatus(CustomerStatus.ACTIVE);
                    return customerRepository.save(customer);
                });
    }

    private void seedCustomerAccounts(User user, Customer customer) {
        if (!accountRepository.findByUser(user, PageRequest.of(0, 1)).isEmpty()) {
            return;
        }

        createAccount(user, customer, "100000000001", new BigDecimal("10000000.00"));
        createAccount(user, customer, "100000000002", new BigDecimal("5000000.00"));
    }

    private void createAccount(User user, Customer customer, String accountNumber, BigDecimal balance) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            return;
        }

        Account account = new Account();
        account.setUser(user);
        account.setCustomer(customer);
        account.setAccountNumber(accountNumber);
        account.setAccountType(AccountType.PAYMENT);
        account.setCurrency("VND");
        account.setBalance(balance);
        account.setStatus(AccountStatus.ACTIVE);
        account.setOpenedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private void seedLoanProduct() {
        if (loanProductRepository.existsByCode("PERSONAL_BASIC")) {
            return;
        }

        LoanProduct product = new LoanProduct();
        product.setCode("PERSONAL_BASIC");
        product.setName("Personal Basic Loan");
        product.setInterestRate(new BigDecimal("12.00"));
        product.setMinAmount(new BigDecimal("1000000.00"));
        product.setMaxAmount(new BigDecimal("200000000.00"));
        product.setMinTermMonths(6);
        product.setMaxTermMonths(60);
        product.setStatus(LoanProductStatus.ACTIVE);
        loanProductRepository.save(product);
    }
}
