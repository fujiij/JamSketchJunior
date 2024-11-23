package jp.jamsketch.main

import groovy.lang.IntRange
import groovy.lang.ObjectRange
import jp.crestmuse.cmx.filewrappers.SCC
import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.processing.CMXApplet
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import java.util.*

// TODO: Stop passing notenum2y & beat2x
class GuideData(
    filename: String?,
    val size: Int,
    val initial_blank_measures: Int,
    val beats_per_measure: Int,
    val guide_smoothness: Int,
    val channel_guide: Int,
    val notenum2y: (Double) -> Double,
    val beat2x: (Int, Double) -> Double,
) {
    var curveGuide: List<Int?>
    var curveGuideView: List<Int?>? = null
    var from: Int = 0
    var sccDataSet: SCCDataSet = ((CMXApplet.readSMFAsSCC(javaClass.getResource("/${filename}").path)) as SCCDataSet)

    init {
        val guide_part = sccDataSet.getFirstPartWithChannel(channel_guide)
        val smoothness: Int = guide_smoothness
        curveGuide = createCurve(guide_part, smoothness,)
        updateCurve(from, size)
    }

    fun createCurve(part: SCC.Part,
                    smoothness: Int,
    ): List<Int?> {
        val curve = arrayOfNulls<Int>(size * 4).toMutableList() // 4 is temporary
        val beats = beats_per_measure
        val initial = initial_blank_measures
        part.noteList.forEach { note ->
                try {
                    val y: Double = notenum2y(note.notenum() - 0.5)
                    val onset: Int = (note.onset(480) / 480).toInt()
                    val m1: Int = (onset / beats)
                    val b1 = onset - m1 * beats
                    val x1: Double = beat2x(m1 - initial, b1.toDouble()) - 100 // 100 is temporary
                    val offset: Int = (note.offset(480) / 480).toInt()
                    val m2: Int = (offset / beats)
                    val b2 = offset - m2 * beats
                    val x2: Double = beat2x(m2 - initial, b2.toDouble()) - 100
                    for (x in ObjectRange(x1, x2)) {
                        curve[x as Int] = y.toInt()
                    }
                } catch (e: UnsupportedOperationException) {
                }
            }
        return smoothCurve(curve, smoothness)
    }

    fun smoothCurve(curve: List<Int?>, K: Int): List<Int?> {
        DefaultGroovyMethods.println(this, "K=$K")
        val curve2 = curve.toTypedArray().clone()
        for (i in IntRange(0, DefaultGroovyMethods.size(curve))) {
            if (curve[i] != null) {
                var n = 1
                for (k in IntRange(1, K)) {
                    if (i - k >= 0 && curve[i - k] != null) {
                        curve2[i] = curve2[i]!! + curve[i - k]!!
                        n++
                    }
                    if (i + k < curve.size && curve[i + k] != null) {
                        curve2[i] = curve2[i]!! + curve[i+k]!!
                        n++
                    }
                }
                curve2[i] = curve2[i]!! / n
            }
        }
        return curve2.toList()
    }

    fun shiftCurve(): List<Int?> {
        curveGuideView = ((DefaultGroovyMethods.multiply(
            ArrayList(Arrays.asList<Any?>(null)),
            size
        )) as List<Int?>)
        return updateCurve(size.let { from += it; from }, from + size)
    }

    fun updateCurve(from: Int, to: Int): List<Int?> {
        if (from < curveGuide.size) {
            val toIndex = if ((to <= curveGuide.size)) to else curveGuide.size
            //            println("from = ${from}, toIndex = ${toIndex}")
            return curveGuide.subList(from, toIndex)
            //            println(curveGuideView)
        }
        return updateCurve(from, to)
    }

}
