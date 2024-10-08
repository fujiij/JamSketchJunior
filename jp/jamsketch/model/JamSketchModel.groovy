package jp.jamsketch.model

import jp.jamsketch.model.engine.Engine
import jp.jamsketch.model.engine.EngineRegistries
import jp.jamsketch.util.Tick


class JamSketchModel implements Tick{
    public Curve curve;
    public Engine engine;

    public JamSketchModel(){
        curve = new Curve(this::generateMelody)
        engine = EngineRegistries.LITE_JAM_SKETCH_ENGINE.get();
    }

    @Override
    void tick() {

    }
    void generateMelody(CurveData data){

    }
}
