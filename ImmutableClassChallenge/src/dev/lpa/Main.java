package dev.lpa;

import dev.bank.Bank;
import dev.bank.BankAccount;
import dev.bank.BankCustomer;

public class Main {
    public static void main(String[] args) {
//        BankAccount account = new BankAccount(BankAccount.AccountType.CURRENT, 500);
//        System.out.println(account);
//        BankCustomer joe = new BankCustomer("Joe", 500.00, 10000.00);
//        System.out.println(joe);

        Bank bank = new Bank(3214567);
        BankCustomer joe = bank.addCustomer("Joe", 500.00, 1000.00);
        System.out.println(joe);

        if (bank.doTransaction(joe.getCustomerId(), BankAccount.AccountType.CURRENT, 35)) {
            System.out.println(joe);
        }

        if (bank.doTransaction(joe.getCustomerId(), BankAccount.AccountType.CURRENT, -135)) {
            System.out.println(joe);
        }

        BankAccount checking = joe.getAccount(BankAccount.AccountType.CURRENT);
        var joesCheckingTransactions = checking.getTransactions();
        joesCheckingTransactions.forEach((k, v) -> System.out.println(k + ": " + v));

//        System.out.println("--------------------------------");
//        for (var txn : joesCheckingTransactions.values()) {
//            txn.setCustomerId(2);
//            txn.setTransactionAmount(10000.00);
//        }
//        joesCheckingTransactions.forEach((k, v) -> System.out.println(k + ": " + v));

        joe.getAccount(BankAccount.AccountType.CURRENT).getTransactions().clear();
        System.out.println("-----------------------------------");
        joe.getAccount(BankAccount.AccountType.CURRENT).getTransactions()
                .forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
