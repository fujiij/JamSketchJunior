package jp.jamsketch.main

import jp.crestmuse.cmx.filewrappers.SCC
import jp.crestmuse.cmx.processing.CMXController
import java.util.stream.IntStream

class MusicData(
    filename: String?,
    var width: Int,
    val initial_blank_measures: Int,
    val beats_per_measure: Int,
    val num_of_measures: Int,
    val repeat_times: Int,
    val division: Int,
) {
    var curve1: MutableList<Int?> = arrayOfNulls<Int>(width).toMutableList()
    var scc: SCC = CMXController.readSMFAsSCC(filename)

    fun initCurve() {
        curve1 = arrayOfNulls<Int>(width).toMutableList()
    }

    fun storeCursorPosition(from: Int, thru: Int, y: Int) {
        IntStream.rangeClosed(from, thru).forEach { i: Int -> curve1[i] = y }
    }

    fun storeCursorPosition(i: Int, y: Int) {
        curve1[i] = y
    }

    init {
        scc.toDataSet().repeat(
            (initial_blank_measures * beats_per_measure * scc.division).toLong(),
            ((initial_blank_measures + num_of_measures) * beats_per_measure * scc.division).toLong(),
            repeat_times - 1
        )

    }
}
