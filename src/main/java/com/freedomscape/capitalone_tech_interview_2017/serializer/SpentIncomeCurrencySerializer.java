package com.freedomscape.capitalone_tech_interview_2017.serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.freedomscape.capitalone_tech_interview_2017.model.SpentIncome;

/**
 * A Jackson serializer that specially generates the spent and income amounts in
 * JSON like strings, always adjusting for two decimal places, dividing by 10000
 * because of the way the transaction amounts are in centocents, and using a
 * default number format that uses a dollar sign for now.
 * 
 * @author lsli
 *
 */
public class SpentIncomeCurrencySerializer extends JsonSerializer<SpentIncome> {
	// Use dollar sign
	// TODO: May want to parameterize this somehow in the future
	private static NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
	static {
		// Don't want to display commas as group separators
		numberFormat.setGroupingUsed(false);
	}

	private static BigDecimal CENTOS_CENTS_FACTOR = new BigDecimal(10000);

	@Override
	public void serialize(SpentIncome value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();

		BigDecimal spentCentos = new BigDecimal(value.getSpent());
		BigDecimal spentDollars = spentCentos.divide(CENTOS_CENTS_FACTOR);
		spentDollars.setScale(2, RoundingMode.HALF_UP);
		gen.writeStringField("spent", numberFormat.format(spentDollars));
		BigDecimal incomeCentos = new BigDecimal(value.getIncome());
		BigDecimal incomeDollars = incomeCentos.divide(CENTOS_CENTS_FACTOR);
		incomeDollars.setScale(2, RoundingMode.HALF_UP);

		gen.writeStringField("income", numberFormat.format(incomeDollars));
		gen.writeEndObject();
	}

}
