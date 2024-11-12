package jp.jamsketch.main

import jp.crestmuse.cmx.inference.MusicCalculator
import jp.jamsketch.model.melodygenerator.SimpleNoteSeqGenerator

class JamSketchEngineSimple : JamSketchEngineAbstract() {

    override fun musicCalculatorForOutline(): MusicCalculator? {
        var noteSeqGenerator =
            SimpleNoteSeqGenerator(
                noteLayer = MELODY_LAYER,
                chordLayer = CHORD_LAYER,
                beatsPerMeas = cfg!!.beats_per_measure,
                entropy_bias = cfg!!.ent_bias,
                model = model!!)
        return noteSeqGenerator
    }

    override fun outlineUpdated(measure: Int, tick: Int) {
        // do nothing
    }

    override fun automaticUpdate(): Boolean {
        return true
    }

    fun parameters(): Map<String, Double> {
        return LinkedHashMap()
    }

    fun paramDesc(): Map<String, String> {
        return LinkedHashMap()
    }
}
