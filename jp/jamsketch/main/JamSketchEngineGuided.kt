package jp.jamsketch.main

import jp.crestmuse.cmx.filewrappers.SCCDataSet
import org.codehaus.groovy.runtime.DefaultGroovyMethods

class JamSketchEngineGuided : JamSketchEngineAbstract() {
    override fun musicCalculatorForOutline(): NoteSeqGeneratorGuided {
        val chGuide: Int = cfg!!.channel_guide
        val partGuide: SCCDataSet.Part = scc!!.toDataSet().getFirstPartWithChannel(chGuide)
        return NoteSeqGeneratorGuided(
            MELODY_LAYER,
            CHORD_LAYER,
            partGuide,
            mr,
            cfg!!.initial_blank_measures,
            cfg!!.beats_per_measure
        )
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
