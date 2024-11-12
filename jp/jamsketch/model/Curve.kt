package jp.jamsketch.model

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class Curve(endUpdateCurveAction: Consumer<CurveData>) : Runnable {
    fun addContainer(controller: ICurveContainer) {
        curveContainers.add(controller)
    }

    fun setCurveData() {
        for (c in curveContainers) c.initCurveData(curveData)
    }

    fun clear() {
        for (c in curveContainers) c.removeAll(curveData)
    }

    fun updateCurve(p: Point?) {
        for (c in curveContainers) c.updateCurve(p, curveData, endUpdateCurveAction)
    }

    fun getCurves(id: Int): ArrayList<ArrayList<Point>> {
        return curveData[id]!!.all
    }

    override fun run() {
        while (!isEnd) {
            for (c in curveContainers) c.tick(curveData)
            try {
                Thread.sleep(10)
            } catch (e: Exception) {
                DefaultGroovyMethods.println(this, "OWATA")
            }
        }
    }

    protected val curveData: HashMap<Int, CurveData> = HashMap()
    private val curveContainers = CopyOnWriteArrayList<ICurveContainer>()
    private val endUpdateCurveAction: Consumer<CurveData>
    private val isEnd = false

    init {
        val t: Thread = Thread(this::run)
        t.start()
        this.endUpdateCurveAction = endUpdateCurveAction
    }
}
