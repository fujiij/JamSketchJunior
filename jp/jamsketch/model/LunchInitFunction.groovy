package jp.jamsketch.model

import jp.jamsketch.main.JamSketch
import jp.jamsketch.view.DisplayCurve

import java.awt.Color

class LunchInitFunction {
    private static LunchInitFunction INSTANCE;
    private JamSketch main;

    public LunchInitFunction(JamSketch sketch){
        main = sketch;
    }

    public void setup(){
        loadModel();
        loadView();
    }

    protected void loadView(){
        main.displays.add(new DisplayCurve(()->main.model.curve.getCurves(JamSketchCurve.PLAYER_MAIN), new Color(0, 0, 255)));
        main.displays.add(new DisplayCurve(()->main.model.curve.getCurves(JamSketchCurve.PLAYER_MARGE), new Color(255, 0, 0, 128)));
    }

    protected void loadModel(){
        JamSketchModel model = new JamSketchModel();
        model.curve.addContainer(new JamSketchCurve(main.controller));
        main.model = model;
        main.ticker.add(model::tick)

        model.curve.setCurveData();
    }

    public static LunchInitFunction create(JamSketch sketch){
        if(INSTANCE != null)
            return INSTANCE
        else {
            INSTANCE = new LunchInitFunction(sketch);
            return INSTANCE;
        }
    }
    public static LunchInitFunction get(){
        return INSTANCE;
    }
}
