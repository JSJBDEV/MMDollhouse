package ace.actually.blocks;

import ace.actually.MMDollhouse;
import ace.actually.schema.MysteryGenerator;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.sgui.api.gui.SimpleGuiBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class DecisionBlock extends Block implements PolymerBlock {
    public DecisionBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(player instanceof ServerPlayerEntity spe)
        {
            NbtCompound data = spe.getServer().getDataCommandStorage().get(MMDollhouse.DATA);
            NbtCompound players = data.getCompoundOrEmpty("players");
            String seed = players.getString(player.getUuidAsString()).get();
            NbtCompound mystery = data.getCompoundOrEmpty("mysteries").getCompoundOrEmpty(seed);


            NbtCompound choice = new NbtCompound();

            SimpleGuiBuilder builder = new SimpleGuiBuilder(ScreenHandlerType.GENERIC_9X4,false);
            builder.setTitle(Text.of("Decision Maker"));
            for (int i = 0; i < MysteryGenerator.MOBS.length; i++) {
                ItemStack skull = new ItemStack(Items.SKELETON_SKULL);
                skull.set(DataComponentTypes.ITEM_NAME,Text.of(MysteryGenerator.MOBS[i]));
                final int j = i;
                builder.setSlot(i,skull,((i1, clickType, slotActionType) -> choice.putString("target",MysteryGenerator.MOBS[j])));
            }
            for (int i = 0; i < MysteryGenerator.DANGEROUS; i++) {
                ItemStack stack = new ItemStack(MysteryGenerator.WEAPONS[i]);
                final int j = i;
                builder.setSlot(i+9,stack,((i1, clickType, slotActionType) -> choice.putString("weapon",MysteryGenerator.WEAPONS[j].getTranslationKey())));
            }
            for (int i = 0; i < MysteryGenerator.ROOMS.length; i++) {
                ItemStack block = new ItemStack(Items.OAK_PLANKS);
                block.set(DataComponentTypes.ITEM_NAME,Text.of(MysteryGenerator.ROOMS[i]));
                final int j = i;
                builder.setSlot(i+18,block,((i1, clickType, slotActionType) -> choice.putString("room",MysteryGenerator.ROOMS[j])));
            }
            ItemStack lime = new ItemStack(Items.LIME_BANNER);
            lime.set(DataComponentTypes.ITEM_NAME,Text.of("Confirm Choice").copy().setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            builder.setSlot(35,lime,((i, clickType, slotActionType) ->
            {
                if(mystery.getString("mob").get().equals(choice.getString("target").get()))
                {
                    if(mystery.getString("weapon").get().equals(choice.getString("weapon").get()))
                    {
                        if(mystery.getString("room").get().equals(choice.getString("room").get()))
                        {
                            player.sendMessage(Text.of("You Win!"),false);
                        }
                    }
                }
                int[] out = mystery.getIntArray("exit").get();
                spe.teleport(spe.getServer().getOverworld(),out[0],out[1],out[2], PositionFlag.ROT,0,0,true);
            }));
            builder.build(spe).open();
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.GLASS.getDefaultState();
    }
}
