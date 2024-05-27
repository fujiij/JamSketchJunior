package jp.jamsketch.web;

import java.io.IOException;
import java.util.Set;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Websocket Endpoint implementation class HelloEndPoint
 */

@ServerEndpoint("/WebSocketApi")
class WebSocketApi {
	// 現在のセッションを記録
    
	Session currentSession = null;
	
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig ec) {
		this.currentSession = session;
		def serviceLocator = ServiceLocator.GetInstance();
    	serviceLocator.setSession(this.currentSession);
	}
	/* メッセージを受信したとき */
	@OnMessage
	public void receiveMessage(String msg) throws IOException {
		System.out.println(msg);
		
		try{
			def serviceLocator = ServiceLocator.GetInstance();
			def controller = serviceLocator.getContoller();
			ObjectMapper mapper = new ObjectMapper();
			ClientParameter info = mapper.readValue(msg, ClientParameter.class);
		
        	controller.updateCurve(info.from, info.thru, info.y);
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	/* 接続がクローズしたとき */
	@OnClose
	public void onClose(Session session, CloseReason reason) {
	}
	/* 接続エラーが発生したとき */
	@OnError
	public void onError(Throwable t) {
	}
}