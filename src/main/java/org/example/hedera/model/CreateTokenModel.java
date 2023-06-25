package org.example.hedera.model;

import com.hedera.hashgraph.sdk.TokenSupplyType;
import lombok.Builder;

@Builder
public record CreateTokenModel(String tokenName,
                               String tokenSymbol,
                               Long initialSupply,
                               Long maxSupply,
                               Integer decimals,
                               TokenSupplyType supplyType) {
}
