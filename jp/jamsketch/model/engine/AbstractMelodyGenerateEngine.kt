package jp.jamsketch.model.engine

import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.crestmuse.cmx.processing.CMXController

/**
 * Import music data to generate and manage melodies and chord progressions.
 */
abstract class AbstractMelodyGenerateEngine(
    val modelPath:String,
    val cmx: CMXController = CMXController.getInstance()
) : IMelodyGenerateEngine{
    init {

    }
    /*
    abstract fun musicCalculatorForOutline(
        noteLayer: String = "",
        chordLayer: String = "",
        guidepart: SCCDataSet.Part? = null,
        mr: MusicRepresentation? = null,
        initial: Int = 0,
        beatsPerMeas: Int = 0,
        entropy_bias: Double = 0.0,
        model: Map<String, Any?> = emptyMap(),
        ) : MusicCalculator
        
     */
    abstract fun outlineUpdated(measure: Int, tick: Int)
}