package jp.jamsketch.main

import java.util.*

/**
 * JamSketchのイベントリスナー
 */
interface JamSketchEventListner : EventListener {
    /**
     * 切断された状態を通知する
     */
    fun disconnected()
}
