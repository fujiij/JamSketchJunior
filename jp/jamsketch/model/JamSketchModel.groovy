package jp.jamsketch.model


class JamSketchModel {
    private final CurveData curveData;
    private final CurveData margeData;

    public JamSketchModel(){
        curveData = new CurveData();
        margeData = new CurveData();
    }

    void updateCurve(Point p){
        if(p.x >= curveData.getWidth())
            curveData.add(p);
        else
            margeData.add(p);
    }

    ArrayList<ArrayList<Point>> getCurves(){
        return curveData.getAll();
    }
    /**
     * 仮実装
     */
    ArrayList<ArrayList<Point>> getMargeData(){
        return margeData.getAll();
    }

    void mouseReleased(){
        if(!margeData.isEmpty())
            curveData.marge(margeData);
    }
}
