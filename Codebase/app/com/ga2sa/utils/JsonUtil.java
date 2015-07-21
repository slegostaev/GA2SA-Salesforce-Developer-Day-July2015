package com.ga2sa.utils;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {
	
	public static JsonNode excludeFields(JsonNode source, Collection<String> fields) {
		
		JsonNode result = null;
		
		if (source.isArray()) {
			result = new ObjectMapper(new JsonFactory()).createArrayNode();
			for (JsonNode node : source) {
				((ArrayNode)result).add(((ObjectNode)node).without(fields));
			}
		} else {
			result = ((ObjectNode)source).without(fields);
		}
		
		return result;
	}
}
