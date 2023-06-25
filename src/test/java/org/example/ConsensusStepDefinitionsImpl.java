package org.example;

import com.hedera.hashgraph.sdk.KeyList;
import com.hedera.hashgraph.sdk.TopicId;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.example.hedera.model.HederaAccount;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConsensusStepDefinitionsImpl extends AbstractBaseStepDefinitions {

    private TopicId topicId;
    private KeyList thresholdKey;

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Given("a first account with more than {int} hbars")
    @SneakyThrows
    public void hederaAccountWithHbars(int initialBalance) {
        accounts.put("first", accountClient.createAccount(rootAccount, initialBalance));
    }

    @And("a second account with more than {int} hbars")
    public void aSecondAccountWithMoreThanHbars(int initialBalance) {
        accounts.put("second", accountClient.createAccount(rootAccount, initialBalance));
    }

    @When("A topic is created with the memo {string} with the first account as the submit key")
    public void aTopicIsCreatedWithTheMemoWithTheFirstAccountAsTheSubmitKey(String memo) {
        this.topicId = topicClient.createTopic(accounts.get("first"), memo);
    }

    @And("The message {string} is published to the topic")
    public void theMessageIsPublishedToTheTopic(String message) {
        topicClient.publishMessage(accounts.get("first"), topicId, message);
    }

    @Then("The message is received by the topic and can be printed to the console")
    public void theMessageIsReceivedByTheTopicAndCanBePrintedToTheConsole() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // Subscribing to the topic
        topicClient.subscribeToTopic(accounts.get("first"), topicId, (message) -> {
            countDownLatch.countDown();
            Assert.assertNotNull(message);
        });

        // Publishing a message to the topic
        topicClient.publishMessage(accounts.get("first"), topicId, "message");


        // Waiting for the message to be received, retrying several times because it can take a while
        boolean messageReceived = false;
        int retries = 0;

        while (!messageReceived && retries < 10) {
            // Hedera testnet is unstable so need to retry
            topicClient.publishMessage(accounts.get("first"), topicId, "message");
            messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
            retries++;
        }
        Assert.assertTrue(messageReceived);
    }

    @And("A {int} of {int} threshold key with the first and second account")
    public void aOfThresholdKeyWithTheFirstAndSecondAccount(int minSigns, int totalKeys) {
        final HederaAccount first = accounts.get("first");
        final HederaAccount second = accounts.get("second");
        this.thresholdKey = accountClient.createThresholdKey(minSigns, first, second);
    }

    @When("A topic is created with the memo {string} with the threshold key as the submit key")
    public void aTopicIsCreatedWithTheMemoWithTheThresholdKeyAsTheSubmitKey(String memo) {
        this.topicId = topicClient.createTopic(accounts.get("first"), memo, thresholdKey);
    }
}
