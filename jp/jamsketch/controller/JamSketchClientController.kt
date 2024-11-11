package jp.jamsketch.controller

import jp.jamsketch.main.JamSketchEventListner
import jp.jamsketch.model.Point
import jp.jamsketch.web.ClientParameter
import jp.jamsketch.web.WebSocketClient

/**
 * JamSketchを操作するクラスにデータ送信機能を持たせたもの
 * JamSketchの操作自体は委譲によって実現する
 */
class JamSketchClientController(
    private val host: String,
    private val port: Int,
    private val innerController: IJamSketchController,
    private val listner: JamSketchEventListner
) :
    IJamSketchController {
    /**
     * 初期化する
     */
    override fun init() {
        webSocketClient.Init(this.host, this.port, this.listner)
    }

    /**
     * Curveを更新する
     *
     * @param from 始点
     * @param thru 終点
     * @param y    Y座標
     */
    override fun updateCurve(from: Int, thru: Int, y: Int) {
        // 内部で持つ操作クラスで更新操作をする
        innerController.updateCurve(from, thru, y)

        // WebSocketで更新情報をサーバーに送る
        webSocketClient.Send(ClientParameter(from, thru, y))
    }

    /**
     * リセットする
     */
    override fun reset() {
        // 内部で持つ操作クラスでリセット操作をする
        // クライアントはリセット時にデータは送らないため、他の処理はしない
        innerController.reset()
    }

    override fun addListener(listener: JamMouseListener?) {
        innerController.addListener(listener)
    }

    override fun mouseReleased(p: Point?) {
        innerController.mouseReleased(p)
    }

    private val webSocketClient = WebSocketClient()

    /**
     * コンストラクタ
     *
     * @param host       接続先ホスト
     * @param port       接続先ポート
     * @param controller 内部で持つ操作クラス
     * @param listner    イベントリスナー
     */
    init {
        this.init()
    }
}