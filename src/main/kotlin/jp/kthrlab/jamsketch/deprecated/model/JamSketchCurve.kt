package jp.kthrlab.jamsketch.deprecated.model

import jp.kthrlab.jamsketch.controller.IJamSketchController
import jp.kthrlab.jamsketch.controller.JamMouseListener
import java.util.function.Consumer

class JamSketchCurve(controller: IJamSketchController) : ICurveContainer, JamMouseListener {

    override fun initCurveData(curves: HashMap<Int, CurveData>) {
        curves!![PLAYER_MAIN] = CurveData()
        curves[PLAYER_MARGE] = CurveData()
    }

    override fun updateCurve(
        p: Point,
        curves: HashMap<Int, CurveData>,
        endUpdateCurveAction: Consumer<CurveData>,
    ) {
        if (p.x >= curves[PLAYER_MAIN]!!.width) {
            curves[PLAYER_MAIN]!!.add(p)
            endUpdateCurveAction.accept(curves[PLAYER_MAIN]!!)
        } else {
            curves[PLAYER_MARGE]!!.add(p)
            endUpdateCurveAction.accept(curves[PLAYER_MARGE]!!)
        }
    }

    override fun removeAll(curves: HashMap<Int, CurveData>) {
        curves!![PLAYER_MAIN]!!.removeAll()
    }


    override fun tick(curves: HashMap<Int, CurveData>) {
        if (isReleased) {
            isReleased = false
            if (!curves!![PLAYER_MARGE]!!.isEmpty) curves[PLAYER_MAIN]!!
                .marge(curves[PLAYER_MARGE]!!)
        }
    }

    override fun mouseReleased(p: Point?) {
        isReleased = true
    }

    private var isReleased = false

    init {
//        controller.addListener(this)
    }

    companion object {
        const val PLAYER_MAIN: Int = 0
        const val PLAYER_MARGE: Int = 1
    }
}
