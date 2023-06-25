package org.example.hedera.client.impl;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.KeyList;
import com.hedera.hashgraph.sdk.PrivateKey;
import lombok.SneakyThrows;
import org.example.hedera.client.AccountClient;
import org.example.hedera.mapper.AccountMapper;
import org.example.hedera.model.HederaAccount;

import java.util.Collections;

public class HederaAccountClient implements AccountClient {

    @Override
    @SneakyThrows
    public HederaAccount createAccount(final HederaAccount rootAccount, final long initialBalance) {
        final PrivateKey privateKey = PrivateKey.generateED25519();

        final AccountId accountId = AccountMapper.INSTANCE.toAccountCreateTransaction(privateKey, initialBalance)
                .execute(rootAccount.client())
                .getReceipt(rootAccount.client()).accountId;

        return new HederaAccount(accountId, privateKey.getPublicKey(), privateKey);
    }

    @Override
    public KeyList createThresholdKey(final int minSigns, final HederaAccount... accounts) {
        PrivateKey[] keys = new PrivateKey[accounts.length];

        for (int i = 0; i < accounts.length; i++) {
            keys[i] = accounts[i].accountPrivateKey();
        }

        KeyList thresholdKey = KeyList.withThreshold(minSigns);
        Collections.addAll(thresholdKey, keys);
        return thresholdKey;
    }

    @Override
    @SneakyThrows
    public AccountBalance retrieveBalance(final HederaAccount account) {
        return new AccountBalanceQuery()
                .setAccountId(account.accountId())
                .execute(account.client());
    }
}
