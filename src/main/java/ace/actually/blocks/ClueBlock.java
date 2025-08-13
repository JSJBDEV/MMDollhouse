package ace.actually.blocks;

import ace.actually.MMDollhouse;
import ace.actually.schema.MysteryGenerator;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.stream.IntStream;

public class ClueBlock extends Block implements BlockWithElementHolder, PolymerBlock {
    public static IntProperty room = IntProperty.of("room",0, MysteryGenerator.ROOMS.length);

    public ClueBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(room));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(room,0);
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

            if(player.getStackInHand(Hand.MAIN_HAND).isOf(Items.BRUSH))
            {
                NbtList activities = mystery.getListOrEmpty("activities");
                for (int i = 0; i < activities.size(); i++) {
                    if(activities.getString(i).get().contains(absRoom))
                    {
                        player.sendMessage(Text.of(MysteryGenerator.formatText(activities.getString(i).get())),false);
                    }
                }
                player.getStackInHand(Hand.MAIN_HAND).damage(5,player);
            }
            else
            {
                //the safe finds the notes forwards, the clue block finds the notes backwards
                boolean found = false;
                for (int i = notes.size()-1; i > 0; i--) {
                    String split = notes.getString(i).get().split("£")[0];
                    if(Integer.parseInt(split)==relativeRoom)
                    {
                        player.sendMessage(MysteryGenerator.formatText(notes.getString(i).get()),false);
                        found = true;
                        break;
                    }

                }
                if(!found)
                {
                    player.sendMessage(Text.of("You don't find anything useful in this room..."),false);
                }
            }


        }

        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        ItemStack stick = new ItemStack(Items.STICK);
        stick.set(DataComponentTypes.ITEM_MODEL, Identifier.of("mmdollhouse","clue"));

        ItemDisplayElement element = new ItemDisplayElement(stick);
        element.setBrightness(Brightness.FULL);
        ElementHolder holder = new ElementHolder();
        holder.addElement(element);
        return holder;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.getDefaultState();
    }
}
