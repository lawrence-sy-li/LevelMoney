package com.freedomscape.capitalone_tech_interview_2017.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the JSON request object (string) that we send in the POST request
 * to the GetAllTransaction RESTful service to get the transaction information.
 * 
 * @author lsli
 *
 */
public class GetAllTransactionsRequest {
	@JsonProperty("args")
	CommonArgs commonArgs;

	public CommonArgs getCommonArgs() {
		return commonArgs;
	}

	public void setCommonArgs(CommonArgs commonArgs) {
		this.commonArgs = commonArgs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commonArgs == null) ? 0 : commonArgs.hashCode());
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
		GetAllTransactionsRequest other = (GetAllTransactionsRequest) obj;
		if (commonArgs == null) {
			if (other.commonArgs != null)
				return false;
		} else if (!commonArgs.equals(other.commonArgs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GetAllTransactionsRequest [commonArgs=" + commonArgs + "]";
	}

}
