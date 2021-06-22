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

@SuppressWarnings("ConstantConditions")
@Mixin(EnchantmentScreen.class)
abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {
    @Unique
    private int backgroundEntryID;

    @Unique
    private int renderEntryID;

    public EnchantmentScreenMixin(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @ModifyVariable(method = "drawBackground",
                    at = @At(value = "INVOKE",
                             target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;setZOffset(I)V"),
                    index = 17)
    public int captureBackgroundEntryID(int ID) {
        return this.backgroundEntryID = ID;
    }

    @ModifyVariable(method = "drawBackground",
                    at = @At(value = "STORE"),
                    ordinal = 0)
    public String showNormalizedCost(String level) {
        PlayerEntity player = this.client.player;
        EnchantmentNormalizationEntry normalization = LimitlessConfiguration.instance.enchantment.normalization;

        if (normalization.enabled && normalization.display != CostDisplay.NORMAL && !player.getAbilities().creativeMode && player.experienceLevel > 30) {
            int relative = ExperienceUtil.relativeCost(player, normalization.offset, this.backgroundEntryID + 1);

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
    public int captureEntryID(int iteration) {
        return this.renderEntryID = iteration;
    }

    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                       ordinal = 0))
    public boolean revealEnchantments(List<Object> enchantments, Object enchantmentText) {
        if (LimitlessConfiguration.instance.enchantment.revealEnchantments) {
            int index = 0;

            for (EnchantmentLevelEntry enchantment : ((EnchantmentScreenHandlerAccess) this.handler).invokeGenerateEnchantments(this.handler.getSlot(0).getStack(), this.renderEntryID, this.handler.enchantmentPower[this.renderEntryID])) {
                enchantments.add(index++, enchantment.enchantment.getName(enchantment.level));
            }
        } else {
            enchantments.add(enchantmentText);
        }

        return true;
    }
}
