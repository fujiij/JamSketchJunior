package jp.jamsketch.main

import jp.crestmuse.cmx.inference.MusicCalculator
import jp.jamsketch.model.melodygenerator.NoteSeqGeneratorSimple

class JamSketchEngineSimple : JamSketchEngineAbstract() {
    override fun initMusicRepresentationLocal() {
        mr.addMusicLayer(MELODY_LAYER, (0..11).toList())
    }

    override fun initLocal() {
        // Do nothing
    }

    override fun musicCalculatorForOutline(): MusicCalculator? {
        var noteSeqGenerator =
            NoteSeqGeneratorSimple(
                noteLayer = MELODY_LAYER,
                chordLayer = CHORD_LAYER,
                beatsPerMeas = config!!.beats_per_measure,
                entropy_bias = config!!.ent_bias,
                modelPath = config!!.model_file)
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
