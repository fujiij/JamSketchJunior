package jp.jamsketch.main

import groovy.lang.IntRange
import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.misc.PianoRoll
import jp.crestmuse.cmx.processing.CMXApplet
import jp.crestmuse.cmx.processing.CMXController
import jp.jamsketch.config.Config
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import java.util.*
import java.util.stream.IntStream

class MelodyData2(
    filename: String?,
    var width: Int,
    cmxcontrol: CMXApplet,
    var pianoroll: PianoRoll,
    var cfg: Config
) {
    fun resetCurve() {
        curve1 = arrayOfNulls(width)
        Arrays.fill(curve1, null)
        engine!!.resetMelodicOutline()
    }

    fun storeCursorPosition(from: Int, thru: Int, y: Int) {
        IntStream.rangeClosed(from, thru).forEach { i: Int -> curve1!![i - cfg.keyboard_width] = y }
    }

    fun updateCurve(from: Int, thru: Int) {
        val nMeas = cfg.num_of_measures
        val div = cfg.division
        val size2 = nMeas * div

        for (i in IntRange(from, thru)) {
            if (curve1!![i - cfg.keyboard_width] != null) {
                val nn = (if (curve1!![i - cfg.keyboard_width] == null) null else DefaultGroovyMethods.invokeMethod(
                    pianoroll, "y2notenum", arrayOf(
                        curve1!![i - cfg.keyboard_width]
                    )
                )) as Double
                val ii = i - cfg.keyboard_width
                val position = (ii * size2 / (DefaultGroovyMethods.size(curve1)))

                if (position >= 0) {
                    engine!!.setMelodicOutline((position / div), position % div, nn)
                }
            }
        }
    }

    var engine: JamSketchEngine? = null
    var curve1: Array<Int?>? = null
    var scc: SCCDataSet = ((CMXController.readSMFAsSCC(filename)) as SCCDataSet)
    var pACKAGE_NAME: String = "jp.jamsketch.main"

    init {
        scc.repeat(
            (cfg.initial_blank_measures * cfg.beats_per_measure * scc.division).toLong(),
            ((cfg.initial_blank_measures + cfg.num_of_measures) * cfg.beats_per_measure * scc.division).toLong(),
            cfg.repeat_times - 1
        )

        val target_part = scc.getFirstPartWithChannel(1)

        try {
            engine = ((Class.forName(pACKAGE_NAME + "." + cfg.jamsketch_engine).newInstance()) as JamSketchEngine)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
        engine!!.init(scc, target_part, cfg)
        resetCurve()
    }
}
