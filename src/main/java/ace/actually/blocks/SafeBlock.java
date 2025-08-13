package ace.actually.blocks;

import ace.actually.MMDollhouse;
import ace.actually.schema.MysteryGenerator;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.stream.IntStream;

public class SafeBlock extends Block implements BlockWithElementHolder, PolymerBlock {
    public SafeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(ClueBlock.room));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(ClueBlock.room,0);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        ItemStack stick = new ItemStack(Items.STICK);
        stick.set(DataComponentTypes.ITEM_MODEL, Identifier.of("mmdollhouse","safe"));

        ItemDisplayElement element = new ItemDisplayElement(stick);
        element.setBrightness(Brightness.FULL);
        ElementHolder holder = new ElementHolder();
        holder.addElement(element);
        return holder;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(player instanceof ServerPlayerEntity spe)
        {
            NbtCompound data = spe.getServer().getDataCommandStorage().get(MMDollhouse.DATA);
            NbtCompound players = data.getCompoundOrEmpty("players");
            String seed = players.getString(player.getUuidAsString()).get();
            NbtCompound mystery = data.getCompoundOrEmpty("mysteries").getCompoundOrEmpty(seed);
            NbtList passwords = mystery.getListOrEmpty("passwords");
            NbtList notes = mystery.getListOrEmpty("notes");
            NbtList rooms = mystery.getListOrEmpty("rooms");

            String absRoom = MysteryGenerator.ROOMS[state.get(ClueBlock.room)];
            int relativeRoom = IntStream.range(0, rooms.size()).filter(i -> rooms.getString(i).get().equals(absRoom)).findFirst().orElse(-1);

            AnvilInputGui inputGui = new AnvilInputGui(spe,false);
            inputGui.addSlot(new ItemStack(Items.TRIPWIRE_HOOK),((slot, clickType, slotActionType) ->
            {
                //this looks a tad jank, rooms and passwords are effectively the same list
                for (int i = 0; i < passwords.size(); i++) {

                    if(relativeRoom==i)
                    {
                        String[] split = passwords.getString(i).get().split(": ");
                        System.out.println(split[1]+" -> "+inputGui.getInput());
                        if(split[1].equals(inputGui.getInput()))
                        {
                            for (int j = 0; j < notes.size(); j++) {
                                if(Integer.parseInt(notes.getString(j).get().split("£")[0])==relativeRoom)
                                {
                                    player.sendMessage(MysteryGenerator.formatText(notes.getString(j).get()),false);
                                    player.sendMessage(MysteryGenerator.formatText(notes.getString(j+1).get()),false);
                                    inputGui.close(false);
                                    break;
                                }
                            }

                        }
                    }
                }
            }));
            inputGui.open();





        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.getDefaultState();
    }
}
