package net.auoeke.limitless.transform.mixin.enchantment;

import java.util.List;
import net.auoeke.limitless.transform.mixin.access.EnchantmentScreenHandlerAccess;
import net.auoeke.limitless.config.Configuration;
import net.auoeke.limitless.enchantment.ExperienceUtil;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
                    index = 18)
    public int captureBackgroundEntryID(int ID) {
        return this.backgroundEntryID = ID;
    }

    @Inject(method = "drawBackground",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void showNormalizedCost(MatrixStack matrixes, float delta, int mouseX, int mouseY, CallbackInfo info, int i, int j) {
        var normalization = Configuration.instance.enchantment.getNormalization();

        if (normalization.getEnabled() && !this.client.player.getAbilities().creativeMode && this.client.player.experienceLevel > Math.max(normalization.getOffset(), this.handler.enchantmentPower[this.backgroundEntryID])) {
            int relative = ExperienceUtil.INSTANCE.relativeCost(this.client.player, normalization.getOffset(), this.backgroundEntryID + 1);
            String string = "(%s)".formatted(Integer.toString(relative));
            int x = i + 78;
            int y = j + 19 * (this.backgroundEntryID + 1);
            int color;
            int outline;

            if (this.handler.getLapisCount() >= this.backgroundEntryID + 1) {
                color = 0xC8FF8F;
                outline = 0x2D2102;
            } else {
                color = 0x8C605D;
                outline = 0x47352F;
            }

            this.textRenderer.draw(matrixes, string, x - 1, y, outline);
            this.textRenderer.draw(matrixes, string, x, y - 1, outline);
            this.textRenderer.draw(matrixes, string, x + 1, y, outline);
            this.textRenderer.draw(matrixes, string, x, y + 1, outline);
            this.textRenderer.draw(matrixes, string, x, y, color);
        }
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
        if (Configuration.instance.enchantment.getRevealEnchantments()) {
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
