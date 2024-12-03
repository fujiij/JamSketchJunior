package jp.kthrlab.jamsketch.model.engine

/**
 *
 */
object MelodyGenerateEngineRegistry {

     /**
      *
      */
     lateinit var melodyGenerateEngines: MutableMap<String, Lazy<IMelodyGenerateEngine>>
     init {
         melodyGenerateEngines[SimpleMelodyGenerateEngine::class.simpleName.toString()] =
             lazy { SimpleMelodyGenerateEngine() }
     }
}
    