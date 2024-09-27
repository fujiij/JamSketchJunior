package jp.jamsketch.model

import java.util.concurrent.CopyOnWriteArrayList;



public class CurveData {
    final CopyOnWriteArrayList<Point> curves = new ArrayList<>();
    public final int SPLIT_DISTANCE = 50;
    public final int CALCULATION_ERROR = 10;
    public CurveData(){}

    public void add(Point p){
        Point target = get(p.x);
        if(target == null){
            curves.add(p);
        }else{
            curves.remove(target);
            curves.add(p);
        }
        curves.sort(Comparator.comparingInt (o -> o.x));
    }

    public ArrayList<ArrayList<Point>> getAll(){

        ArrayList<ArrayList<Point>> pointers = new ArrayList<>();
        ArrayList<Point> split_curve = new ArrayList<>();
        Point p0 = null
        for(Point p : curves){
            if(p0 == null){
                p0 = p;
                split_curve.add(p);
            }else{
                if(Math.abs(p0.x - p.x) <= SPLIT_DISTANCE){
                    split_curve.add(p);
                    p0 = p;
                }else{
                    pointers.add(split_curve);
                    split_curve = new ArrayList<>();
                    p0 = null;
                }
            }
        }
        if(!split_curve.isEmpty())
            pointers.add(split_curve);
        return pointers;
    }


    public Point get(int x){
        for(Point p : curves)
            if(p.x == x)
                return p;


        return null;
    }

    public int getWidth(){
        if(curves.isEmpty())
            return 0;
        else
            return curves.max(Comparator.comparingInt (o -> o.x)).x;
    }

    public boolean isEmpty(){
        return curves.isEmpty()
    }

    public void removeAll(){
        curves.clear();
    }

    public void remove(int x0, x1){
        for(Point p : curves){
            if(p.x >= x0 && p.x <= x1){
                curves.remove(p);
            }
        }
    }

    void marge(CurveData margeData){
        ArrayList<Point> m = margeData.getAll().get(0);
        remove(m.get(0).x, m.get(m.size() - 1).x);

        for(Point p : m){
            add(p);
        }
        margeData.removeAll();
    }


}
