package jp.jamsketch.model.melodygenerator

import jp.jamsketch.config.AccessibleConfig
import jp.jamsketch.config.Config
import jp.jamsketch.config.IConfigAccessible
import jp.jamsketch.model.CurveData
import jp.jamsketch.model.MelodyData
import jp.jamsketch.model.engine.IMelodyGenerateEngine
import jp.jamsketch.model.engine.MelodyGenerateEngineRegistry

/**
 * Based on the curve information, a melody is generated.
 * Once the generation is complete, it is transferred to MelodyData.
 * Information on the algorithm used for generation is also stored.
 */
class MelodyGenerator() : IConfigAccessible {

    override val config: Config = AccessibleConfig.config
    private val melodyGenerativeEngine: IMelodyGenerateEngine =
        MelodyGenerateEngineRegistry.melodyGenerateEngines[config.melody_generate_engine]?.value!!

    private val noteGenerator: AbstractNoteSeqGenerator =
        NoteSeqGeneratorRegistry.noteSeqGenerators[config.note_seq_generator]?.value!!

    fun generate(data: CurveData) {
        val melodyData:MelodyData = noteGenerator.generateMusicDataFromCurveData(data, melodyGenerativeEngine)
    }
}