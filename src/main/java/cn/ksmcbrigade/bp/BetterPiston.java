package cn.ksmcbrigade.bp;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod("bp")
public class BetterPiston {
    public static final Map<BlockPos, CompoundTag> ORIGINAL_POS_NBT_MAP = new HashMap<>();
}
