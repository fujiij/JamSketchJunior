package jp.kthrlab.jamsketch.deprecated

import jp.crestmuse.cmx.processing.gui.SimplePianoRoll
import jp.kthrlab.jamsketch.deprecated.model.Point
import java.awt.Color
import java.util.function.Supplier

class DisplayCurve(private val arrayListSupplier: Supplier<ArrayList<ArrayList<Point>>>, private val lineCol: Color) :
    IDisplay {
    override fun display(scene: SimplePianoRoll?) {
        scene!!.strokeWeight(3f)
        for (curves in arrayListSupplier.get()) {
            var p0: Point? = null
            for (p in curves) {
                if (p0 == null) {
                    p0 = p
                } else {
                    scene.stroke(
                        lineCol.red.toFloat(),
                        lineCol.green.toFloat(),
                        lineCol.blue.toFloat(),
                        lineCol.alpha.toFloat()
                    )
                    scene.line(p0.x.toFloat(), p0.y.toFloat(), p.x.toFloat(), p.y.toFloat())
                    p0 = p
                }
            }
        }
    }
}
