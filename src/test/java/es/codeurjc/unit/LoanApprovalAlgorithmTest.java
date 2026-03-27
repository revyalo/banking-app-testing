package es.codeurjc.unit;
import es.codeurjc.model.LoanEvaluationResult;
import es.codeurjc.service.loan.LoanApprovalAlgorithm;
import es.codeurjc.service.loan.LoanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoanApprovalAlgorithmTest {
    private final LoanApprovalAlgorithm algorithm = new LoanApprovalAlgorithm();

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
}
