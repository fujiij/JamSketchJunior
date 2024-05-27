package jp.jamsketch.controller;

import java.net.URI;
import java.util.Scanner;

import javax.websocket.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.jamsketch.web.WebSocketClient;
import jp.jamsketch.web.ClientParameter;

public class JamSketchClientController implements IJamSketchController{
    IJamSketchController controller;
    WebSocketClient webSocketClient;

    public JamSketchClientController(String host, int port, IJamSketchController controller){
      this.controller = controller;
      webSocketClient = new WebSocketClient();        
      webSocketClient.Init(host, port);
    }

    @Override
    public void updateCurve(int from, int thru, int y){
      this.controller.updateCurve(from, thru, y);
      this.webSocketClient.Send(new ClientParameter(from, thru, y));
    }

    @Override
    public void reset(){
        this.controller.reset();
    }
}