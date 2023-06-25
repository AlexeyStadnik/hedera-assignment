package org.example.hedera.mapper;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TransferTransaction;
import org.example.hedera.model.CreateTokenModel;
import org.example.hedera.model.HederaAccount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

@Mapper
public interface TokenMapper {

    TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

    default TokenCreateTransaction toCreateTokenTransaction(final HederaAccount account,
                                                            final CreateTokenModel newToken) {
        final TokenCreateTransaction transaction = new TokenCreateTransaction()
                .setTokenName(newToken.tokenName())
                .setTokenSymbol(newToken.tokenSymbol())
                .setTokenType(TokenType.FUNGIBLE_COMMON)
                .setDecimals(newToken.decimals())
                .setInitialSupply(newToken.initialSupply())
                .setTreasuryAccountId(account.accountId())
                .setSupplyType(newToken.supplyType())
                .setSupplyKey(account.accountPublicKey());

        if (newToken.maxSupply() != null) {
            transaction.setMaxSupply(newToken.maxSupply());
        }
        transaction.freezeWith(account.client());
        return transaction;
    }

    default TokenMintTransaction toTokenMintTransaction(final TokenId tokenId,
                                                        final long mintingAmount,
                                                        final Client client) {
        return new TokenMintTransaction()
                .setTokenId(tokenId)
                .setAmount(mintingAmount)
                .freezeWith(client);
    }

    default TokenAssociateTransaction toAssociateTransaction(final HederaAccount payer,
                                                             final HederaAccount affectedAccount,
                                                             final TokenId tokenId) {
        return new TokenAssociateTransaction()
                .setAccountId(affectedAccount.accountId())
                .setTokenIds(Collections.singletonList(tokenId))
                .freezeWith(payer.client());
    }

    default TransferTransaction toTransferTransaction(final HederaAccount from,
                                                      final AccountId to,
                                                      final TokenId tokenId,
                                                      final long amount) {
        return new TransferTransaction()
                .addTokenTransfer(tokenId, from.accountId(), -amount)
                .addTokenTransfer(tokenId, to, amount)
                .freezeWith(from.client());
    }
}
