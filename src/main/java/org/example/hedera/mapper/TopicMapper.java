package org.example.hedera.mapper;

import com.hedera.hashgraph.sdk.KeyList;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import org.example.hedera.model.HederaAccount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper

public interface TopicMapper {

    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);


    default TopicCreateTransaction toCreateTopicTransaction(final String memo,
                                                            final KeyList thresholdKey) {
        final TopicCreateTransaction transaction = new TopicCreateTransaction().setTopicMemo(memo);

        if (thresholdKey != null) {
            transaction.setSubmitKey(thresholdKey);
        }
        return transaction;
    }

    default TopicMessageSubmitTransaction toSubmitMessageTransaction(final HederaAccount account,
                                                                     final TopicId topicId,
                                                                     final String message) {
        return new TopicMessageSubmitTransaction()
                .setTopicId(topicId)
                .setMessage(message)
                .freezeWith(account.client());
    }
}
