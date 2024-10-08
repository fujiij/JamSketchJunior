package jp.jamsketch.model

import jp.jamsketch.controller.IJamSketchController
import jp.jamsketch.controller.JamMouseListener
import jp.jamsketch.controller.JamSketchController
import jp.jamsketch.main.JamSketch;

public class JamSketchCurve implements ICurveContainer, JamMouseListener{
    public static final int PLAYER_MAIN = 0;
    public static final int PLAYER_MARGE = 1;

    private boolean isReleased = false;
    public JamSketchCurve(IJamSketchController controller){
        controller.addListener(this);
    }

    @Override
    void initCurveData(HashMap<Integer, CurveData> curves) {
        curves.put(PLAYER_MAIN, new CurveData())
        curves.put(PLAYER_MARGE, new CurveData())
    }

    @Override
    void updateCurve(Point p, HashMap<Integer, CurveData> curves) {
        if(p.x >= curves.get(PLAYER_MAIN).getWidth())
            curves.get(PLAYER_MAIN).add(p);
        else
            curves.get(PLAYER_MARGE).add(p);
    }

    @Override
    void removeAll(HashMap<Integer, CurveData> curves) {
        curves.get(PLAYER_MAIN).removeAll();
    }

    @Override
    void tick(HashMap<Integer, CurveData> curves) {

        if(isReleased) {
            isReleased = false;
            if (!curves.get(PLAYER_MARGE).isEmpty())
                curves.get(PLAYER_MAIN).marge(curves.get(PLAYER_MARGE))
        }
    }

    @Override
    void mouseReleased(Point p) {
        isReleased = true;
    }
}
