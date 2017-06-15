package com.winonetech.jhpplugins.scoket.base;

public interface ComunicationClient {

	long clientId();

	void setServer(Communication server);

	void startMonite();

	void stopMonite();

	void sendMessage(String message);

	void handlerMessage(String result);

	void setAccess(boolean access);

	boolean isAccess();

	boolean isMonite();
}
