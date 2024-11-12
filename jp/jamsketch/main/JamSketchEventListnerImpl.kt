package jp.jamsketch.main

import javax.swing.JOptionPane
import javax.swing.JPanel

/**
 * JamSketchのイベントリスナー
 */
class JamSketchEventListnerImpl
/**
 * コンストラクタ
 *
 * @param panel ダイアログ
 */(private val panel: JPanel) : JamSketchEventListner {
    /**
     * 切断された状態を通知する
     */
    override fun disconnected() {
        JOptionPane.showConfirmDialog(null, panel, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE)
    }
}
