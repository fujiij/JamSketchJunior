package jp.jamsketch.model.engine

import jp.crestmuse.cmx.filewrappers.SCC

class JamSketchEngine extends Engine{

    JamSketchEngine(SCC scc, SCC.Part target_part, @Deprecated cfg) {
        super(scc, target_part, cfg)
    }

}
