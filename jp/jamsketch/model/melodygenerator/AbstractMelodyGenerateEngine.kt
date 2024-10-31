package jp.jamsketch.model.melodygenerator

import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.inference.MusicCalculator
import jp.crestmuse.cmx.inference.MusicRepresentation

/**
 * Import music data to generate and manage melodies and chord progressions.
 */
abstract class AbstractMelodyGenerateEngine : IMelodyGenerateEngine {
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
    abstract fun outlineUpdated(measure: Int, tick: Int)
}