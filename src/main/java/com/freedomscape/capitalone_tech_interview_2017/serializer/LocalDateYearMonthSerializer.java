package com.freedomscape.capitalone_tech_interview_2017.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Special Jackson serializer to only show the year and month in the JSON when
 * the string that shows the monthly spend/income is displayed.
 * 
 * @author lsli
 *
 */
public class LocalDateYearMonthSerializer extends JsonSerializer<LocalDate> {
	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		if (value.equals(LocalDate.MAX)) {
			// This is the special case where in the linked hash map, if the
			// date is the maximum local date, we know this entry has data for
			// the average monthly spend/income
			gen.writeFieldName("average");
		} else {
			gen.writeFieldName(value.format(DateTimeFormatter.ofPattern("yyyy-MM")));
		}
	}

}
