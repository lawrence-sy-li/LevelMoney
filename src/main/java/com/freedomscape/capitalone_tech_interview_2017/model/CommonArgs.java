package com.freedomscape.capitalone_tech_interview_2017.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonArgs {
	@JsonProperty("uid")
	private long userId;

	@JsonProperty("token")
	private String authenticationToken;

	@JsonProperty("api-token")
	private String apiToken;

	@JsonProperty("json-strict-mode")
	private boolean jsonStrictMode;

	@JsonProperty("json-verbose-response")
	private boolean jsonVerboseResponse;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public boolean isJsonStrictMode() {
		return jsonStrictMode;
	}

	public void setJsonStrictMode(boolean jsonStrictMode) {
		this.jsonStrictMode = jsonStrictMode;
	}

	public boolean isJsonVerboseResponse() {
		return jsonVerboseResponse;
	}

	public void setJsonVerboseResponse(boolean jsonVerboseResponse) {
		this.jsonVerboseResponse = jsonVerboseResponse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiToken == null) ? 0 : apiToken.hashCode());
		result = prime * result + ((authenticationToken == null) ? 0 : authenticationToken.hashCode());
		result = prime * result + (jsonStrictMode ? 1231 : 1237);
		result = prime * result + (jsonVerboseResponse ? 1231 : 1237);
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		CommonArgs other = (CommonArgs) obj;
		if (apiToken == null) {
			if (other.apiToken != null)
				return false;
		} else if (!apiToken.equals(other.apiToken))
			return false;
		if (authenticationToken == null) {
			if (other.authenticationToken != null)
				return false;
		} else if (!authenticationToken.equals(other.authenticationToken))
			return false;
		if (jsonStrictMode != other.jsonStrictMode)
			return false;
		if (jsonVerboseResponse != other.jsonVerboseResponse)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CommonArgs [userId=" + userId + ", authenticationToken=" + authenticationToken + ", apiToken="
				+ apiToken + ", jsonStrictMode=" + jsonStrictMode + ", jsonVerboseResponse=" + jsonVerboseResponse
				+ "]";
	}
}
