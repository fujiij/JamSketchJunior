package jp.jamsketch.web;

import jakarta.websocket.*;

import com.fasterxml.jackson.databind.ObjectMapper;

// このアノテーションを付けるとクライアントのEndpointだと認識される
@ClientEndpoint
class WebSocketClient {

    Session session;

    public WebSocketClinet() {
        super();
    }

    // セッション確立時の処理
    @OnOpen
    public void onOpen(Session session) {
    }

    // メッセージ受信時の処理
    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
		
		try{
			def serviceLocator = ServiceLocator.GetInstance();
			def controller = serviceLocator.getContoller();
			ObjectMapper mapper = new ObjectMapper();
			ServerParameter info = mapper.readValue(message, ServerParameter.class);
		
        	if (info.mode == "reset"){
                controller.reset();
            }
		}
		catch (Exception e){
			System.out.println(e);
		}
    }

    // メッセージ受信時の処理
    @OnError
    public void onError(Throwable th) {
    }

    // セッション解放時の処理
    @OnClose
    public void onClose(Session session) {
    }

    public void Send(Object object) {
        this.session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(object));
    }

    public void Init(String host, int port) {
      // 初期化のため WebSocket コンテナのオブジェクトを取得する
      WebSocketContainer container = ContainerProvider
              .getWebSocketContainer();
      // サーバー・エンドポイントの URI
      URI uri = URI
              .create("ws://" + host + ":" + port + "/websockets/WebSocketApi");
      this.session = container.connectToServer(this, uri);
    }
}