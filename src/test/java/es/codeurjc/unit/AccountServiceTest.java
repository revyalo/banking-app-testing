package es.codeurjc.unit;


import es.codeurjc.model.Account;
import es.codeurjc.model.User;
import es.codeurjc.model.Transaction;
import es.codeurjc.repository.AccountRepository;
import es.codeurjc.repository.TransactionRepository;
import es.codeurjc.service.AccountService;
import es.codeurjc.service.RandomService;
import es.codeurjc.service.notifications.EmailNotificationService;
import es.codeurjc.service.notifications.SmsNotificationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitario de AccountService")
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private  TransactionRepository transactionRepository;
    @Mock
    private EmailNotificationService emailService;
    @Mock
    private SmsNotificationService smsService;
    @Mock
    private RandomService randomService;
    @InjectMocks
    private AccountService accountService;


    @Test
    @DisplayName("Una transferencia exitosa valida saldos y guarda datos")
    void successfulTransfers_updates_and_saves(){
        User user1 = new User();
        user1.setNotificationType(User.NotificationType.EMAIL);
        User user2 = new User();
        user2.setNotificationType(User.NotificationType.SMS);

        Account accountFrom = new Account("ES123", Account.AccountType.CHECKING, 1000.0);
        accountFrom.setUser(user1);

        Account accountTo = new Account("ES664", Account.AccountType.SAVINGS, 500.0);
        accountTo.setUser(user2);

        when(accountRepository.findByAccountNumber("ES123")).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findByAccountNumber("ES664")).thenReturn(Optional.of(accountTo));

        accountService.transfer("ES123", "ES664", 200.0);

        assertThat(accountFrom.getBalance()).isEqualTo(800.0);
        assertThat(accountTo.getBalance()).isEqualTo(700.0);

        verify(accountRepository, times(1)).save(accountFrom);
        verify(accountRepository, times(1)).save(accountTo);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }
    @Test
    @DisplayName("Lanza excepcion si se intenta transferir a la misma cuenta")
    void transfer_sameaccount(){
        Account accountFrom = new Account("ES123", Account.AccountType.CHECKING, 1000.0);
        when(accountRepository.findByAccountNumber("ES123")).thenReturn(Optional.of(accountFrom));

        assertThatThrownBy(() -> accountService.transfer("ES123", "ES123", 200.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot transfer to same account");

        verify(accountRepository, never()).save(any(Account.class));

    }
    @Test
    @DisplayName("Generar una cuenta correctamente")
    void createAccount(){
        User user1 = new User();
    }

    @Test
    @DisplayName("Lanza excepeción si el deposito es menor o igual que 0")
    void deposit_withnopositiveamount_exceptio(){
        assertThatThrownBy(() -> accountService.deposit("cuenta",0,"hola")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Amount must be positive");

        verifyNoInteractions(accountRepository,transactionRepository,emailService,smsService);
    }

    @Test
    @DisplayName("Lanza excepeción si el deposito supera el maximo")
    void deposit_withamonutgtlimit_exceptio(){
        assertThatThrownBy(() -> accountService.deposit("cuenta",10001,"hola")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Amount exceeds maximum deposit limit");

        verifyNoInteractions(accountRepository,transactionRepository,emailService,smsService);
    }

    @Test
    @DisplayName("Prueba de ingreso por email")
    void succesfuldeposit_withemail(){
        User user = new User();
        user.setNotificationType(User.NotificationType.EMAIL);

        Account account =   new Account("cuenta",Account.AccountType.CHECKING,1000.0);
        account.setUser(user);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account deposit = accountService.deposit("cuenta",200.0,"hola");

        assertThat(deposit.getBalance()).isEqualTo(1200.0);

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(account);

        verify(emailService).sendNotification(eq(user), eq(es.codeurjc.model.Notification.NotificationType.DEPOSIT), eq(AccountService.DEPOSIT_CONFIRMATION), contains("200,00"));

        verifyNoInteractions(smsService);
    }
    @Test
    @DisplayName("Prueba de ingreso por sms")
    void succesfuldeposit_withsms(){
        User user = new User();
        user.setNotificationType(User.NotificationType.SMS);

        Account account =   new Account("cuenta",Account.AccountType.CHECKING,1000.0);
        account.setUser(user);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account deposit = accountService.deposit("cuenta",200.0,"hola");

        assertThat(deposit.getBalance()).isEqualTo(1200.0);

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(account);

        verify(smsService).sendNotification(eq(user), eq(es.codeurjc.model.Notification.NotificationType.DEPOSIT), eq(AccountService.DEPOSIT_CONFIRMATION), contains("200,00"));

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Prueba quick deposit")
    void quickDeposit(){
        User user = new User();
        user.setNotificationType(User.NotificationType.EMAIL);

        Account account = new Account("cuenta",Account.AccountType.CHECKING,1000.0);
        account.setUser(user);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        accountService.deposit("cuenta",100.0);

        verify(transactionRepository).save(argThat(transaction -> transaction.getType() == Transaction.TransactionType.DEPOSIT && transaction.getDescription().equals("Quick Deposit") && transaction.getAmount() == 100.0));
    }

    @Test
    @DisplayName("Prueba excepcion si retirada es menor o igual a 0")
    void withdraw_withNoPositiveAmount() {
        assertThatThrownBy(() -> accountService.withdraw("cuenta", 0, "Retirada")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Amount must be positive");

        verifyNoInteractions(accountRepository, transactionRepository, emailService, smsService);
    }

    @Test
    @DisplayName("Prueba excepcion si retirada es mayor a lo perimtido")
    void withdraw_withgtmaximun() {
        assertThatThrownBy(() -> accountService.withdraw("cuenta", 5001, "Retirada")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Amount exceeds maximum withdrawal limit");

        verifyNoInteractions(accountRepository, transactionRepository, emailService, smsService);
    }

    @Test
    @DisplayName("Prueba excepcion si no hay saldo suficiente")
    void withdraw_withInsufficientFunds() {
        User user = new User();
        user.setNotificationType(User.NotificationType.EMAIL);

        Account account = new Account("cuenta", Account.AccountType.CHECKING, 100.0);
        account.setUser(user);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.withdraw("cuenta", 200.0, "Retirada"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient funds");

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verifyNoInteractions(emailService, smsService);
    }
    @Test
    @DisplayName("Prueba retirada con email")
    void succesfulwithdraw_withemail() {
        User user = new User();
        user.setNotificationType(User.NotificationType.EMAIL);

        Account account = new Account("cuenta", Account.AccountType.CHECKING, 1000.0);
        account.setUser(user);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.withdraw("cuenta", 200.0, "Cajero");

        assertThat(result.getBalance()).isEqualTo(800.0);

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(account);

        verify(emailService).sendNotification(eq(user), eq(es.codeurjc.model.Notification.NotificationType.WITHDRAWAL), eq("Withdrawal Confirmation"), contains("200,00"));

        verifyNoInteractions(smsService);
    }
    @Test
    @DisplayName("Prueba retirada con sms")
    void succesfulwithdraw_withsms() {
        User user = new User();
        user.setNotificationType(User.NotificationType.SMS);

        Account account = new Account("cuenta", Account.AccountType.CHECKING, 1000.0);
        account.setUser(user);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.withdraw("cuenta", 200.0, "Cajero");

        assertThat(result.getBalance()).isEqualTo(800.0);

        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(account);

        verify(smsService).sendNotification(eq(user), eq(es.codeurjc.model.Notification.NotificationType.WITHDRAWAL), eq("Withdrawal"), contains("200,00"));

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Prueba excepcion borrado de cuenta con saldo distinto de 0")
    void rm_withNonZeroBalance() {
        Account account = new Account("cuenta", Account.AccountType.CHECKING, 100.0);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.rm("cuenta")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Cannot delete account with non-zero balance");

        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    @DisplayName("Borra la cuenta si el saldo es 0")
    void rm_withZeroBalance_deletesAccount() {
        Account account = new Account("cuenta", Account.AccountType.CHECKING, 0.0);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));

        accountService.rm("cuenta");

        verify(accountRepository).delete(account);
    }

    @Test
    @DisplayName("Devuelve la cuenta cuando existe")
    void getAccount_existingAccount() {
        Account account = new Account("cuenta", Account.AccountType.CHECKING, 1000.0);

        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.of(account));

        Account result = accountService.getAccount("cuenta");

        assertThat(result).isEqualTo(account);
    }
    @Test
    @DisplayName("Prueba excepción cuando la cuenta no existe")
    void getAccount_nonExistingAccount() {
        when(accountRepository.findByAccountNumber("cuenta")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount("cuenta")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Account not found");
    }
}
