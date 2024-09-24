package jp.jamsketch.model;

import com.fasterxml.jackson.databind.annotation.NoClass

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class CurveData {
    final ArrayList<Point> curves = new ArrayList<>();


    public CurveData(){}

    public void add(Point p){
        Point target = get(p.x);
        if(target == null){
            curves.add(p);
        }else{
            curves.remove(target);
            curves.add(p);
        }
        print("Curve X:" + p.x)
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
                if(Math.abs(p0.x - p.x) <= 50){
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

    public void removeAll(){
        curves.clear();
    }

}
