package jp.kthrlab.jamsketch.deprecated.model.engine

import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.kthrlab.jamsketch.deprecated.model.CurveData


/**
 * A MelodyGenerateEngine that generates simple melodies
 */
class SimpleMelodyGenerateEngine : AbstractMelodyGenerateEngine(modelPath = "") {
    private lateinit var model:Any


    override fun outlineUpdated(measure: Int, tick: Int) {
        TODO("Not yet implemented")
    }

    override fun loadModel(modelPath: String) {
        TODO("Not yet implemented")
    }

    override fun getNextNote(curveData: CurveData, mr: MusicRepresentation) {
        TODO("Not yet implemented")
    }
}