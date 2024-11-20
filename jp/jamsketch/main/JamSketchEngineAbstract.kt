package jp.jamsketch.main

import jp.crestmuse.cmx.filewrappers.SCC
import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.inference.MusicCalculator
import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.crestmuse.cmx.misc.ChordSymbol2
import jp.crestmuse.cmx.misc.ChordSymbol2.NON_CHORD
import jp.crestmuse.cmx.processing.CMXController
import jp.jamsketch.config.Config

// TODO: passing config is prohibited
abstract class JamSketchEngineAbstract : JamSketchEngine {
    override fun init(scc: SCC, target_part: SCC.Part, cfg: Config) {
        this.scc = scc
        this.cfg = cfg
        cmx = CMXController.getInstance()

        initMusicRepresentation()
        // if (cfg.EXPRESSION) {
        //    expgen = new ExpressionGenerator()
        //    expgen.start(scc.getFirstPartWithChannel(1),
        //           getFullChordProgression(), cfg.BEATS_PER_MEASURE)
        // }
        val sccgen = SCCGenerator(target_part as SCCDataSet.Part, scc.division, OUTLINE_LAYER, expgen, cfg)
        mr.addMusicCalculator(MELODY_LAYER, sccgen)
        val calc: MusicCalculator? = musicCalculatorForOutline()
        if (calc != null) {
            mr.addMusicCalculator(OUTLINE_LAYER, calc)
        }

        initLocal()
    }

    fun initLocal() {
        // do nothing
    }

    fun initMusicRepresentation() {
        this.mr = CMXController.createMusicRepresentation(cfg!!.num_of_measures, cfg!!.division)
        mr.addMusicLayerCont(OUTLINE_LAYER)
        mr.addMusicLayer(MELODY_LAYER, (0..11).toList())
        mr.addMusicLayer(
            CHORD_LAYER,
            listOf<ChordSymbol2>(ChordSymbol2.C, ChordSymbol2.F, ChordSymbol2.G),
            cfg!!.division)
        cfg!!.chordprog.forEachIndexed { index, chord ->
            mr.getMusicElement(CHORD_LAYER, index, 0).setEvidence(ChordSymbol2.parse(chord))
        }
    }


    val fullChordProgression: Any
        get() = List(cfg!!.initial_blank_measures) { NON_CHORD } +
                List(cfg!!.repeat_times) {cfg!!.chordprog.toList()}.flatten()

    abstract fun musicCalculatorForOutline(): MusicCalculator?

    override fun setMelodicOutline(measure: Int, tick: Int, value: Double) {
        val e = mr.getMusicElement(OUTLINE_LAYER, measure, tick)
        if (!automaticUpdate()) {
            e.suspendUpdate()
        }
        e.setEvidence(value)
        outlineUpdated(measure, tick)
    }

    override fun getMelodicOutline(measure: Int, tick: Int): Double {
        return ((mr.getMusicElement(OUTLINE_LAYER, measure, tick).mostLikely) as Double)
    }

    abstract fun outlineUpdated(measure: Int, tick: Int)

    abstract fun automaticUpdate(): Boolean

    override fun initMelodicOutline() {
        // TODO: Don't need to refer config?
        mr.getMusicElementList(OUTLINE_LAYER).forEach { element ->
            element.setEvidence(Double.NaN)
        }
//        (0..cfg!!.num_of_measures-1).forEach { i ->
//            (0..cfg!!.division-1).forEach { j ->
//                mr.getMusicElement(OUTLINE_LAYER, i, j).
//                setEvidence(Double.NaN)
//            }
//        }
    }

    override fun setFirstMeasure(num: Int) {
        SCCGenerator.firstMeasure = num
    }

    override fun getChord(measure: Int, tick: Int): ChordSymbol2? {
        return ((mr.getMusicElement(CHORD_LAYER, measure, tick).mostLikely) as ChordSymbol2)
    }

    lateinit var mr: MusicRepresentation
    var cmx: CMXController? = null
    var cfg: Config? = null
    var scc: SCC? = null
    var expgen: Any? = null

    companion object {
        var OUTLINE_LAYER: String = "curve"
        var MELODY_LAYER: String = "melody"
        var CHORD_LAYER: String = "chord"
    }
}
