package user11681.limitless.enchantment;

import net.minecraft.entity.player.PlayerEntity;

public class ExperienceUtil {
    public static void addExperienceLevelsNormalized(PlayerEntity player, int levels) {
        addExperienceLevelsRelatively(player, 0, levels);
    }

    public static void addExperienceLevelsRelatively(PlayerEntity player, int offset, int levels) {
        if (player.experienceLevel <= offset) {
            player.addExperienceLevels(levels);
        } else if (levels > 0) {
            for (levels = offset - levels; levels != offset; levels++) {
                player.addExperience((int) fromPreviousLevel(levels));
            }
        } else for (levels = offset + levels; levels != offset; levels++) {
            player.addExperience((int) -fromPreviousLevel(-levels));
        }
    }

    public static int normalizedCost(PlayerEntity player, int levels) {
        return relativeCost(player, 0, levels);
    }

    public static int relativeCost(PlayerEntity player, int offset, int levels) {
        final int level = player.experienceLevel;

        return level > offset
            ? level - ExperienceUtil.toLevel(ExperienceUtil.getCurrentExperience(player) - ExperienceUtil.difference(offset - levels, offset))
            : level - levels;
    }

    public static long getCurrentExperience(PlayerEntity player) {
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

    public static long normalizedExperience(int level, double progress) {
        return normalizedExperience(level) + (long) (progress * normalizedExperience(level + 1));
    }

    public static long normalizedExperience(int level) {
        return difference(0, level);
    }

    public static long fromPreviousLevel(int level) {
        return difference(level - 1, level);
    }

    public static long toNextLevel(int level) {
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

    public static int toLevel(long experience) {
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

        return 0;
    }
}
