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

        var config : Config =
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

        override fun save() {
            println(jsonFile.path)
            mapper.addMixIn(ConfigJSON::class.java, Config::class.java)
            mapper.writeValue(jsonFile, config)
        }
    }
}

data object AccessibleConfig : SealedConfig() {
    fun config() : Config? {
        return if (isAssignableFrom(Thread.currentThread().stackTrace)) config else null
    }

    fun save() {
        if (isAssignableFrom(Thread.currentThread().stackTrace)) { SealedConfig.save() }
    }

    private fun isAssignableFrom(stackTrace: Array<StackTraceElement>) : Boolean {
        stackTrace.forEach {
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