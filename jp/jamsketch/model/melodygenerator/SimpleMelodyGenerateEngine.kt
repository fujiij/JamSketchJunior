package jp.jamsketch.model.melodygenerator

import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.inference.MusicCalculator
import jp.crestmuse.cmx.inference.MusicRepresentation

/**
 * A MelodyGenerateEngine that generates simple melodies
 */
class SimpleMelodyGenerateEngine : AbstractMelodyGenerateEngine() {

    override fun musicCalculatorForOutline(
        noteLayer: String,
        chordLayer: String,
        guidepart: SCCDataSet.Part?,
        mr: MusicRepresentation?,
        initial: Int,
        beatsPerMeas: Int,
        entropyBias: Double,
        model: Map<String, Any?>
    ): MusicCalculator {
        return NoteSeqGenerator(noteLayer, chordLayer, beatsPerMeas, entropyBias, model)
    }

    override fun outlineUpdated(measure: Int, tick: Int) {
        TODO("Not yet implemented")
    }

    override fun generate() {
        TODO("Not yet implemented")
    }
}