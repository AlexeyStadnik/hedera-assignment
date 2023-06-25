package org.example.hedera.client;

import com.hedera.hashgraph.sdk.KeyList;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessage;
import org.example.hedera.model.HederaAccount;

import java.util.function.Consumer;

public interface TopicClient {
    TopicId createTopic(HederaAccount account, String memo);

    TopicId createTopic(HederaAccount account, String memo, KeyList thresholdKey);

    Status publishMessage(HederaAccount account, TopicId topicId, String message);

    void subscribeToTopic(HederaAccount account, TopicId topicId, Consumer<TopicMessage> callback);

}
