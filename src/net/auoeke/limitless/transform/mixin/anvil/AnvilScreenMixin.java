package net.auoeke.limitless.transform.mixin.anvil;

import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.config.common.CostDisplay;
import net.auoeke.limitless.enchantment.ExperienceUtil;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("ConstantConditions")
@Mixin(AnvilScreen.class)
abstract class AnvilScreenMixin extends HandledScreen<AnvilScreenHandler> {
    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @ModifyConstant(method = "drawForeground",
                    constant = @Constant(intValue = 40))
    public int modifyLimit(int limit) {
        return Configuration.instance.getAnvil().getLevelLimit();
    }

    @ModifyArg(method = "drawForeground",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/text/TranslatableText;<init>(Ljava/lang/String;[Ljava/lang/Object;)V"),
               index = 1)
    public Object[] showNormalizedCost(Object[] arguments) {
        var cost = this.handler.getLevelCost();
        var player = this.client.player;
        var normalization = Configuration.instance.getAnvil().getNormalization();

        if (normalization.getEnabled() && normalization.getDisplay() != CostDisplay.NORMAL && !player.getAbilities().creativeMode && player.experienceLevel > cost) {
            var relative = ExperienceUtil.INSTANCE.normalizedCost(player, cost);

            if (normalization.getDisplay() == CostDisplay.REPLACE) {
                arguments[0] = Integer.toString(relative);
            } else {
                arguments[0] += " (%s)".formatted(relative);
            }
        }

        return arguments;
    }
}
