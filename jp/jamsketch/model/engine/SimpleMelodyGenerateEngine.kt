package jp.jamsketch.model.engine

import groovy.json.JsonSlurper
import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.jamsketch.config.Config
import jp.jamsketch.model.CurveData
import jp.jamsketch.model.MelodyData
import java.io.File

/**
 * A MelodyGenerateEngine that generates simple melodies
 */
class SimpleMelodyGenerateEngine : AbstractMelodyGenerateEngine(modelPath = "") {
    private lateinit var model:Any


    override fun outlineUpdated(measure: Int, tick: Int) {
        TODO("Not yet implemented")
    }

    override fun loadModel(modelPath: String) {
        val json:JsonSlurper = JsonSlurper()
        model = json.parseText(File(modelPath).readText())

    }

    override fun getNextNote(curveData: CurveData, mr: MusicRepresentation) {

    }
}