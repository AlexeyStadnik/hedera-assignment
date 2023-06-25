package org.example.hedera.client.impl;

import com.hedera.hashgraph.sdk.KeyList;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessage;
import com.hedera.hashgraph.sdk.TopicMessageQuery;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import lombok.SneakyThrows;
import org.example.hedera.client.TopicClient;
import org.example.hedera.mapper.TopicMapper;
import org.example.hedera.model.HederaAccount;

import java.util.function.Consumer;

public class HederaTopicClient implements TopicClient {

    public static final TopicMapper TOPIC_MAPPER = TopicMapper.INSTANCE;

    @Override
    @SneakyThrows
    public TopicId createTopic(final HederaAccount account, final String memo) {
        return createTopic(account, memo, null);
    }

    @Override
    @SneakyThrows
    public TopicId createTopic(final HederaAccount account, final String memo, final KeyList thresholdKey) {
        final TopicCreateTransaction transaction = TOPIC_MAPPER.toCreateTopicTransaction(memo, thresholdKey);

        return transaction
                .execute(account.client())
                .getReceipt(account.client()).topicId;
    }

    @Override
    @SneakyThrows
    public Status publishMessage(final HederaAccount account, final TopicId topicId, final String message) {
        TopicMessageSubmitTransaction transaction
                = TOPIC_MAPPER.toSubmitMessageTransaction(account, topicId, message)
                .sign(account.accountPrivateKey());

        return transaction
                .execute(account.client())
                .getReceipt(account.client()).status;
    }

    @Override
    public void subscribeToTopic(final HederaAccount account,
                                 final TopicId topicId,
                                 final Consumer<TopicMessage> callback) {
        new TopicMessageQuery()
                .setTopicId(topicId)
                .subscribe(account.client(), callback);
    }
}
