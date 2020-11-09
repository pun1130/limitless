package user11681.limitless.asm.mixin.enchantment;

import java.util.List;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.limitless.asm.mixin.access.EnchantmentScreenHandlerAccess;
import user11681.limitless.config.LimitlessConfiguration;
import user11681.limitless.config.common.CostDisplay;
import user11681.limitless.config.enchantment.entry.normalization.EnchantmentNormalizationEntry;
import user11681.limitless.enchantment.ExperienceUtil;

@Mixin(EnchantmentScreen.class)
abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {
    @Unique
    private int backgroundEntryID;

    @Unique
    private int renderEntryID;

    public EnchantmentScreenMixin(final EnchantmentScreenHandler handler, final PlayerInventory inventory, final Text title) {
        super(handler, inventory, title);
    }

    @ModifyVariable(method = "drawBackground",
                    at = @At(value = "STORE"),
                    ordinal = 0)
    public String showNormalizedCost(final String level) {
        final PlayerEntity player = this.playerInventory.player;
        final EnchantmentNormalizationEntry normalization = LimitlessConfiguration.instance.enchantment.normalization;
        final int cost = this.backgroundEntryID + 1;

        this.backgroundEntryID = cost % 3;

        if (normalization.enabled && normalization.display != CostDisplay.NORMAL && !player.abilities.creativeMode && player.experienceLevel > 30) {
            final int relative = ExperienceUtil.relativeCost(player, normalization.offset, cost);

            if (normalization.display == CostDisplay.REPLACE) {
                return Integer.toString(relative);
            }

            return String.format("%s (%s)", level, relative);
        }

        return level;
    }

    @ModifyVariable(method = "render",
                    at = @At(value = "FIELD",
                             target = "Lnet/minecraft/screen/EnchantmentScreenHandler;enchantmentPower:[I",
                             ordinal = 0),
                    ordinal = 3)
    public int captureEntryID(final int iteration) {
        return this.renderEntryID = iteration;
    }

    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                       ordinal = 0))
    public <E> boolean revealEnchantments(final List enchantments, final E enchantmentText) {
        if (LimitlessConfiguration.instance.enchantment.revealEnchantments) {
            int index = 0;

            for (final EnchantmentLevelEntry enchantment : ((EnchantmentScreenHandlerAccess) this.handler).invokeGenerateEnchantments(this.handler.getSlot(0).getStack(), this.renderEntryID, this.handler.enchantmentPower[this.renderEntryID])) {
                enchantments.add(index++, enchantment.enchantment.getName(enchantment.level));
            }
        } else {
            enchantments.add(enchantmentText);
        }

        return true;
    }
}
