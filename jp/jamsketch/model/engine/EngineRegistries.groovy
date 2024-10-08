package jp.jamsketch.model.engine

import jp.jamsketch.util.RegistryList
import jp.jamsketch.util.RegistryObject


class EngineRegistries {
    public static final RegistryList<Engine> ENGINE =
            RegistryList.create();

    public static final RegistryObject<Engine> JAM_SKETCH_ENGINE =
            ENGINE.create(JamSketchEngine.class,"jam_sketch_engine");
    public static final RegistryObject<Engine> LITE_JAM_SKETCH_ENGINE =
            ENGINE.create(LiteJamSketchEngine.class, "lite_jam_sketch_engine");


}
