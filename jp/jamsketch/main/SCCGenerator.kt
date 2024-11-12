package jp.jamsketch.main

import groovy.lang.IntRange
import jp.crestmuse.cmx.filewrappers.SCCDataSet
import jp.crestmuse.cmx.inference.MusicCalculator
import jp.crestmuse.cmx.inference.MusicRepresentation
import jp.crestmuse.cmx.processing.CMXController
import jp.jamsketch.config.Config
import kotlin.math.abs

class SCCGenerator(
    var target_part: SCCDataSet.Part,
    var sccdiv: Int,
    var curveLayer: String,
    var expgen: Any,
    var cFG: Config
) : MusicCalculator {
    override fun updated(measure: Int, tick: Int, layer: String, mr: MusicRepresentation) {
        val e = mr.getMusicElement(layer, measure, tick)
        if (!e.rest() && !e.tiedFromPrevious()) {
            val curvevalue = mr.getMusicElement(curveLayer, measure, tick).mostLikely
            println("curve value: $curvevalue")
            if (curvevalue != null) {
                println("e.getMostLikely: " + e.mostLikely.javaClass.toString() + ", curvevalue: " + curvevalue.javaClass.toString())
                val notenum = getNoteNum(e.mostLikely as Int, curvevalue as Double)
                val duration = e.duration() * sccdiv / (cFG.division / cFG.beats_per_measure)
                val onset =
                    ((firstMeasure + measure) * cFG.division + tick) * sccdiv / (cFG.division / cFG.beats_per_measure)
                synchronized(this) {
                    if (onset > CMXController.getInstance().tickPosition) {
                        target_part.noteList.forEach { note ->
                            if (note.onset() < onset && onset <= note.offset()) {
                                if (note.onset() < onset && onset <= note.offset()) {
                                    target_part.remove(note)
                                    target_part.addNoteElement(note.onset(), (onset-1).toLong(),
                                        note.notenum(),
                                        note.velocity(),
                                        note.offVelocity())
                                }
                            }

                            if (onset <= note.onset() && note.offset() <= onset + duration) {
                                target_part.remove(note)
                            }

                            if (note.onset() < onset + duration && onset + duration < note.offset()) {
                                //		  note.setOnset(onset+duration)
                            }
                        }
                        target_part.addNoteElement(
                            onset.toLong(),
                            (onset+duration).toLong(),
                            notenum,
                            100,
                            100

                        )
                        println("all add " + onset.toString() + ", " + (onset + duration).toString() + ", " + notenum.toString())
                    }
                }
            }
        }
    }

    fun getNoteNum(notename: Int, neighbor: Double): Int {
        var best = 0
        for (i in IntRange(0, 11)) {
            val notenum = i * 12 + notename
            if (abs(notenum - neighbor) < abs(best - neighbor)) best = notenum
        }

        return best
    }

    companion object {
        var firstMeasure: Int = 0
    }
}
