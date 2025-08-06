package ace.actually.items;

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
        if(block instanceof PolymerHeadBlock phb)
        {
            String value = phb.getPolymerSkinValue(block.getDefaultState(), BlockPos.ORIGIN,context);
            item = PolymerUtils.createPlayerHead(value);
        }
        item.set(DataComponentTypes.ITEM_NAME,block.getName());

        return item;
    }
}
