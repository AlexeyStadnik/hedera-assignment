package org.example;

import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TransferTransaction;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.example.hedera.HederaFactory;
import org.example.hedera.client.AccountClient;
import org.example.hedera.client.TokenClient;
import org.example.hedera.client.TopicClient;
import org.example.hedera.model.HederaAccount;

import java.util.HashMap;
import java.util.Map;

public class AbstractBaseStepDefinitions {

    protected TopicClient topicClient;
    protected TokenClient tokenClient;
    protected AccountClient accountClient;
    protected HederaAccount rootAccount;
    protected Map<String, HederaAccount> accounts = new HashMap<>();
    protected TokenInfo tokenInfo;
    protected TransferTransaction transferTransaction;

    @Before
    public void setup() {
        final String accountId = System.getenv("ACCOUNT_ID");
        final String privateKey = System.getenv("PRIVATE_KEY");

        if (accountId == null || privateKey == null) {
            throw new RuntimeException("ACCOUNT_ID and PRIVATE_KEY environment variables must be set");
        }

        rootAccount = HederaFactory.createAccount(accountId, privateKey);
        topicClient = HederaFactory.createTopicClient();
        tokenClient = HederaFactory.createTokenClient();
        accountClient = HederaFactory.createAccountClient();
    }

    @After
    public void teardown() {
        accounts.values().forEach(account -> {
            try {
                account.client().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
