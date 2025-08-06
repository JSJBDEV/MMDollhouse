package ace.actually.blocks;

import ace.actually.schema.MysteryGenerator;
import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class DollhouseBlock extends Block implements PolymerHeadBlock {
    public DollhouseBlock(Settings settings) {
        super(settings);
    }

    @Override
    public String getPolymerSkinValue(BlockState blockState, BlockPos blockPos, PacketContext packetContext) {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjgxZWYyM2VkN2RhN2VlMDQ4YThjNTk5OGEwMWUwZDNkNTM1N2Q0MjZhMThhYzllOTYxM2E1ZGQ1MzMzZWJkNCJ9fX0=";
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!world.isClient)
        {
            MysteryGenerator.generateMystery(pos.asLong(), (ServerPlayerEntity) player);
        }
        return super.onUse(state, world, pos, player, hit);
    }

}
