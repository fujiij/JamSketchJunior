package jp.jamsketch.model.melodygenerator

import jp.jamsketch.model.CurveData
import jp.jamsketch.model.MelodyData
import jp.jamsketch.model.engine.IMelodyGenerateEngine

/**
 * ...
 */
abstract class AbstractNoteSeqGenerator{
    /**
     * CurveDataオブジェクトの情報を基に、旋律を生成する
     */
    abstract fun generateMusicDataFromCurveData(curveData: CurveData, engine: IMelodyGenerateEngine):MelodyData


    /**
     * 旋律の音の数を指定し、旋律を生成する。
     */
    abstract fun generateMusicDataFromLength(noteLength: Int, engine: IMelodyGenerateEngine): MelodyData

    /**
     * 旋律の小節数を指定し、生成を行う
     */
    abstract fun generateMusicDataFromMeasure(measureLength: Int, engine: IMelodyGenerateEngine): MelodyData
}