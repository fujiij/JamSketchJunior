package jp.jamsketch.model.engine

import jp.jamsketch.config.Config
import jp.jamsketch.model.CurveData
import jp.jamsketch.model.MelodyData

/**
 * A MelodyGenerateEngine that generates simple melodies
 */
class SimpleMelodyGenerateEngine : AbstractMelodyGenerateEngine(modelPath = "") {

    override fun outlineUpdated(measure: Int, tick: Int) {
        TODO("Not yet implemented")
    }

    override fun getNextNote(curveData: CurveData, melodyData: MelodyData?) {
        TODO("Not yet implemented")
    }
}