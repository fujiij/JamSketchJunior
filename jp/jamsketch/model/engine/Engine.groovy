package jp.jamsketch.model.engine

import groovy.json.JsonSlurper
import jp.crestmuse.cmx.filewrappers.SCC
import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.crestmuse.cmx.misc.ChordSymbol2
import jp.crestmuse.cmx.processing.CMXController
import jp.jamsketch.main.JamSketch
import jp.jamsketch.main.SCCGenerator

import static jp.crestmuse.cmx.misc.ChordSymbol2.C
import static jp.crestmuse.cmx.misc.ChordSymbol2.F
import static jp.crestmuse.cmx.misc.ChordSymbol2.G

abstract class Engine {

    @Deprecated
    protected def cfg;

    protected SCC scc;
    protected SCC.Part part;
    MusicRepresentation mr;
    CMXController cmx
    def model
    def expgen = null
    static String OUTLINE_LAYER = "curve"
    static String MELODY_LAYER = "melody"
    static String CHORD_LAYER = "chord"


    public Engine(CMXController sketch, @Deprecated def cfg){
        this.cfg = cfg;

        this.scc = sketch.readSMFAsSCC(cfg.MIDIFILENAME);

        part = scc.getFirstPartWithChannel(1);
        def json = new JsonSlurper()
        model = json.parseText((new File(cfg.MODEL_FILE)).text)
        cmx = CMXController.getInstance()
        mr = cmx.createMusicRepresentation(cfg.NUM_OF_MEASURES,
                cfg.DIVISION)
        mr.addMusicLayerCont(OUTLINE_LAYER)
        // mr.addMusicLayer(MELODY_LAYER, (0..11) as int[])
        mr.addMusicLayer(MELODY_LAYER, (0..11) as int[])
        mr.addMusicLayer(CHORD_LAYER,
                [C, F, G] as ChordSymbol2[],	// temporary
                cfg.DIVISION)
        cfg.chordprog.eachWithIndex{ c, i ->
            mr.getMusicElement(CHORD_LAYER, i, 0).setEvidence(c)
        }

        def sccgen = new SCCGenerator(part, scc.division,
                OUTLINE_LAYER, expgen, cfg)
        mr.addMusicCalculator(MELODY_LAYER, sccgen)
        def calc = musicCalculatorForOutline()
        if (calc != null) {
            mr.addMusicCalculator(OUTLINE_LAYER, calc)
        }
    }
}
