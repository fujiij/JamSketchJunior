package jp.jamsketch.config
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jp.crestmuse.cmx.misc.ChordSymbol2
import java.io.File

sealed class SealedConfig {

    companion object : AbstractConfig() {
        private var jsonFile = File(this::class.java.getResource("config.json").path)
        private val mapper = jacksonObjectMapper()
        var value : MutableMap<String, Any?> = mapper.readValue(jsonFile)

        @JvmStatic
        protected var config : Config =
            (mapper.readValue(jsonFile, Config::class.java) as Config).let {
            it.chordprog.forEach { chord ->
                it.chord_symbols.add(
                    ChordSymbol2.parse(chord)
                )
            }
            it
        }

        override fun load() {
        }

        @JvmStatic
        override fun save() {
            mapper.addMixIn(ConfigJSON::class.java, Config::class.java)
            mapper.writeValue(jsonFile, config)
        }
    }
}

data object AccessibleConfig : SealedConfig() {

    val config : Config
        get() {
            if (isAssignableFrom(Thread.currentThread().stackTrace)) {
                return SealedConfig.config
            } else {
                throw IllegalAccessException()
            }
        }

    fun save() {
        if (isAssignableFrom(Thread.currentThread().stackTrace)) { SealedConfig.save() }
    }

    private fun isAssignableFrom(stackTrace: Array<StackTraceElement>) : Boolean {
        stackTrace.reversed().forEach {
            val callerClass = Class.forName(it.className)
            if (IConfigAccessible::class.java.isAssignableFrom(callerClass)) return true
        }
        return false
    }
}

data object ModelConfig : SealedConfig() {
//  ModelConfig.config で参照したい場合
//    var config = SealedConfig.config
    fun save()  =  SealedConfig.save()
}

data object ControllerConfig : SealedConfig(

) {

}

data object ViewConfig : SealedConfig() {

}