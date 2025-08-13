package ace.actually.items;

import ace.actually.blocks.ClueBlock;
import ace.actually.blocks.DecisionBlock;
import ace.actually.blocks.DollhouseBlock;
import ace.actually.blocks.SafeBlock;
import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.packettweaker.PacketContext;

public class PolymerBlockItem extends BlockItem implements PolymerItem {
    Block block;

    public PolymerBlockItem(Block block, Settings settings) {
        super(block, settings);
        this.block=block;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.STICK;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack item = new ItemStack(Items.STICK);
        if(block instanceof ClueBlock)
        {
            item.set(DataComponentTypes.ITEM_MODEL, Identifier.of("mmdollhouse","clue"));
        }
        if(block instanceof SafeBlock)
        {
            item.set(DataComponentTypes.ITEM_MODEL, Identifier.of("mmdollhouse","safe"));
        }
        if(block instanceof DecisionBlock)
        {
            item.set(DataComponentTypes.ITEM_MODEL, Identifier.of("mmdollhouse","decision"));
        }
        if(block instanceof DollhouseBlock)
        {
            item.set(DataComponentTypes.ITEM_MODEL, Identifier.of("mmdollhouse","dollhouse"));
        }
        item.set(DataComponentTypes.ITEM_NAME, Text.translatable(block.getTranslationKey()));

        return item;
    }
}
