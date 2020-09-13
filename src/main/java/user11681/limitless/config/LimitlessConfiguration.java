package user11681.limitless.config;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.stream.Collectors;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Category;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.limitless.Limitless;

@Config(name = Limitless.ID)
@Background("textures/block/andesite.png")
public class LimitlessConfiguration implements ConfigData {
    // an empty default category is made even with @Exclude
    @Category("enchantment")
    @Excluded
    private static final String ENCHANTMENT = "enchantment";

    @Category(ENCHANTMENT)
    @Excluded
    public static LimitlessConfiguration instance;

    @Category(ENCHANTMENT)
    public int globalMaxLevel = Integer.MAX_VALUE;

    @Category(ENCHANTMENT)
    public ReferenceArrayList<ConfigEnchantmentEntry> maxLevels = Registry.ENCHANTMENT.getIds().stream().sorted().map((final Identifier identifier) -> new ConfigEnchantmentEntry(identifier, false)).collect(Collectors.toCollection(ReferenceArrayList::new));
}
