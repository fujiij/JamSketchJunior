package jp.jamsketch.model.melodygenerator

import jp.crestmuse.cmx.inference.MusicCalculator

/**
 * A MelodyGenerateEngine that generates simple melodies
 */
class SimpleMelodyGenerateEngine : AbstractMelodyGenerateEngine() {
    override fun musicCalculatorForOutline(): MusicCalculator {
        return NoteSeqGenerator(
            noteLayer = TODO(),
            chordLayer = TODO(),
            beatsPerMeas = TODO(),
            entropy_bias = TODO(),
            model = TODO()
        )
    }

    override fun outlineUpdated(measure: Int, tick: Int) {
        TODO("Not yet implemented")
    }

    override fun generate() {
        TODO("Not yet implemented")
    }
}