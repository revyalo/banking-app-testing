package es.codeurjc.unit;


import es.codeurjc.model.Account;
import es.codeurjc.model.User;
import es.codeurjc.model.Transaction;
import es.codeurjc.repository.AccountRepository;
import es.codeurjc.repository.TransactionRepository;
import es.codeurjc.service.AccountService;
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
    
}
