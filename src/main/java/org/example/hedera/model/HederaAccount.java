package org.example.hedera.model;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import lombok.SneakyThrows;

import java.time.Duration;

public record HederaAccount(AccountId accountId,
                            PublicKey accountPublicKey,
                            PrivateKey accountPrivateKey,
                            Client client) {

    // Hedera testnet nodes are sometimes slow to respond so need to increase the timeout
    public static final int HEDERA_NODES_TIMEOUT_SECONDS = 160;

    public HederaAccount(final AccountId accountId,
                         final PublicKey accountPublicKey,
                         final PrivateKey accountPrivateKey) {
        this(accountId, accountPublicKey, accountPrivateKey, createClient(accountId, accountPrivateKey));


    }

    @SneakyThrows
    private static Client createClient(final AccountId accountId,
                                       final PrivateKey accountPrivateKey) {
        Client client = Client.forTestnet();
        client.setOperator(accountId, accountPrivateKey);
        client.setRequestTimeout(Duration.ofSeconds(HEDERA_NODES_TIMEOUT_SECONDS));
        return client;
    }
}
