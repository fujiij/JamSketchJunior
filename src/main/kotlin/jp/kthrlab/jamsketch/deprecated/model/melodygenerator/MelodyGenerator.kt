package jp.kthrlab.jamsketch.deprecated.model.melodygenerator

import jp.kthrlab.jamsketch.config.AccessibleConfig
import jp.kthrlab.jamsketch.config.Config
import jp.kthrlab.jamsketch.config.IConfigAccessible
import jp.kthrlab.jamsketch.deprecated.model.CurveData

/**
 * Based on the curve information, a melody is generated.
 * Once the generation is complete, it is transferred to MelodyData.
 * Information on the algorithm used for generation is also stored.
 */
class MelodyGenerator() : IConfigAccessible {

    override val config: Config = AccessibleConfig.config
//    private val melodyGenerativeEngine: IMelodyGenerateEngine =
//        MelodyGenerateEngineRegistry.melodyGenerateEngines[config.melody_generate_engine]?.value!!
//
//    private val noteGenerator: AbstractNoteSeqGenerator =
//        NoteSeqGeneratorRegistry.noteSeqGenerators[config.note_seq_generator]?.value!!

    fun generate(data: CurveData) {
//        val melodyData:MelodyData = noteGenerator.generateMusicDataFromCurveData(data, melodyGenerativeEngine)
    }
}