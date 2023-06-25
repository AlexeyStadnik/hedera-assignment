package org.example.hedera;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import org.example.hedera.client.AccountClient;
import org.example.hedera.client.TokenClient;
import org.example.hedera.client.TopicClient;
import org.example.hedera.client.impl.HederaAccountClient;
import org.example.hedera.client.impl.HederaTokenClient;
import org.example.hedera.client.impl.HederaTopicClient;
import org.example.hedera.model.HederaAccount;

public class HederaFactory {
    public static AccountClient createAccountClient() {
        return new HederaAccountClient();
    }

    public static TokenClient createTokenClient() {
        return new HederaTokenClient();
    }

    public static HederaAccount createAccount(final String accountId, final String privateKey) {
        final PrivateKey hederaPrivateKey = PrivateKey.fromString(privateKey);
        return new HederaAccount(AccountId.fromString(accountId), hederaPrivateKey.getPublicKey(), hederaPrivateKey);
    }

    public static TopicClient createTopicClient() {
        return new HederaTopicClient();
    }
}
