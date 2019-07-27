package pw.kitl.bound_hotbar.mixin;

import net.minecraft.container.Slot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import pw.kitl.bound_hotbar.SlotInvId;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
// Mixins HAVE to be written in java due to constraints in the mixin system.
public abstract class SlotMixin implements SlotInvId {
	@Shadow @Final
	private int invSlot;
	@Shadow @Final
	public Inventory inventory;
	@Shadow
	public abstract ItemStack getStack();
	
	@Inject(at = @At("TAIL"), method = "canTakeItems", cancellable = true)
	private void binding_hotbar(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
		// hotbar and shield slot
		if (inventory instanceof PlayerInventory && (invSlot < 9 || invSlot == 40)) {
			ItemStack item = this.getStack();
			if (!item.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(item)) {
				info.setReturnValue(false);
			}
		}
	}
	
	public int getInvSlot() {
		return invSlot;
	}
}
