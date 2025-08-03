package ace.actually.blocks;

import ace.actually.MMDollhouse;
import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class ClueBlock extends Block implements PolymerHeadBlock {

    public ClueBlock(Settings settings) {
        super(settings);
    }

    @Override
    public String getPolymerSkinValue(BlockState blockState, BlockPos blockPos, PacketContext packetContext) {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTViNTJhNWJhNDdiNDg3YTRlYTcyM2NjZjQwNGIzM2FjOWVkODA0MjhjNjI2YzA5OWViZWU0YmI3ZTZmNjM2MyJ9fX0=";
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(world instanceof ServerWorld serverWorld)
        {
            NbtCompound data = serverWorld.getServer().getDataCommandStorage().get(MMDollhouse.DATA);
            NbtCompound players = data.getCompoundOrEmpty("players");
        }
        return super.onUse(state, world, pos, player, hit);
    }
}
