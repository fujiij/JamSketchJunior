package jp.jamsketch.model

import java.util.concurrent.CopyOnWriteArrayList

class Curve implements Runnable{

    protected final HashMap<Integer, CurveData> curveData = new HashMap<>()
    private CopyOnWriteArrayList<ICurveContainer> curveContainers = new CopyOnWriteArrayList<>();
    private boolean isEnd = false;

    public Curve(){
        Thread t = new Thread(this::run);
        t.start();
    }

    void addContainer(ICurveContainer controller){
        curveContainers.add(controller);

    }

    void setCurveData(){
        for(ICurveContainer c : curveContainers)
            c.initCurveData(curveData);
    }

    void clear(){
        for(ICurveContainer c : curveContainers)
            c.removeAll(curveData);
    }

    void updateCurve(Point p){
        for(ICurveContainer c : curveContainers)
            c.updateCurve(p, curveData);
    }

    ArrayList<ArrayList<Point>> getCurves(int id){
        return curveData.get(id).getAll();
    }

    @Override
    void run() {
        while(!isEnd) {
            for (ICurveContainer c : curveContainers)
                c.tick(curveData);
            try {
                Thread.sleep(10);
            }catch (Exception e){
                println "OWATA";
            }
        }
    }
}
