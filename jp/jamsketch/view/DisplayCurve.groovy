package jp.jamsketch.view

import jp.crestmuse.cmx.processing.gui.SimplePianoRoll
import jp.jamsketch.model.Point

import java.awt.Color
import java.util.function.Supplier

class DisplayCurve implements IDisplay{

    private final Supplier<ArrayList<ArrayList<Point>>> arrayListSupplier;
    private final Color lineCol;

    public DisplayCurve(Supplier<ArrayList<ArrayList<Point>>> arrayListSupplier, Color color){
        this.arrayListSupplier = arrayListSupplier;
        lineCol = color;
    }
    @Override
    void display(SimplePianoRoll scene) {
        scene.strokeWeight(3)
        for(ArrayList<Point> curves : arrayListSupplier.get()){
            Point p0 = null
            for(Point p : curves){
                if(p0 == null) {
                    p0 = p;
                }else{
                    scene.stroke(lineCol.getRed(), lineCol.getGreen(), lineCol.getBlue(), lineCol.getAlpha())
                    scene.line(p0.x, p0.y, p.x, p.y);
                    p0 = p;
                }
            }
        }
    }
}
