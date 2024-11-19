package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSON {
	static final ObjectMapper mapper = new ObjectMapper();

	public static final synchronized String encode(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static final synchronized <T> T decode(String json, Class<T> classOf) {
		try {
			var res = mapper.readValue(json, classOf);
			return res;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final synchronized <T> T decode(String json, TypeReference<T> typeOf) {
		try {
			var res = mapper.readValue(json, typeOf);
			return res;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
