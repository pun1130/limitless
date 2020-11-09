package user11681.limitless.asm.mixin.command;

import net.minecraft.server.command.EffectCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import user11681.limitless.config.LimitlessConfiguration;

@Mixin(EffectCommand.class)
abstract class EffectCommandMixin {
    @ModifyConstant(method = "register",
                    constant = @Constant(intValue = 1000000))
    private static int modifyDurationLimit(final int original) {
        return LimitlessConfiguration.instance.command.effect.durationLimit;
    }

    @ModifyConstant(method = "register",
                    constant = @Constant(intValue = 255))
    private static int modifyAmplifierLimit(final int original) {
        return LimitlessConfiguration.instance.command.effect.amplifierLimit;
    }
}
