package jp.jamsketch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Session;


import jp.jamsketch.web.WebSocketApi;
import jp.jamsketch.web.ServerParameter;
import jp.jamsketch.web.ServiceLocator;

import org.glassfish.tyrus.server.Server;

public class JamSketchServerController implements IJamSketchController{
    Server server
    IJamSketchController controller;

    public JamSketchServerController(String host, int port, IJamSketchController controller){
        this.controller = controller;
        server = new Server(host, port, "/websockets", null, WebSocketApi.class);
        server.start();
    }

    @Override
    public void updateCurve(int from, int thru, int y){
      this.controller.updateCurve(from, thru, y);
    }

    @Override
    public void reset(){
        this.controller.reset();
        def serviceLocator = ServiceLocator.GetInstance();
    	def currentSession = serviceLocator.getSession();
        Set<Session> sessions = currentSession.getOpenSessions();
		for (Session session : sessions) {
			try {
				session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(new ServerParameter("reset")));
                System.out.println(new ObjectMapper().writeValueAsString(new ServerParameter("reset")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}