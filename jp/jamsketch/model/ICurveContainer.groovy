package jp.jamsketch.model


interface ICurveContainer {
    /**
     * MODはカーブデータをここで初期化する
     * @param curves Curveクラスが保有するCurveData
     */
    public void initCurveData(HashMap<Integer, CurveData> curves);

    /**
     * 新しく座標が更新されたときに呼び出されるメソッド。
     *　各クラスが追加されたCurveDataに座標を更新する必要がある。
     * @param p 新しく追加される座標の情報
     * @param curves Curveクラスが保有するCurveData
     *
     */
    public void updateCurve(Point p, HashMap<Integer, CurveData> curves);

    /**
     * カーブデータの座標の情報をすべて削除する。
     * @param curves
     */
    public void removeAll(HashMap<Integer, CurveData> curves);

    public default void tick(HashMap<Integer, CurveData> curves){}
}