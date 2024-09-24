package jp.jamsketch.model


class JamSketchModel {
    private final CurveData curveData;

    public JamSketchModel(){
        curveData = new CurveData();
    }

    void updateCurve(Point p){
        curveData.add(p);

    }

    ArrayList<ArrayList<Point>> getCurves(){
        return curveData.getAll();
    }
}
