package com.lzl.experiment13.template;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccountRepository {
    private final Map<String, BankAccount> accounts = new LinkedHashMap<>();

    public AccountRepository() {
    }

    public BankAccount findByAccountNo(String accountNo) {
        return accounts.get(accountNo);
    }

    public void addAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("账户对象不能为空");
        }
        accounts.put(account.getAccountNo(), account);
    }
}
