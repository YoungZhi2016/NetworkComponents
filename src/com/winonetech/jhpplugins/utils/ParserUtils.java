package com.winonetech.jhpplugins.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.winone.ftc.mtools.Log;

/**
 * @author ywkj
 *
 *         解析
 */
public class ParserUtils {

	private static ObjectMapper MAPPER;

	static {
		MAPPER = new ObjectMapper();
	}

	/**
	 * 获取JSON JsonNode
	 * 
	 * @param scheduleFilePath
	 *            JSON文件路径
	 * @return JsonNode
	 */
	public static JsonNode getJsonNodeByFilePath(String scheduleFilePath) {
		return getJsonNodeByFile(new File(scheduleFilePath));
	}

	/**
	 * @param scheduleFile
	 *            JSON文件
	 * @return JsonNode
	 */
	public static JsonNode getJsonNodeByFile(File scheduleFile) {
		JsonNode jsonNode = null;
		try {
			jsonNode = MAPPER.readTree(scheduleFile);
		} catch (IOException e) {
			Log.e("解析失败" + e.getMessage());
		}
		return jsonNode;
	}

	public static JsonNode getJsonNodeByFileWithUTF8(File scheduleFile) throws IOException {
		JsonNode jsonNode = null;
		FileInputStream inputStream = new FileInputStream(scheduleFile);
		// InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
		// BufferedReader br = new BufferedReader(isr);
		jsonNode = MAPPER.readTree(inputStream);

		return jsonNode;
	}

	/**
	 * @param jsonString
	 *            jsonString
	 * @return
	 */
	public static JsonNode getJsonNedeByJsonString(String jsonString) {
		JsonNode jsonNode = null;
		try {
			jsonNode = MAPPER.readTree(jsonString);
		} catch (IOException e) {
			Log.e("解析失败" + e.getMessage());
		}
		return jsonNode;
	}

	public static JsonNode getByJsonString(String jsonString) throws JsonProcessingException, IOException {
		return MAPPER.readTree(jsonString);
	}

	/**
	 * @return new ObjectNode
	 */
	public static ObjectNode getObjectNode() {
		return MAPPER.createObjectNode();
	}

	/**
	 * @return new ArrayNode
	 */
	public static ArrayNode getArrayNode() {
		return MAPPER.createArrayNode();
	}

	/**
	 * ArrayNode
	 */
	public static ArrayNode getArrayNode(String contents, String split) {
		StringTokenizer tokenizer = new StringTokenizer(contents, split);
		ArrayNode arrayNode = getArrayNode();
		while (tokenizer.hasMoreTokens()) {
			arrayNode.add(Integer.parseInt(tokenizer.nextToken()));
		}
		return arrayNode;
	}
}
