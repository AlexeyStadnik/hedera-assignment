package org.example.hedera.client.impl;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TokenInfoQuery;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransferTransaction;
import lombok.SneakyThrows;
import org.example.hedera.client.TokenClient;
import org.example.hedera.mapper.TokenMapper;
import org.example.hedera.model.CreateTokenModel;
import org.example.hedera.model.HederaAccount;

public class HederaTokenClient implements TokenClient {


    @Override
    @SneakyThrows
    public TokenId createToken(final HederaAccount account, final CreateTokenModel newToken) {
        TokenCreateTransaction tokenCreateTx = TokenMapper.INSTANCE.toCreateTokenTransaction(account, newToken);

        return tokenCreateTx
                .sign(account.accountPrivateKey())
                .execute(account.client())
                .getReceipt(account.client()).tokenId;
    }

    @Override
    @SneakyThrows
    public TokenInfo retrieveToken(final HederaAccount account, final TokenId tokenId) {
        return new TokenInfoQuery().setTokenId(tokenId).execute(account.client());
    }

    @Override
    @SneakyThrows
    public TransactionId mintTokens(final HederaAccount treasury,
                                    final TokenId tokenId,
                                    final long mintingAmount) {
        final Client client = treasury.client();
        final TokenMintTransaction transaction
                = TokenMapper.INSTANCE.toTokenMintTransaction(tokenId, mintingAmount, client);

        return transaction
                .sign(treasury.accountPrivateKey())
                .execute(client)
                .getReceipt(client).transactionId;
    }

    @Override
    @SneakyThrows
    public TransactionId associateAccount(final HederaAccount payer,
                                          final HederaAccount affectedAccount,
                                          final TokenId tokenId) {
        final TokenAssociateTransaction associateAliceTx
                = TokenMapper.INSTANCE.toAssociateTransaction(payer, affectedAccount, tokenId)
                .sign(affectedAccount.accountPrivateKey());

        return associateAliceTx
                .execute(payer.client())
                .getReceipt(affectedAccount.client()).transactionId;
    }

    @Override
    @SneakyThrows
    public TransactionId transferTokens(final HederaAccount from,
                                        final AccountId to,
                                        final TokenId tokenId,
                                        final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        final TransferTransaction tokenTransferTx
                = TokenMapper.INSTANCE.toTransferTransaction(from, to, tokenId, amount);

        return tokenTransferTx
                .execute(from.client())
                .getReceipt(from.client()).transactionId;
    }


}
