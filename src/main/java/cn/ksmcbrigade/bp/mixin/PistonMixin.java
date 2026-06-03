package cn.ksmcbrigade.bp.mixin;

import cn.ksmcbrigade.bp.BetterPiston;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonBaseBlock.class)
public class PistonMixin {
    @Inject(method = "isPushable",at = @At("HEAD"),cancellable = true)
    private static void is(BlockState p_60205_, Level p_60206_, BlockPos p_60207_, Direction p_60208_, boolean p_60209_, Direction p_60210_, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(!(p_60205_.getBlock() instanceof MovingPistonBlock));
    }

    @Inject(
            method = "moveBlocks",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z")
    )
    private void captureNBT(Level level, BlockPos pos, Direction dir, boolean extending, CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide) return;
        PistonStructureResolver handler = new PistonStructureResolver(level, pos, dir, extending);
        if (!handler.resolve()) {
            return;
        }

        List<BlockPos> movedBlocks = handler.getToPush();
        for (BlockPos srcPos : movedBlocks) {
            BlockEntity be = level.getBlockEntity(srcPos);
            if (be != null) {
                CompoundTag nbt = be.saveWithFullMetadata();
                BetterPiston.ORIGINAL_POS_NBT_MAP.put(srcPos, nbt);

                Clearable.tryClear(be);
                level.removeBlockEntity(srcPos);
            }
        }
    }
}
