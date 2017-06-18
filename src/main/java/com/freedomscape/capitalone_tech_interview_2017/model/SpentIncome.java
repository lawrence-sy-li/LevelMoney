package com.freedomscape.capitalone_tech_interview_2017.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.freedomscape.capitalone_tech_interview_2017.serializer.SpentIncomeCurrencySerializer;

@JsonSerialize(using = SpentIncomeCurrencySerializer.class)
public class SpentIncome {
	private long spent;
	private long income;

	public SpentIncome(long spent, long income) {
		super();
		this.spent = spent;
		this.income = income;
	}

	public long getSpent() {
		return spent;
	}

	public void setSpent(long spent) {
		this.spent = spent;
	}

	public long getIncome() {
		return income;
	}

	public void setIncome(long income) {
		this.income = income;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (income ^ (income >>> 32));
		result = prime * result + (int) (spent ^ (spent >>> 32));
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
		SpentIncome other = (SpentIncome) obj;
		if (income != other.income)
			return false;
		if (spent != other.spent)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SpentIncome [spent=" + spent + ", income=" + income + "]";
	}

}
