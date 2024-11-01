package jp.jamsketch.model.melodygenerator

import jp.jamsketch.model.CurveData
import jp.jamsketch.model.MelodyData
import jp.jamsketch.model.engine.IMelodyGenerateEngine

/**
 *
 */
class SimpleNoteSeqGenerator: AbstractNoteSeqGenerator() {

    override fun generateMusicDataFromCurveData(curveData: CurveData, engine: IMelodyGenerateEngine): MelodyData {
        TODO("Not yet implemented")
    }

    override fun generateMusicDataFromLength(noteLength: Int, engine: IMelodyGenerateEngine): MelodyData {
        TODO("Not yet implemented")
    }

    override fun generateMusicDataFromMeasure(measureLength: Int, engine: IMelodyGenerateEngine): MelodyData {
        TODO("Not yet implemented")
    }

}