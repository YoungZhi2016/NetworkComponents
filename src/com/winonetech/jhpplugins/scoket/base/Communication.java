package com.winonetech.jhpplugins.scoket.base;

import com.fasterxml.jackson.databind.JsonNode;

public interface Communication {

	/**
	 * @param client
	 *            add client
	 */
	void add(ComunicationClient client);

	/**
	 * @param client
	 *            remote
	 */
	void remote(ComunicationClient client);

	/**
	 * @param client
	 * @param message
	 */
	void handlerMessage(ComunicationClient client, JsonNode message);
}
