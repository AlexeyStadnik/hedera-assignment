package org.example.utils;

import com.hedera.hashgraph.sdk.TokenSupplyType;
import org.example.hedera.model.CreateTokenModel;

import javax.annotation.Nullable;

public class TestDataUtils {

    public static final int DEFAULT_DECIMALS = 2;

    public static CreateTokenModel createTokenModel(final String name,
                                                    final String symbol,
                                                    Long initialSupply,
                                                    @Nullable Long maxSupply,
                                                    final TokenSupplyType supplyType) {
        final CreateTokenModel.CreateTokenModelBuilder tokenBuilder = CreateTokenModel.builder()
                .tokenName(name)
                .tokenSymbol(symbol)
                .decimals(DEFAULT_DECIMALS)
                .initialSupply(initialSupply)
                .supplyType(supplyType);

        if (maxSupply != null) {
            tokenBuilder.maxSupply(maxSupply);
        }

        return tokenBuilder.build();
    }
}
