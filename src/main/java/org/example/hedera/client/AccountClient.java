package org.example.hedera.client;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.KeyList;
import org.example.hedera.model.HederaAccount;

public interface AccountClient {

    HederaAccount createAccount(HederaAccount rootAccount, long initialBalance);

    KeyList createThresholdKey(int minSigns, HederaAccount... accounts);

    AccountBalance retrieveBalance(HederaAccount account);
}
