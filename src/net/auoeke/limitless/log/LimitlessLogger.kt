package net.auoeke.limitless.log

import net.auoeke.limitless.Limitless
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.io.PrintStream
import java.time.LocalTime

// log4j does not display logger names in production.
object LimitlessLogger {
    private val logger = when {
        FabricLoader.getInstance().isDevelopmentEnvironment -> LogManager.getLogger("limitless")
        else -> null
    }

    fun info(message: String, vararg arguments: Any) {
        this.print(System.out, Level.INFO, message, *arguments)
    }

    fun warn(message: String, vararg arguments: Any) {
        this.print(System.out, Level.WARN, message, *arguments)
    }

    fun error(message: String, vararg arguments: Any) {
        this.print(System.err, Level.ERROR, message, *arguments)
    }

    private fun print(stream: PrintStream, level: Level, message: String, vararg arguments: Any) {
        if (this.logger === null) {
            stream.printf("[${LocalTime.now().withNano(0)}] [${Thread.currentThread().name}/$level] (${Limitless.ID}) ${message.formatted(arguments)}%n")
        } else {
            this.logger.printf(level, message, *arguments)
        }
    }
}
