package jp.jamsketch.model.melodygenerator

object MelodyGenerateEngineRegistry {
     lateinit var melodyGeneratEengines: MutableMap<String, Lazy<IMelodyGenerateEngine>>

     init {
         melodyGeneratEengines[SimpleMelodyGenerateEngine::class.simpleName.toString()] =
             lazy { SimpleMelodyGenerateEngine() }
     }
}
