package user11681.limitless.enchantment;

import net.minecraft.entity.player.PlayerEntity;

public class ExperienceUtil {
    public static void addExperienceLevelsNormalized(final PlayerEntity player, int levels) {
        addExperienceLevelsRelatively(player, 0, levels);
    }

    public static void addExperienceLevelsRelatively(final PlayerEntity player, final int offset, int levels) {
        if (player.experienceLevel <= offset) {
            player.addExperienceLevels(levels);
        } else {
            if (levels > 0) {
                levels += offset;

                for (int i = offset; i != levels; i++) {
                    player.addExperience((int) fromPreviousLevel(i));
                }
            } else {
                for (levels -= offset; levels != -offset; levels++) {
                    player.addExperience((int) -toNextLevel(levels));
                }
            }
        }
    }

    public static int normalizedCost(final PlayerEntity player, final int levels) {
        return relativeCost(player, 0, levels);
    }

    public static int relativeCost(final PlayerEntity player, final int offset, final int levels) {
        final int level = player.experienceLevel;

        if (level <= offset) {
            return level - levels;
        } else {
            return level - ExperienceUtil.toLevel(ExperienceUtil.getCurrentExperience(player) - ExperienceUtil.difference(offset, levels + offset));
        }
    }

    public static long getCurrentExperience(final PlayerEntity player) {
        return normalizedExperience(player.experienceLevel) + (long) (toNextLevel(player.experienceLevel) * (double) player.experienceProgress);
    }

    public static long normalizedDifference(int from, int to) {
        if (from < 0) {
            from = -from;
        }

        if (to < 0) {
            to = -to;
        }

        if (from > to) {
            return difference(from - to, 0);
        }

        return difference(0, to - from);
    }

    public static long normalizedExperience(final int level, final double progress) {
        return normalizedExperience(level) + (long) (progress * normalizedExperience(level + 1));
    }

    public static long normalizedExperience(int level) {
        return difference(0, level);
    }

    public static long fromPreviousLevel(final int level) {
        return difference(level - 1, level);
    }

    public static long toNextLevel(final int level) {
        return difference(level, level + 1);
    }

    public static long difference(int from, int to) {
        if (from == to) {
            return 0;
        }

        if (from < 0) {
            from = -from;
        }

        if (to < 0) {
            to = -to;
        }

        if (from > to) {
            return difference(to, from);
        }

        int toMin;
        long levels;
        long total = 0;

        if (from < 15) {
            toMin = Math.min(to, 15);
            levels = toMin - from;
            total = levels * (from + toMin + 6);

            if (to <= 15) {
                return total;
            }
        }

        if (from < 30) {
            final int fromMax = Math.max(from, 15);
            toMin = Math.min(to, 30);
            levels = toMin - fromMax;
            total += 5 * (levels * (fromMax + toMin - 1) / 2) - 38 * levels;

            if (to <= 30) {
                return total;
            }
        }

        from = Math.max(from, 30);
        levels = to - from;

        return total + 9 * (levels * (from + to - 1) / 2) - 158 * levels;
    }

    public static int toLevel(final long experience) {
        if (experience == 0) {
            return 0;
        }

        if (experience < 0) {
            return -toLevel(-experience);
        }

        long normalizedExperience;

        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            normalizedExperience = normalizedExperience(i);

            if (normalizedExperience > experience) {
                return i - 1;
            }
        }

        return (int) Float.NaN;
    }
}
