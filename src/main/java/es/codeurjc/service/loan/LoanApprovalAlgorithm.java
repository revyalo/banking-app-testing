package es.codeurjc.service.loan;

import es.codeurjc.model.LoanEvaluationResult;
import es.codeurjc.service.EuriborService;

public class LoanApprovalAlgorithm {

    private static final double MIN_AMOUNT = 1000.0;
    private static final double MAX_AMOUNT = 50000.0;
    private static final String VALUE_OUT = "Valor fuera de rango";

    private static final int MIN_TERM = 6;
    private static final int MAX_TERM = 120;
    private static final String TERM_OUT = "Plazo fuera de rango";
    private final EuriborService euriborService;

    public LoanApprovalAlgorithm(EuriborService euriborService) {
        this.euriborService = euriborService;
    }

    public LoanEvaluationResult evaluate(LoanRequest request) {
        if (request.getAmount() < MIN_AMOUNT || request.getAmount() > MAX_AMOUNT) {
            return new LoanEvaluationResult(false ,VALUE_OUT);
        }

        if (request.getTermMonths() < MIN_TERM || request.getTermMonths() > MAX_TERM){
            return new LoanEvaluationResult(false ,TERM_OUT);

        }
        if (request.getCustomerBalance() < request.getAmount() * 0.20) {
            return new LoanEvaluationResult(false, "Saldo insuficiente");
        }

        double interestRate = 2.0 + euriborService.getEuribor();
        double monthlyPayment = (request.getAmount() * (1+ interestRate / 100.0)) / request.getTermMonths();
        if (monthlyPayment > request.getMonthlyIncome() * 0.40) {return new LoanEvaluationResult(false, "Cuota demasiado alta");}
        return new LoanEvaluationResult(true, "Aprobado", request.getAmount(), interestRate, monthlyPayment);
    }
}
