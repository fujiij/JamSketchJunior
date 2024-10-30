package jp.jamsketch.model.melodygenerator

import jp.crestmuse.cmx.inference.MusicCalculator

/**
 * Import music data to generate and manage melodies and chord progressions.
 */
abstract class AbstractMelodyGenerateEngine : IMelodyGenerateEngine {
    abstract fun  musicCalculatorForOutline() : MusicCalculator
    abstract fun outlineUpdated(measure: Int, tick: Int)
}