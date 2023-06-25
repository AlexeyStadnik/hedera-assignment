package org.example.hedera.client;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TransactionId;
import org.example.hedera.model.CreateTokenModel;
import org.example.hedera.model.HederaAccount;

public interface TokenClient {
    TokenId createToken(HederaAccount account, CreateTokenModel newToken);

    TokenInfo retrieveToken(HederaAccount account, TokenId tokenId);

    TransactionId mintTokens(HederaAccount account, TokenId tokenId, long mintingAmount);

    TransactionId associateAccount(HederaAccount payer, HederaAccount affectedAccount, TokenId tokenId);

    TransactionId transferTokens(HederaAccount from, AccountId to, TokenId tokenId, long amount);
}
