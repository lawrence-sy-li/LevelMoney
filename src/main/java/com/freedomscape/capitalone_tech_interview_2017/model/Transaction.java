package com.freedomscape.capitalone_tech_interview_2017.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
	@JsonProperty("transaction-id")
	private String transactionId;

	@JsonProperty("account-id")
	private String accountId;

	@JsonProperty("raw-merchant")
	private String rawMercant;

	@JsonProperty("is-pending")
	private boolean isPending;

	@JsonProperty("transaction-time")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime transactionTime;

	@JsonProperty("amount")
	private long amount;

	@JsonProperty("previous-transaction-id")
	private String previousTransactionId;

	@JsonProperty("categorization")
	private String categorization;

	@JsonProperty("memo-only-for-testing")
	private String memoOnlyForTesting;

	@JsonProperty("payee-name-only-for-testing")
	private String payeeNameOnlyForTesting;

	@JsonProperty("clear-date")
	private long clearDate;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getRawMercant() {
		return rawMercant;
	}

	public void setRawMercant(String rawMercant) {
		this.rawMercant = rawMercant;
	}

	public boolean isPending() {
		return isPending;
	}

	public void setPending(boolean isPending) {
		this.isPending = isPending;
	}

	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getPreviousTransactionId() {
		return previousTransactionId;
	}

	public void setPreviousTransactionId(String previousTransactionId) {
		this.previousTransactionId = previousTransactionId;
	}

	public String getCategorization() {
		return categorization;
	}

	public void setCategorization(String categorization) {
		this.categorization = categorization;
	}

	public String getMemoOnlyForTesting() {
		return memoOnlyForTesting;
	}

	public void setMemoOnlyForTesting(String memoOnlyForTesting) {
		this.memoOnlyForTesting = memoOnlyForTesting;
	}

	public String getPayeeNameOnlyForTesting() {
		return payeeNameOnlyForTesting;
	}

	public void setPayeeNameOnlyForTesting(String payeeNameOnlyForTesting) {
		this.payeeNameOnlyForTesting = payeeNameOnlyForTesting;
	}

	public long getClearDate() {
		return clearDate;
	}

	public void setClearDate(long clearDate) {
		this.clearDate = clearDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result + (int) (amount ^ (amount >>> 32));
		result = prime * result + ((categorization == null) ? 0 : categorization.hashCode());
		result = prime * result + (int) (clearDate ^ (clearDate >>> 32));
		result = prime * result + (isPending ? 1231 : 1237);
		result = prime * result + ((memoOnlyForTesting == null) ? 0 : memoOnlyForTesting.hashCode());
		result = prime * result + ((payeeNameOnlyForTesting == null) ? 0 : payeeNameOnlyForTesting.hashCode());
		result = prime * result + ((previousTransactionId == null) ? 0 : previousTransactionId.hashCode());
		result = prime * result + ((rawMercant == null) ? 0 : rawMercant.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result + ((transactionTime == null) ? 0 : transactionTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (amount != other.amount)
			return false;
		if (categorization == null) {
			if (other.categorization != null)
				return false;
		} else if (!categorization.equals(other.categorization))
			return false;
		if (clearDate != other.clearDate)
			return false;
		if (isPending != other.isPending)
			return false;
		if (memoOnlyForTesting == null) {
			if (other.memoOnlyForTesting != null)
				return false;
		} else if (!memoOnlyForTesting.equals(other.memoOnlyForTesting))
			return false;
		if (payeeNameOnlyForTesting == null) {
			if (other.payeeNameOnlyForTesting != null)
				return false;
		} else if (!payeeNameOnlyForTesting.equals(other.payeeNameOnlyForTesting))
			return false;
		if (previousTransactionId == null) {
			if (other.previousTransactionId != null)
				return false;
		} else if (!previousTransactionId.equals(other.previousTransactionId))
			return false;
		if (rawMercant == null) {
			if (other.rawMercant != null)
				return false;
		} else if (!rawMercant.equals(other.rawMercant))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		if (transactionTime == null) {
			if (other.transactionTime != null)
				return false;
		} else if (!transactionTime.equals(other.transactionTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", accountId=" + accountId + ", rawMercant=" + rawMercant
				+ ", isPending=" + isPending + ", transactionTime=" + transactionTime + ", amount=" + amount
				+ ", previousTransactionId=" + previousTransactionId + ", categorization=" + categorization
				+ ", memoOnlyForTesting=" + memoOnlyForTesting + ", payeeNameOnlyForTesting=" + payeeNameOnlyForTesting
				+ ", clearDate=" + clearDate + "]";
	}
}
