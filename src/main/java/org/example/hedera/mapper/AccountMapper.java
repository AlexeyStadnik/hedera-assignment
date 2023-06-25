package org.example.hedera.mapper;

import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    default AccountCreateTransaction toAccountCreateTransaction(final PrivateKey privateKey,
                                                                final long initialBalance) {
        final PublicKey publicKey = privateKey.getPublicKey();

        return new AccountCreateTransaction()
                .setKey(publicKey)
                .setInitialBalance(Hbar.from(initialBalance));
    }
}
