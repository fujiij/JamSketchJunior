package jp.jamsketch.controller;

import java.util.function.Supplier;

public class JamSketchController implements IJamSketchController{
    private MelodyData2 melodyData;
    private Supplier<MelodyData2> initData;
    
    public JamSketchController(MelodyData2 melodyData, Supplier<MelodyData2> initData){
        this.melodyData = melodyData;
        this.initData = initData;
    }

    @Override
    public void updateCurve(int from, int thru, int y){
        this.melodyData.storeCursorPosition(from, thru, y)
        this.melodyData.updateCurve(from, thru);
    }

    @Override
    public void reset(){
        this.melodyData = this.initData.get();
    }
}