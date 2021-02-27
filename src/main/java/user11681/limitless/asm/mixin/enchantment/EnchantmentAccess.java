package user11681.limitless.asm.mixin.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Enchantment.class)
public interface EnchantmentAccess {
    @Accessor("slotTypes")
    EquipmentSlot[] slotTypes();

    @Invoker("canAccept")
    boolean invokeCanAccept(Enchantment other);

    @Invoker("getOrCreateTranslationKey")
    String invokeGetOrCreateTranslationKey();
}
