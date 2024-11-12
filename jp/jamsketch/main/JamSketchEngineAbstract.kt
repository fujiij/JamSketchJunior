package jp.jamsketch.main

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jp.crestmuse.cmx.filewrappers.SCC
import jp.crestmuse.cmx.inference.MusicCalculator
import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.crestmuse.cmx.misc.ChordSymbol2
import jp.crestmuse.cmx.misc.ChordSymbol2.NON_CHORD
import jp.crestmuse.cmx.processing.CMXController
import jp.jamsketch.config.Config
import java.io.File
import java.util.*

abstract class JamSketchEngineAbstract : JamSketchEngine {
    fun init(scc: SCC, target_part: SCC.Part?, cfg: Config) {
        this.scc = scc
        this.cfg = cfg
        val mapper = jacksonObjectMapper()
        val jsonFile = File(cfg.model_file)
        model = mapper.readValue(jsonFile)
        cmx = CMXController.getInstance()
        CMXController.createMusicRepresentation(cfg.num_of_measures, cfg.division).also { this.mr = it }
        mr!!.addMusicLayerCont(OUTLINE_LAYER)
        // mr.addMusicLayer(MELODY_LAYER, (0..11) as int[])
        mr!!.addMusicLayer(MELODY_LAYER, (0..11).toList())
        mr!!.addMusicLayer(CHORD_LAYER, listOf<ChordSymbol2>(ChordSymbol2.C, ChordSymbol2.F, ChordSymbol2.G), cfg.division)
        cfg.chordprog.forEachIndexed { index, chord ->
            mr!!.getMusicElement(CHORD_LAYER, index, 0).setEvidence(chord)
        }
        // if (cfg.EXPRESSION) {
        //    expgen = new ExpressionGenerator()
        //    expgen.start(scc.getFirstPartWithChannel(1),
        //           getFullChordProgression(), cfg.BEATS_PER_MEASURE)
        // }
        val sccgen = SCCGenerator(target_part, scc.division, OUTLINE_LAYER, expgen, cfg)
        mr!!.addMusicCalculator(MELODY_LAYER, sccgen)
        val calc: MusicCalculator? = musicCalculatorForOutline()
        if (calc != null) {
            mr!!.addMusicCalculator(OUTLINE_LAYER, calc)
        }

        init_local()
    }

    fun init_local() {
        // do nothing
    }

    val fullChordProgression: Any
        get() = List(cfg!!.initial_blank_measures) { NON_CHORD } +
                List(cfg!!.repeat_times) {cfg!!.chordprog.toList()}.flatten()

    abstract fun musicCalculatorForOutline(): MusicCalculator?

    override fun setMelodicOutline(measure: Int, tick: Int, value: Double) {
        val e = mr!!.getMusicElement(OUTLINE_LAYER, measure, tick)
        if (!automaticUpdate()) {
            e.suspendUpdate()
        }
        e.setEvidence(value)
        outlineUpdated(measure, tick)
    }

    override fun getMelodicOutline(measure: Int, tick: Int): Double {
        return ((mr!!.getMusicElement(OUTLINE_LAYER, measure, tick).mostLikely) as Double)
    }

    abstract fun outlineUpdated(measure: Int, tick: Int): Any?

    abstract fun automaticUpdate(): Boolean

    override fun resetMelodicOutline() {
        (0..cfg!!.num_of_measures-1).forEach { i ->
            (0..cfg!!.division-1).forEach { j ->
                mr!!.getMusicElement(OUTLINE_LAYER, i, j).
                setEvidence(Double.NaN)
            }
        }
    }

    override fun setFirstMeasure(num: Int) {
        SCCGenerator.firstMeasure = num
    }

    override fun getChord(measure: Int, tick: Int): ChordSymbol2? {
        return ((mr!!.getMusicElement(CHORD_LAYER, measure, tick).mostLikely) as ChordSymbol2)
    }

    var mr: MusicRepresentation? = null
    var cmx: CMXController? = null
    var cfg: Config? = null
    var model: MutableMap<String, Any?>? = null
    var scc: SCC? = null
    var expgen: Any? = null

    companion object {
        var OUTLINE_LAYER: String = "curve"
        var MELODY_LAYER: String = "melody"
        var CHORD_LAYER: String = "chord"
    }
}
