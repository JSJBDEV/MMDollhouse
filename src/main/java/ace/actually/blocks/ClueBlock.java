package ace.actually.blocks;

import ace.actually.MMDollhouse;
import ace.actually.schema.MysteryGenerator;
import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ClueBlock extends Block implements PolymerHeadBlock {
    public static IntProperty room = IntProperty.of("room",0, MysteryGenerator.ROOMS.length);

    public ClueBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(room));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(room,0);
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
            String seed = players.getString(player.getUuidAsString()).get();
            NbtCompound mystery = data.getCompoundOrEmpty("mysteries").getCompoundOrEmpty(seed);
            NbtList notes = mystery.getListOrEmpty("notes");

            NbtList rooms = mystery.getListOrEmpty("rooms");
            //TODO: There is a more efficient way to do this, requires rework
            String absRoom = MysteryGenerator.ROOMS[state.get(ClueBlock.room)];
            int relativeRoom = IntStream.range(0, rooms.size()).filter(i -> rooms.getString(i).get().equals(absRoom)).findFirst().orElse(-1);

            //the safe finds the notes forwards, the clue block finds the notes backwards
            for (int i = notes.size(); i > 0; i--) {
                String split = notes.getString(i).get().split("£")[0];
                if(Integer.parseInt(split)==relativeRoom)
                {
                    player.sendMessage(MysteryGenerator.formatText(notes.getString(i).get()),false);
                    break;
                }

            }
        }

        return super.onUse(state, world, pos, player, hit);
    }
}
