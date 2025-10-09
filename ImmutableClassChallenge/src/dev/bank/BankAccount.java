package dev.bank;

import dev.dto.Transaction;

import java.util.LinkedHashMap;
import java.util.Map;

public class BankAccount {
    public enum AccountType { CURRENT, SAVINGS }
    private final AccountType accountType;
    private double balance;
    private final Map<Long, Transaction> transactions = new LinkedHashMap<>();

    BankAccount(AccountType accountType, double balance) {
        this.accountType = accountType;
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public Map<Long, String> getTransactions() {
        Map<Long, String> txnMap = new LinkedHashMap<>();
        for (var txn : transactions.entrySet()) {
            txnMap.put(txn.getKey(), txn.getValue().toString());
        }
        return txnMap;
    }

    @Override
    public String toString() {
        return "%s $%.2f".formatted(accountType, balance);
    }

    void commitTransaction(int routingNumber, long transactionId, String customerId,double amount) {
        balance += amount;
        transactions.put(transactionId, new Transaction(routingNumber, Integer.parseInt(customerId), transactionId, amount ));
    }
}
