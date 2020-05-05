package cn.br.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class LocalDateAsJson implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
	private static final String PATTERN = "yyyy-MM-dd";

	private final DateTimeFormatter formatter;

	private LocalDateAsJson(final DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	public static LocalDateAsJson withDefault() {
		return of(PATTERN);
	}

	public static LocalDateAsJson of(final String pattern) {
		return new LocalDateAsJson(DateTimeFormatter.ofPattern(pattern));
	}

	@Override
	public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return LocalDate.from(formatter.parse(jsonElement.toString()));
	}

	@Override
	public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(formatter.format(date));
	}
}