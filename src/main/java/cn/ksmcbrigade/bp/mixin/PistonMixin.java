package cn.ksmcbrigade.bp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonMixin {
    @Inject(method = "isPushable",at = @At("HEAD"),cancellable = true)
    private static void is(BlockState p_60205_, Level p_60206_, BlockPos p_60207_, Direction p_60208_, boolean p_60209_, Direction p_60210_, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!(p_60205_.getBlock() instanceof MovingPistonBlock));
    }
}
