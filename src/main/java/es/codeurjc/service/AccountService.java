package es.codeurjc.service;

import es.codeurjc.model.Account;
import es.codeurjc.model.User;
import es.codeurjc.model.Notification;
import es.codeurjc.model.Transaction;
import es.codeurjc.repository.AccountRepository;
import es.codeurjc.repository.TransactionRepository;
import es.codeurjc.service.notifications.EmailNotificationService;
import es.codeurjc.service.notifications.SmsNotificationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing bank accounts.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final RandomService randomService;
    public static final String DEPOSIT_CONFIRMATION = "Deposit Confirmation";
    private static final int MAX_DEPOSIT = 10000;
    private static final int MAX_TANSFER = 20000;
    private static final int MAX_WITHDRAW = 5000;
    private static final int ACCOUNT_NUMBER_BOUND = 1000000000;

    public AccountService(AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            EmailNotificationService emailService,
            SmsNotificationService smsService,
            RandomService randomService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.randomService = randomService;
    }

    /**
     * Create a new account
     */
    public Account createAccount(User user, Account.AccountType accountType) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, accountType, 0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    /**
     * Generate account number
     */
    private String generateAccountNumber() {
        return String.format("ES%010d", randomService.nextInt(ACCOUNT_NUMBER_BOUND));
    }

    /**
     * Get account by account number
     */
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /**
     * Get all accounts for a user
     */
    public List<Account> getUserAccounts(User user) {
        return accountRepository.findByUser(user);
    }

    /**
     * Deposit money into account
     */
    @Transactional
    public Account deposit(String accountNumber, double amount, String description) {

        validateDepositeAmount(amount);

        Account account = getAccount(accountNumber);
        account.deposit(amount);

        // Record transaction
        Transaction transaction = new Transaction(account, Transaction.TransactionType.DEPOSIT,
                amount, description);
        transactionRepository.save(transaction);

        Account savedAccount = accountRepository.save(account);

        // Send notification
        sendDepositeConfirmation(account, amount);
        return savedAccount;
    }

    /**
     * Quick deposit without description
     */


    @Transactional
    public Account deposit(String accountNumber, double amount){
        return deposit(accountNumber, amount, "Quick Deposit");
    }

    /**
     * Withdraw money from account
     */
    @Transactional
    public Account withdraw(String accountNumber, double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (amount > MAX_WITHDRAW) {
            throw new IllegalArgumentException("Amount exceeds maximum withdrawal limit");
        }

        Account account = getAccount(accountNumber);

        // Check balance
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.withdraw(amount);

        // Record transaction
        Transaction transaction = new Transaction(account, Transaction.TransactionType.WITHDRAWAL,
                amount, description);
        transactionRepository.save(transaction);

        Account savedAccount = accountRepository.save(account);

        sendNotificationByPreference(account.getUser(), Notification.NotificationType.WITHDRAWAL, "Withdrawal Confirmation", "Withdrawal", String.format("Withdrawal of %.2f EUR. New balance: %.2f EUR", amount, account.getBalance()), String.format("Withdrawal of %.2f EUR. New balance: %.2f EUR", amount, account.getBalance()));

        return savedAccount;
    }

    private void validateDepositeAmount(double amount){
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > MAX_DEPOSIT) {
            throw new IllegalArgumentException("Amount exceeds maximum deposit limit");
        }
    }

    private void sendDepositeConfirmation(Account account, double amount){
        // Send notification
        sendNotificationByPreference(account.getUser(), Notification.NotificationType.DEPOSIT, DEPOSIT_CONFIRMATION, DEPOSIT_CONFIRMATION, String.format("Deposit of %.2f EUR. New balance: %.2f EUR", amount, account.getBalance()), String.format("Deposit: %.2f EUR. Balance: %.2f EUR", amount, account.getBalance()));
    }

    /**
     * Transfer money between accounts
     */
    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > MAX_TANSFER) {
            throw new IllegalArgumentException("Amount exceeds maximum transfer limit");
        }

        Account m = getAccount(fromAccountNumber);
        Account o = getAccount(toAccountNumber);

        // Validate same account
        if (m.getAccountNumber().equals(o.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        // Check balance
        if (m.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Perform transfer
        m.withdraw(amount);
        o.deposit(amount);

        // Record transactions
        Transaction sentTransaction = new Transaction(m,
                Transaction.TransactionType.TRANSFER_SENT,
                amount,
                "Transfer to " + toAccountNumber);
        sentTransaction.setDestinationAccountNumber(toAccountNumber);
        transactionRepository.save(sentTransaction);

        Transaction receivedTransaction = new Transaction(o,
                Transaction.TransactionType.TRANSFER_RECEIVED,
                amount,
                "Transfer from " + fromAccountNumber);
        receivedTransaction.setDestinationAccountNumber(fromAccountNumber);
        transactionRepository.save(receivedTransaction);

        accountRepository.save(m);
        accountRepository.save(o);

        sendNotificationByPreference(m.getUser(), Notification.NotificationType.TRANSFER, "Transfer Sent", "Transfer Sent", String.format("Transfer of %.2f EUR to %s. New balance: %.2f EUR", amount, toAccountNumber, m.getBalance()), String.format("Transfer of %.2f EUR to %s. New balance: %.2f EUR", amount, toAccountNumber, m.getBalance()));

        sendNotificationByPreference(o.getUser(), Notification.NotificationType.TRANSFER, "Transfer Received", "Transfer Received", String.format("Transfer of %.2f EUR from %s. New balance: %.2f EUR", amount, fromAccountNumber, o.getBalance()), String.format("Transfer of %.2f EUR from %s. New balance: %.2f EUR", amount, fromAccountNumber, o.getBalance()));
    }

    /**
     * Delete account
     */
    public void rm(String accountNumber) {
        Account account = getAccount(accountNumber);

        if (account.getBalance() != 0) {
            throw new IllegalArgumentException("Cannot delete account with non-zero balance");
        }

        accountRepository.delete(account);
    }

    /**
     * Get account balance
     */
    public double getBalance(String accountNumber) {
        Account account = getAccount(accountNumber);
        return account.getBalance();
    }

    /**
     * Get account transactions
     */
    public List<Transaction> getTransactions(String accountNumber) {
        Account account = getAccount(accountNumber);
        return transactionRepository.findByAccountOrderByTimestampDesc(account);
    }

    private void sendNotificationByPreference(User user,Notification.NotificationType notificationType, String emailSubject, String smsSubject, String emailMessage, String smsMessage){
        User.NotificationType ntype = user.getNotificationType();

        if(ntype == User.NotificationType.EMAIL){
            emailService.sendNotification(user, notificationType, emailSubject, emailMessage);
        }else if(ntype ==User.NotificationType.SMS){
            smsService.sendNotification(user, notificationType, smsSubject, smsMessage);
        }
    }

    private void validateTransferAmount(double amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be positive");
        }
        if(amount > MAX_TANSFER){
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
