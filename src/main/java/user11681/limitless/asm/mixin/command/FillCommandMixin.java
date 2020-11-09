package user11681.limitless.asm.mixin.command;

import net.minecraft.server.command.FillCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import user11681.limitless.config.LimitlessConfiguration;

@Mixin(FillCommand.class)
abstract class FillCommandMixin {
    @ModifyConstant(method = "execute",
                    constant = @Constant(intValue = 1 << 15))
    private static int modifyLimit(final int previousLimit) {
        return LimitlessConfiguration.instance.command.fill.limit;
    }
}
