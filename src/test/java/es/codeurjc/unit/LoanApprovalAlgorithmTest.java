package es.codeurjc.unit;
import es.codeurjc.model.LoanEvaluationResult;
import es.codeurjc.service.EuriborService;
import es.codeurjc.service.loan.LoanApprovalAlgorithm;
import es.codeurjc.service.loan.LoanRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanApprovalAlgorithmTest {

    @Mock
    private EuriborService euriborService;

    private LoanApprovalAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new LoanApprovalAlgorithm(euriborService);
    }
    @Test
    @DisplayName("Estrategia TDD: Se rechaza el prestamo si tiene un valor menor a 1000")
    void algorithm_less_than_1000(){
        LoanRequest request = new LoanRequest();
        request.setAmount(500.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(2000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertFalse(result.isApproved(), "Este prestamo debe rechazarse");
        assertEquals("Valor fuera de rango", result.getReason());
    }

    @Test
    @DisplayName("Estrategia TDD: Se rechaza el prestamo si tiene un valor mayor a 5000")
    void algorithm_greather_than_5000(){
        LoanRequest request = new LoanRequest();
        request.setAmount(500000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(2000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertFalse(result.isApproved(), "Este prestamo debe rechazarse");
        assertEquals("Valor fuera de rango", result.getReason());
    }
    @Test
    @DisplayName("Estrategia TDD: Se rechaza el prestamo si tiene un plazo menor a 6 meses")
    void algorithm_termOutRangeLT6(){
        LoanRequest request = new LoanRequest();
        request.setAmount(1500.0);
        request.setTermMonths(4);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(2000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertFalse(result.isApproved(), "Este prestamo debe rechazarse por plazo invalido");
        assertEquals("Plazo fuera de rango", result.getReason());
    }

    @Test
    @DisplayName("Estrategia TDD: Se rechaza el prestamo si tiene un plazo mayor a 120 meses")
    void algorithm_termOutRangeGT120(){
        LoanRequest request = new LoanRequest();
        request.setAmount(1500.0);
        request.setTermMonths(121);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(2000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertFalse(result.isApproved(), "Este prestamo debe rechazarse por plazo invalido");
        assertEquals("Plazo fuera de rango", result.getReason());
    }

    @Test
    @DisplayName ("Estrategia TDD: Se rechaza el prestamo si el saldo es insuficiente")
    void algorithm_insufficientBalance(){
        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(3000.0);
        request.setMonthlyIncome(3000.0);
        LoanEvaluationResult result = algorithm.evaluate(request);
        assertFalse(result.isApproved(), "Deberia rechazarse este prestamo pues el saldo es insuficiente");
        assertEquals("Saldo insuficiente", result.getReason());
    }

    @Test
    @DisplayName("Estrategia TDD: Se aprueeba un prestamo con datos validos")
    void algorithm_approvedBasic(){
        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);
        LoanEvaluationResult result = algorithm.evaluate(request);

        assertTrue(result.isApproved(), "Este prestamo debe aprobarse");
        assertEquals("Aprobado", result.getReason());
    }

    @Test
    @DisplayName("Estrategia TDD: Un prestamo aprobado con Euribor al 3% tiene un tipo de interes del 5%")
    void algorithm_interestRate_euribor3() {
        when(euriborService.getEuribor()).thenReturn(3.0);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertTrue(result.isApproved());
        assertEquals(5.0, result.getInterestRate());
    }
    @Test
    @DisplayName("Estrategia TDD: Un prestamo aprobado con Euribor al 3% tiene una cuota de 875€ al mes")
    void algorithm_monthlyPayment_euribor3() {
        when(euriborService.getEuribor()).thenReturn(3.0);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertTrue(result.isApproved());
        assertEquals(5.0, result.getInterestRate(), 0.01);
        assertEquals(875.0, result.getMonthlyPayment(), 0.01);
    }
    @Test
    @DisplayName("Estrategia TDD: Un prestamo aprobado con Euribor al 3.5% tiene una cuota de 878.17€ al mes")
    void algorithm_monthlyPayment_euribor35() {
        when(euriborService.getEuribor()).thenReturn(3.5);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertTrue(result.isApproved());
        assertEquals(5.5, result.getInterestRate(), 0.01);
        assertEquals(879.17, result.getMonthlyPayment(), 0.01);
    }
    @Test
    @DisplayName("Estrategia TDD: Un prestamo aprobado con Euribor al 5% tiene una cuota de 891.67€ al mes")
    void algorithm_monthlyPayment_euribor5() {
        when(euriborService.getEuribor()).thenReturn(5.0);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertTrue(result.isApproved());
        assertEquals(7.0, result.getInterestRate(), 0.01);
        assertEquals(891.67, result.getMonthlyPayment(), 0.01);
    }
    @Test
    @DisplayName("Estrategia TDD: Se rechaza el prestamo si la cuota supera el 40% de los ingresos mensuales")
    void algorithm_rejected_whenMonthlyPaymentExceeds40PercentOfIncome() {
        when(euriborService.getEuribor()).thenReturn(3.0);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(2000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertFalse(result.isApproved(), "Este prestamo debe rechazarse porque la cuota supera el 40% de los ingresos");
    }
    @Test
    @DisplayName("Estrategia TDD: Se rechaza el prestamo si el plazo corto hace que la cuota supere el 40% de los ingresos")
    void algorithm_rejected_whenShortTermMakesMonthlyPaymentTooHigh() {
        when(euriborService.getEuribor()).thenReturn(5.0);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(7);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertFalse(result.isApproved(), "Este prestamo debe rechazarse porque la cuota supera el 40% de los ingresos");
        assertEquals("Cuota demasiado alta", result.getReason());
    }
    @Test
    @DisplayName("Estrategia TDD: Se aprueba el prestamo si la cuota no supera el 40% de los ingresos")
    void algorithm_approved_whenMonthlyPaymentIsWithin40PercentOfIncome() {
        when(euriborService.getEuribor()).thenReturn(3.0);

        LoanRequest request = new LoanRequest();
        request.setAmount(20000.0);
        request.setTermMonths(24);
        request.setCustomerBalance(5000.0);
        request.setMonthlyIncome(3000.0);

        LoanEvaluationResult result = algorithm.evaluate(request);

        assertTrue(result.isApproved(), "Este prestamo debe aprobarse porque la cuota no supera el 40% de los ingresos");
        assertEquals("Aprobado", result.getReason());
        assertEquals(875.0, result.getMonthlyPayment(), 0.01);
    }
}
