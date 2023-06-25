package org.example;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TransferTransaction;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.example.hedera.model.CreateTokenModel;
import org.example.hedera.model.HederaAccount;
import org.example.utils.TestDataUtils;
import org.junit.Assert;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class TokensStepDefinitions extends AbstractBaseStepDefinitions {


    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Given("A Hedera account with more than 100 hbar")
    @SneakyThrows
    public void hederaAccountWithHbars() {
        hederaAccountWithHbars(100);
    }

    @Given("A first hedera account with more than {int} hbar")
    public void hederaAccountWithHbars(int initialBalance) {
        accounts.put("first", accountClient.createAccount(rootAccount, initialBalance));
    }

    @And("A second Hedera account")
    public void aSecondHederaAccount() {
        accounts.put("second", accountClient.createAccount(rootAccount, 0L));
    }

    @And("The second account holds {int} HTT tokens")
    public void theSecondAccountHoldsHTTTokens(int amount) {
        AccountBalance balance = accountClient.retrieveBalance(accounts.get("second"));
        final Long tokenBalance = balance.tokens.get(tokenInfo.tokenId);
        if (amount == 0) {
            assertNull(tokenBalance);
        } else {
            assertEquals(amount, tokenBalance.intValue());
        }
    }

    @Then("The first account holds {int} HTT tokens")
    public void assertFirstAccountTokenBalance(int expectedBalance) {
        final AccountBalance accountBalance = accountClient.retrieveBalance(accounts.get("first"));
        final Long balance = accountBalance.tokens.get(tokenInfo.tokenId);
        assertEquals(expectedBalance, balance.intValue());
    }

    @Then("The first account has paid for the transaction fee")
    public void theFirstAccountHasPaidForTheTransactionFee() {
        final AccountBalance accountBalance = accountClient.retrieveBalance(accounts.get("first"));
        Assert.assertTrue(accountBalance.hbars.getValue().compareTo(new BigDecimal("100")) < 0);
    }

    @When("I create a token named {string} \\({string})")
    @SneakyThrows
    public void iCreateATokenNamedTestTokenHTT(final String name, final String symbol) {
        final TokenId tokenId = tokenClient.createToken(accounts.get("first"),
                TestDataUtils.createTokenModel(name, symbol, 1000L, null, TokenSupplyType.INFINITE));
        this.tokenInfo = tokenClient.retrieveToken(accounts.get("first"), tokenId);
    }

    @Then("The token has the name {string}")
    public void theTokenHasTheName(String tokenName) {
        assertEquals(tokenName, tokenInfo.name);
    }

    @And("The token has the symbol {string}")
    public void theTokenHasTheSymbol(String symbol) {
        assertEquals(symbol, tokenInfo.symbol);
    }

    @And("The token has {int} decimals")
    public void theTokenHasDecimals(int decimals) {
        assertEquals(decimals, tokenInfo.decimals);
    }

    @And("The token is owned by the account")
    public void theTokenIsOwnedByTheAccount() {
        assertEquals(accounts.get("first").accountId(), tokenInfo.treasuryAccountId);
    }

    @And("An attempt to mint {int} additional tokens succeeds")
    public void anAttemptToMintAdditionalTokensSucceeds(int mintingAmount) {
        tokenClient.mintTokens(accounts.get("first"), tokenInfo.tokenId, mintingAmount);
        tokenInfo = tokenClient.retrieveToken(accounts.get("first"), tokenInfo.tokenId);
        assertEquals(1000 + mintingAmount, tokenInfo.totalSupply);
    }

    @When("I create a fixed supply token named {string} \\({string}) with {int} tokens")
    public void fixedSupplyToken(
            final String name,
            final String symbol,
            final int initialSupply) {
        final CreateTokenModel token
                = TestDataUtils.createTokenModel(name, symbol,
                (long) initialSupply, (long) initialSupply, TokenSupplyType.FINITE);


        final TokenId tokenId = tokenClient.createToken(accounts.get("first"), token);
        this.tokenInfo = tokenClient.retrieveToken(accounts.get("first"), tokenId);
    }

    @Then("The total supply of the token is {int}")
    public void theTotalSupplyOfTheTokenIs(int totalSup) {
        assertEquals(totalSup, tokenInfo.totalSupply);
    }

    @And("An attempt to mint tokens fails")
    public void anAttemptToMintTokensFails() {
        assertThrows(ReceiptStatusException.class,
                () -> tokenClient.mintTokens(accounts.get("first"), tokenInfo.tokenId, 1));
    }

    @And("A token named {string} \\({string}) with {string} tokens")
    public void aTokenNamedWithTokens(String name, String symbol, String supply) {
        final CreateTokenModel token = CreateTokenModel.builder()
                .tokenName(name)
                .tokenSymbol(symbol)
                .decimals(2)
                .initialSupply(Long.parseLong(supply))
                .maxSupply(Long.parseLong(supply))
                .supplyType(TokenSupplyType.FINITE)
                .build();

        final TokenId tokenId = tokenClient.createToken(rootAccount, token);
        this.tokenInfo = tokenClient.retrieveToken(rootAccount, tokenId);
    }

    @Given("Given the first account holds {int} HTT tokens")
    public void givenTheFirstAccountHoldsHTTTokens(int amount) {
        tokenClient.associateAccount(rootAccount, accounts.get("first"), tokenInfo.tokenId);
        if (amount > 0) {
            tokenClient.transferTokens(rootAccount, accounts.get("first").accountId(), tokenInfo.tokenId, amount);
        }
    }

    @Given("Given the second account holds {int} HTT tokens")
    public void givenTheSecondAccountHoldsHTTTokens(int amount) {
        tokenClient.associateAccount(rootAccount, accounts.get("second"), tokenInfo.tokenId);
        if (amount > 0) {
            tokenClient.transferTokens(rootAccount, accounts.get("second").accountId(), tokenInfo.tokenId, amount);
        }
    }

    @When("The first account creates a transaction to transfer {int} HTT tokens to the second account")
    public void theFirstAccountCreatesATransactionToTransferHTTTokensToTheSecondAccount(int amount) {
        final HederaAccount fromAccount = accounts.get("first");
        final HederaAccount toAccount = accounts.get("second");

        transferTransaction =
                new TransferTransaction()
                        .addTokenTransfer(tokenInfo.tokenId, fromAccount.accountId(), -amount)
                        .addTokenTransfer(tokenInfo.tokenId, toAccount.accountId(), amount)
                        .freezeWith(fromAccount.client())
                        .sign(fromAccount.accountPrivateKey());

    }

    @And("The first account submits the transaction")
    public void theFirstAccountSubmitsTheTransaction() throws Exception {
        final HederaAccount firstAccount = accounts.get("first");

        transferTransaction
                .execute(firstAccount.client())
                .getReceipt(firstAccount.client());
    }

    @When("The second account creates a transaction to transfer {int} HTT tokens to the first account")
    public void theSecondAccountCreatesATransactionToTransferHTTTokensToTheFirstAccount(int amount) {
        final HederaAccount fromAccount = accounts.get("second");
        final HederaAccount toAccount = accounts.get("first");

        this.transferTransaction =
                new TransferTransaction()
                        .addTokenTransfer(tokenInfo.tokenId, fromAccount.accountId(), -amount)
                        .addTokenTransfer(tokenInfo.tokenId, toAccount.accountId(), amount)
                        .freezeWith(toAccount.client())
                        .sign(fromAccount.accountPrivateKey());
    }

    @And("A first hedera account with more than {int} hbar and {int} HTT tokens")
    public void aFirstHederaAccountWithMoreThanHbarAndHTTTokens(int hbarAmount, int httAmount) {
        accounts.put("first", createAccountWithBalance(hbarAmount, httAmount));
    }

    @And("A {string} Hedera account with {int} hbar and {int} HTT tokens")
    public void aHederaAccountWithHbarAndHTTTokens(String accountName, int hbarAmount, int httAmount) {
        accounts.put(accountName, createAccountWithBalance(hbarAmount, httAmount));
    }

    private HederaAccount createAccountWithBalance(final int hbarAmount, final int httAmount) {
        final HederaAccount account = accountClient.createAccount(rootAccount, hbarAmount);
        tokenClient.associateAccount(rootAccount, account, tokenInfo.tokenId);
        tokenClient.transferTokens(rootAccount, account.accountId(), tokenInfo.tokenId, httAmount);
        return account;
    }

    @When("A transaction is created to transfer {int} HTT tokens out of the first and second account and {int} HTT tokens "
            + "into the third account and {int} HTT tokens into the fourth account")
    public void multipleAccountsTransaction(int fromFirstAndSecond, int toThird, int toForth) {
        final HederaAccount firstAccount = accounts.get("first");
        final HederaAccount secondAccount = accounts.get("second");
        final HederaAccount thirdAccount = accounts.get("third");
        final HederaAccount forthAccount = accounts.get("forth");

        transferTransaction =
                new TransferTransaction()
                        .addTokenTransfer(tokenInfo.tokenId, firstAccount.accountId(), -fromFirstAndSecond)
                        .addTokenTransfer(tokenInfo.tokenId, secondAccount.accountId(), -fromFirstAndSecond)
                        .addTokenTransfer(tokenInfo.tokenId, thirdAccount.accountId(), toThird)
                        .addTokenTransfer(tokenInfo.tokenId, forthAccount.accountId(), toForth)
                        .freezeWith(firstAccount.client())
                        .sign(firstAccount.accountPrivateKey())
                        .sign(secondAccount.accountPrivateKey());
    }

    @And("The third account holds {int} HTT tokens")
    public void theThirdAccountHoldsHTTTokens(int expectedBalance) {
        final HederaAccount thirdAccount = accounts.get("third");
        final AccountBalance accountBalance = accountClient.retrieveBalance(thirdAccount);
        final Long balance = accountBalance.tokens.get(tokenInfo.tokenId);
        assertEquals(expectedBalance, balance.intValue());
    }

    @And("The fourth account holds {int} HTT tokens")
    public void theFourthAccountHoldsHTTTokens(int expectedBalance) {
        final HederaAccount forthAccount = accounts.get("forth");
        final AccountBalance accountBalance = accountClient.retrieveBalance(forthAccount);
        final Long balance = accountBalance.tokens.get(tokenInfo.tokenId);
        assertEquals(expectedBalance, balance.intValue());
    }
}

