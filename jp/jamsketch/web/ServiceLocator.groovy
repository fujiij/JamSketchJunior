package jp.jamsketch.web;

import jakarta.websocket.Session;

import jp.jamsketch.controller.IJamSketchController;

public class ServiceLocator{
    private static final ServiceLocator instance = new ServiceLocator();
    private IJamSketchController controller;
    private Session session;

    
    public IJamSketchController getContoller(){
        return controller;
    }

    public IJamSketchController setContoller(IJamSketchController controller){
        this.controller = controller;
    }

    public Session getSession(){
        return session;
    }

    public Session setSession(Session session){
        this.session = session;
    }

    private ServiceLocator(){

    }

    public static ServiceLocator GetInstance(){
        return instance;
    }
}