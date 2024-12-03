package jp.kthrlab.jamsketch.model

import jp.kthrlab.jamsketch.main.JamSketchEngine
import jp.kthrlab.jamsketch.main.JamSketchEngineSimple
import jp.kthrlab.jamsketch.util.Tick
import java.util.function.Consumer

class JamSketchModel : Tick {
    override fun tick() {
    }

    fun generateMelody(data: CurveData?) {
    }

    var curve: Curve =
        Curve(Consumer { data: CurveData? ->
            this.generateMelody(
                data
            )
        })

    // TODO: Retrieve the engine specified in the config file.
    // This is a placeholder code to avoid compilation errors.
    var engine: JamSketchEngine = JamSketchEngineSimple()
}
