package cn.ksmcbrigade.bp.mixin;

import cn.ksmcbrigade.bp.BetterPiston;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin extends BlockEntity {
    public PistonMovingBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.AFTER
            )
    )
    private static void onTickSetBlockState(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        if (level != null && !level.isClientSide && !blockEntity.isSourcePiston()) {
            pistonproplus$restoreNbt(level, pos, blockEntity);
        }
    }

    @Unique
    private static void pistonproplus$restoreNbt(Level level, BlockPos finalPos, @UnknownNullability PistonMovingBlockEntity pbe) {
        Direction moveDir = pbe.isExtending() ? pbe.getDirection() : pbe.getDirection().getOpposite();
        BlockPos originalPos = finalPos.relative(moveDir.getOpposite());

        CompoundTag nbtToRestore = BetterPiston.ORIGINAL_POS_NBT_MAP.remove(originalPos);
        System.out.println("nbtToRestore" + nbtToRestore);
        if (nbtToRestore == null) {
            return;
        }

        BlockEntity targetBe = level.getBlockEntity(finalPos);
        if (targetBe != null && !(targetBe instanceof PistonMovingBlockEntity)) {
            targetBe.load(nbtToRestore);
            targetBe.setChanged();

            BlockState currentState = level.getBlockState(finalPos);
            level.sendBlockUpdated(finalPos, currentState, currentState, 3);
        }
    }
}