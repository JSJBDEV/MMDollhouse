package ace.actually.mixin;

import ace.actually.MMDollhouse;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootMixin {
    @Shadow @Nullable protected RegistryKey<LootTable> lootTable;

    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Shadow public abstract ItemStack getStack(int slot);

    @Inject(method = "checkUnlocked", at = @At("HEAD"))
    private void loot(PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
    {
        if (this.lootTable!= null)
        {
            Random random = player.getRandom();
            if(random.nextBoolean())
            {
                if(this.getStack(0).isEmpty())
                {
                    this.setStack(0,new ItemStack(MMDollhouse.DOLLHOUSE.asItem()));
                }

            }


        }

    }
}
