package jp.jamsketch.model

import jp.jamsketch.main.JamSketch
import jp.jamsketch.view.DisplayCurve
import java.awt.Color

class LunchInitFunction(private val main: JamSketch) {
    fun setup() {
        loadModel()
        loadView()
    }

    protected fun loadView() {
        main.displays.add(DisplayCurve({ main.model!!.curve.getCurves(JamSketchCurve.PLAYER_MAIN)}, Color(0, 0, 255)))
        main.displays.add(DisplayCurve({ main.model!!.curve.getCurves(JamSketchCurve.PLAYER_MARGE)}, Color(255, 0, 0, 128)))
    }

    protected fun loadModel() {
        val model = JamSketchModel()
        model.curve.addContainer(JamSketchCurve(main.controller!!))
        main.model = model
        main.ticker.add(model)

        model.curve.setCurveData()
    }

    companion object {
        fun create(sketch: JamSketch): LunchInitFunction {
            if (INSTANCE != null) return INSTANCE!!
            else {
                INSTANCE = LunchInitFunction(sketch)
                return INSTANCE!!
            }
        }

        fun get(): LunchInitFunction? {
            return INSTANCE
        }

        private var INSTANCE: LunchInitFunction? = null
    }
}
