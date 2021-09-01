package net.auoeke.limitless.enchantment

import net.minecraft.entity.player.PlayerEntity
import kotlin.math.max
import kotlin.math.min

@Suppress("NAME_SHADOWING")
object ExperienceUtil {
    private val PlayerEntity.currentExperience: Long get() = normalizedExperience(this.experienceLevel) + (toNextLevel(this.experienceLevel) * this.experienceProgress.toDouble()).toLong()

    fun addExperienceLevelsRelatively(player: PlayerEntity, offset: Int, levels: Int) {
        var levels = levels

        when {
            player.experienceLevel <= offset -> player.addExperienceLevels(levels)
            levels > 0 -> {
                levels = offset - levels

                while (levels != offset) {
                    player.addExperience(this.fromPreviousLevel(levels).toInt())
                    levels++
                }
            }
            else -> {
                levels += offset

                while (levels != offset) {
                    player.addExperience((-this.fromPreviousLevel(-levels)).toInt())
                    levels++
                }
            }
        }
    }

    fun addExperienceLevelsNormalized(player: PlayerEntity, levels: Int) = this.addExperienceLevelsRelatively(player, 0, levels)

    fun relativeCost(player: PlayerEntity, offset: Int, levels: Int): Int {
        val level = player.experienceLevel

        return when {
            level > offset -> level - this.toLevel(player.currentExperience - this.difference(offset - levels, offset))
            else -> level - levels
        }
    }

    fun normalizedCost(player: PlayerEntity, levels: Int): Int = this.relativeCost(player, 0, levels)

    fun normalizedDifference(from: Int, to: Int): Long {
        var from = from
        var to = to

        if (from < 0) {
            from = -from
        }

        if (to < 0) {
            to = -to
        }

        return when {
            from > to -> {
                this.difference(from - to, 0)
            }
            else -> this.difference(0, to - from)
        }
    }

    fun normalizedExperience(level: Int, progress: Double): Long = this.normalizedExperience(level) + (progress * this.normalizedExperience(level + 1)).toLong()

    private fun normalizedExperience(level: Int): Long = this.difference(0, level)
    private fun fromPreviousLevel(level: Int): Long = this.difference(level - 1, level)
    private fun toNextLevel(level: Int): Long = this.difference(level, level + 1)

    private fun difference(from: Int, to: Int): Long {
        var from = from
        var to = to

        if (from == to) {
            return 0
        }

        if (from < 0) {
            from = -from
        }

        if (to < 0) {
            to = -to
        }

        if (from > to) {
            return this.difference(to, from)
        }

        var toMin: Int
        var levels: Long
        var total: Long = 0

        if (from < 15) {
            toMin = min(to, 15)
            levels = (toMin - from).toLong()
            total = levels * (from + toMin + 6)

            if (to <= 15) {
                return total
            }
        }

        if (from < 30) {
            val fromMax = max(from, 15)
            toMin = min(to, 30)
            levels = (toMin - fromMax).toLong()
            total += 5 * (levels * (fromMax + toMin - 1) / 2) - 38 * levels

            if (to <= 30) {
                return total
            }
        }

        from = max(from, 30)
        levels = (to - from).toLong()

        return total + 9 * (levels * (from + to - 1) / 2) - 158 * levels
    }

    private fun toLevel(experience: Long): Int {
        if (experience == 0L) {
            return 0
        }

        if (experience < 0) {
            return -this.toLevel(-experience)
        }

        var normalizedExperience: Long

        for (i in 1 until Int.MAX_VALUE) {
            normalizedExperience = this.normalizedExperience(i)

            if (normalizedExperience > experience) {
                return i - 1
            }
        }

        return 0
    }
}
