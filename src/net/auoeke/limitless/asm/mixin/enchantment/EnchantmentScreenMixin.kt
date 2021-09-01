package net.auoeke.limitless.asm.mixin.enchantment

import net.auoeke.limitless.asm.mixin.access.EnchantmentScreenHandlerAccess
import net.auoeke.limitless.config.Configuration
import net.auoeke.limitless.enchantment.ExperienceUtil
import net.auoeke.limitless.player
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.EnchantmentScreenHandler
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyVariable
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.LocalCapture
import kotlin.math.max

@Mixin(EnchantmentScreen::class)
internal abstract class EnchantmentScreenMixin(handler: EnchantmentScreenHandler, inventory: PlayerInventory, title: Text) : HandledScreen<EnchantmentScreenHandler>(handler, inventory, title) {
    @Unique
    private var backgroundEntryID = 0

    @Unique
    private var renderEntryID = 0

    @ModifyVariable(method = ["drawBackground"], at = At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;setZOffset(I)V"), index = 18)
    fun captureBackgroundEntryID(ID: Int): Int = ID.also {this.backgroundEntryID = it}

    @Inject(method = ["drawBackground"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I")], locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    fun showNormalizedCost(matrixes: MatrixStack, delta: Float, mouseX: Int, mouseY: Int, info: CallbackInfo, i: Int, j: Int) {
        val normalization = Configuration.instance.enchantment.normalization

        if (normalization.enabled && !player.abilities.creativeMode && player.experienceLevel > max(normalization.offset, this.handler.enchantmentPower[this.backgroundEntryID])) {
            val relative = ExperienceUtil.relativeCost(player, normalization.offset, this.backgroundEntryID + 1)
            val string = "($relative)"
            val x = (i + 78).toFloat()
            val y = (j + 19 * (this.backgroundEntryID + 1)).toFloat()

            val (color, outline) = when {
                this.handler.lapisCount >= this.backgroundEntryID + 1 -> 0xC8FF8F to 0x2D2102
                else -> 0x8C605D to 0x47352F
            }

            this.textRenderer.apply {
                draw(matrixes, string, x - 1, y, outline)
                draw(matrixes, string, x, y - 1, outline)
                draw(matrixes, string, x + 1, y, outline)
                draw(matrixes, string, x, y + 1, outline)
                draw(matrixes, string, x, y, color)
            }
        }
    }

    @ModifyVariable(method = ["render"], at = At(value = "FIELD", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;enchantmentPower:[I", ordinal = 0), ordinal = 3)
    fun captureEntryID(iteration: Int): Int = iteration.also {this.renderEntryID = it}

    @Redirect(method = ["render"], at = At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    fun revealEnchantments(enchantments: MutableList<Any>, enchantmentText: Any): Boolean {
        if (Configuration.instance.enchantment.revealEnchantments) {
            (this.handler as EnchantmentScreenHandlerAccess).invokeGenerateEnchantments(this.handler.getSlot(0).stack, this.renderEntryID, this.handler.enchantmentPower[this.renderEntryID]).withIndex().forEach {(index, enchantment) ->
                enchantments.add(index, enchantment.enchantment.getName(enchantment.level))
            }
        } else {
            enchantments.add(enchantmentText)
        }

        return true
    }
}
