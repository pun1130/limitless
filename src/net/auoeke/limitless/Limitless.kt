package net.auoeke.limitless

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.config.ExcludingNonprovider
import net.auoeke.limitless.config.enchantment.provider.EnchantmentListProvider
import net.fabricmc.api.*
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.item.ItemStack
import java.lang.reflect.Modifier

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer::class)
object Limitless : ModInitializer, ClientModInitializer {
    const val ID: String = "limitless"
    val forConflictRemoval: ReferenceOpenHashSet<ItemStack> = ReferenceOpenHashSet()

    override fun onInitialize() {
        Configuration.holder = AutoConfig.register(Configuration::class.java) {definition, configClass -> GsonConfigSerializer(definition, configClass)}
        Configuration.refresh()
        CommandRegistrationCallback.EVENT.register(LimitlessCommand)
    }

    @Environment(EnvType.CLIENT)
    override fun onInitializeClient() {
        AutoConfig.getGuiRegistry(Configuration::class.java).apply {
            registerPredicateProvider(EnchantmentListProvider) {field -> field.name == "maxLevels"}
            registerPredicateProvider(ExcludingNonprovider) {field -> field.modifiers and (Modifier.STATIC or Modifier.TRANSIENT or 0x1000 /*synthetic*/) != 0}
        }
    }

    @Environment(EnvType.CLIENT)
    class ModMenu : ModMenuApi {
        override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory {parent -> AutoConfig.getConfigScreen(Configuration::class.java, parent).get()}
    }
}
