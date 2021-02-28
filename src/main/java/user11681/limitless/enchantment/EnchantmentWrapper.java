package user11681.limitless.enchantment;

import com.biom4st3r.moenchantments.api.EnchantmentSkeleton;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import user11681.limitless.asm.mixin.enchantment.EnchantmentAccess;
import user11681.limitless.config.LimitlessConfiguration;

public class EnchantmentWrapper extends Enchantment {
    public static final String INTERNAL_NAME = "user11681/limitless/enchantment/EnchantmentWrapper";

    private static final boolean moEnchantments = FabricLoader.getInstance().isModLoaded("moenchantments");

    private final Enchantment delegate;

    public int maxLevel;
    public boolean useGlobalMaxLevel;

    public EnchantmentWrapper(Enchantment delegate) {
        super(delegate.getRarity(), delegate.type, ((EnchantmentAccess) delegate).slotTypes());

        this.delegate = delegate;

        if (delegate.getMaxLevel() == 1) {
            this.maxLevel = 1;
        } else {
            int minLevel = delegate.getMinLevel();
            int maxIterations = Math.min(1000, delegate.getMaxLevel() - minLevel);
            int previousPower = delegate.getMinPower(minLevel);

            for (int i = 1; i <= maxIterations; i++) {
                int power = delegate.getMinPower(minLevel + i);

                if (previousPower < power) {
                    this.maxLevel = moEnchantments && this.delegate instanceof EnchantmentSkeleton ? Byte.MAX_VALUE : Integer.MAX_VALUE;

                    return;
                }

                previousPower = power;
            }

            this.maxLevel = delegate.getMaxLevel();
        }
    }

    public int originalMaxLevel() {
        return this.delegate.getMaxLevel();
    }

    @Override
    public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
        return this.delegate.getEquipment(entity);
    }

    @Override
    public Rarity getRarity() {
        return this.delegate.getRarity();
    }

    @Override
    public int getMinLevel() {
        return this.delegate.getMinLevel();
    }

    @Override
    public int getMaxLevel() {
        if (this.useGlobalMaxLevel) {
            return LimitlessConfiguration.instance.enchantment.globalMaxLevel;
        }

        return this.maxLevel == Integer.MIN_VALUE ? this.delegate.getMaxLevel() : this.maxLevel;
    }

    @Override
    public int getMinPower(int level) {
        return this.delegate.getMinPower(level);
    }

    @Override
    public int getMaxPower(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        return this.delegate.getProtectionAmount(level, source);
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return this.delegate.getAttackDamage(level, group);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (other instanceof EnchantmentWrapper) {
            other = ((EnchantmentWrapper) other).delegate;
        }

        return ((EnchantmentAccess) this.delegate).invokeCanAccept(other);
    }

    @Override
    protected String getOrCreateTranslationKey() {
        return ((EnchantmentAccess) this.delegate).invokeGetOrCreateTranslationKey();
    }

    @Override
    public String getTranslationKey() {
        return this.delegate.getTranslationKey();
    }

    @Override
    public Text getName(int level) {
        return this.delegate.getName(level);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return this.delegate.isAcceptableItem(stack);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        this.delegate.onTargetDamaged(user, target, level);
    }

    @Override
    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
        this.delegate.onUserDamaged(user, attacker, level);
    }

    @Override
    public boolean isTreasure() {
        return this.delegate.isTreasure();
    }

    @Override
    public boolean isCursed() {
        return this.delegate.isCursed();
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return this.delegate.isAvailableForEnchantedBookOffer();
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return this.delegate.isAvailableForRandomSelection();
    }
}
