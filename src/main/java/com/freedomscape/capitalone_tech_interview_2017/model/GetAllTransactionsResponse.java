package com.freedomscape.capitalone_tech_interview_2017.model;

import java.util.List;

/**
 * Represents the JSON response object (string) that we get back from calling
 * the GetAllTransaction RESTful service. Corresponds to the
 * GetAllTransactionsResponse response object that is returned from calling the
 * GetAllTransaction RESTful service.
 * 
 * @author lsli
 *
 */
public class GetAllTransactionsResponse {
	private String error;
	private List<Transaction> transactions;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((transactions == null) ? 0 : transactions.hashCode());
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
		GetAllTransactionsResponse other = (GetAllTransactionsResponse) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (transactions == null) {
			if (other.transactions != null)
				return false;
		} else if (!transactions.equals(other.transactions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GetAllTransactionsResponse [error=" + error + ", transactions=" + transactions + "]";
	}
}
