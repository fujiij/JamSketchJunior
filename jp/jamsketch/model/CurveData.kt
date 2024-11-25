package jp.jamsketch.model

import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class CurveData {
    fun add(p: Point) {
        val target = get(p.x)
        if (target == null) {
            curves.add(p)
        } else {
            curves.remove(target)
            curves.add(p)
        }
        curves.sortWith(comparator = Comparator.comparingInt{o -> o.x})
    }

    val all: ArrayList<ArrayList<Point>>
        get() {
            val pointers =
                ArrayList<ArrayList<Point>>()
            var split_curve =
                ArrayList<Point>()
            var p0: Point? = null
            for (p in curves) {
                if (p0 == null) {
                    p0 = p
                    split_curve.add(p)
                } else {
                    if (abs((p0.x - p.x).toDouble()) <= SPLIT_DISTANCE) {
                        split_curve.add(p)
                        p0 = p
                    } else {
                        pointers.add(split_curve)
                        split_curve = ArrayList()
                        p0 = null
                    }
                }
            }

            if (!split_curve.isEmpty()) pointers.add(split_curve)
            return pointers
        }

    fun get(x: Int): Point? {
        for (p in curves) if (p.x == x) return p
        return null
    }

    val width: Int
        get() = if (curves.isEmpty()) 0
        else curves.maxWithOrNull(Comparator.comparingInt{ it.x })?.x ?:0

    val isEmpty: Boolean
        get() = curves.isEmpty()

    fun removeAll() {
        curves.clear()
    }

    fun remove(x0: Int, x1: Int) {
        for (p in curves) {
            if (p.x >= x0 && p.x <= x1) {
                curves.remove(p)
            }
        }
    }

    fun marge(margeData: CurveData) {
        val m = margeData.all[0]
        remove(m[0].x, m[m.size - 1].x)

        for (p in m) {
            add(p)
        }

        margeData.removeAll()
    }

    val curves = CopyOnWriteArrayList<Point>()
    val SPLIT_DISTANCE: Int = 50
    val CALCULATION_ERROR: Int = 10
}
