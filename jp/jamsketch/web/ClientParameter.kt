package jp.jamsketch.web

/**
 * クライアントの送信用パラメータ
 */
class ClientParameter {
    /**
     * デフォルトコンストラクタ
     * これが無いとJSONをデリシアライズできない
     */
    constructor()

    /**
     * コンストラクタ
     *
     * @param from 始点
     * @param thru 終点
     * @param y    Y座標
     */
    constructor(from: Int, thru: Int, y: Int) {
        this.from = from
        this.thru = thru
        this.y = y
    }

    var from: Int = 0
    var thru: Int = 0
    var y: Int = 0
}
