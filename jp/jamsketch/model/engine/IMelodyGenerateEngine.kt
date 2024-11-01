package jp.jamsketch.model.engine

import jp.jamsketch.model.CurveData
import jp.jamsketch.model.MelodyData

interface IMelodyGenerateEngine {
    fun getNextNote(curveData: CurveData, melodyData: MelodyData?= null)
}