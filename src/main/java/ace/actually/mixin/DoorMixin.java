package ace.actually.mixin;

import ace.actually.MMDollhouse;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public class DoorMixin {
	@Inject(at = @At("HEAD"), method = "onUse",cancellable = true)
	private void init(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(!world.isClient && world.getRegistryKey()== MMDollhouse.HOUSES)
		{
			if(world.getBlockState(pos.offset(state.get(DoorBlock.FACING))).isAir())
			{
				player.sendMessage(Text.of("It seems there isn't anything useful beyond that door..."),true);
				cir.setReturnValue(ActionResult.PASS);
			}
		}
	}
}