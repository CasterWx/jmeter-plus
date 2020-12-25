package org.apache.jmeter.protocol.dubbo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * JsonUtils
 */
public class JsonUtils {

	private static final Logger logger = LoggingManager.getLoggerForClass();

	private static final Gson gson = new GsonBuilder()
			.setDateFormat(Constants.DATE_FORMAT)
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.serializeNulls()
			.registerTypeAdapter(Locale.class, (JsonDeserializer<Locale>) (json, typeOfT, context) -> {
				return ClassUtils.parseLocale(json.getAsJsonPrimitive().getAsString());
			})
			.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
				return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
			})
			.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
				LocalDateTime localDateTime = LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
				return localDateTime.toLocalDate();
			})
			.registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> {
				LocalDateTime localDateTime = LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
				return localDateTime.toLocalTime();
			})
			.create();

	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public static String toJson(Object obj, Type type) {
		return gson.toJson(obj, type);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			logger.error("json to class[" + classOfT.getName() + "] is error!",
					e);
		}
		return null;
	}

	public static <T> T fromJson(String json, Type type) {
		try {
			return gson.fromJson(json, type);
		} catch (JsonSyntaxException e) {
			logger.error("json to class[" + type.getClass().getName()
					+ "] is error!", e);
		}
		return null;
	}
}
