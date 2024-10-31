package jp.jamsketch.model.melodygenerator

import jp.jamsketch.config.AccessibleConfig
import jp.jamsketch.config.Config
import jp.jamsketch.config.IConfigAccessible
import jp.jamsketch.model.CurveData

/**
 * Based on the curve information, a melody is generated.
 * Once the generation is complete, it is transferred to MelodyData.
 * Information on the algorithm used for generation is also stored.
 */
class MelodyGenerator() : IConfigAccessible {
    override val config: Config = AccessibleConfig.config
    val melodyGenerateEngine: IMelodyGenerateEngine =
        MelodyGenerateEngineRegistry.melodyGeneratEengines[config.melody_generate_engine]?.value!!

    fun generate(data: CurveData) {
        melodyGenerateEngine.generate()
    }
}