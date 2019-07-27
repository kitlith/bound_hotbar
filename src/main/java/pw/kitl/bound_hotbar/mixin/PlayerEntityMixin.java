package pw.kitl.bound_hotbar.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import pw.kitl.bound_hotbar.SlotInvId;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Shadow @Final
	public PlayerInventory inventory;
	@Shadow @Final
	public PlayerContainer playerContainer;
	@Shadow
	public abstract boolean isCreative();

	@Inject(at = @At("HEAD"), method = "dropSelectedItem", cancellable = true)
	public void cancel_drop_if_bound(boolean boolean_1, CallbackInfoReturnable<ItemEntity> info) {
		Boolean canTakeItems = playerContainer.slotList.stream()
			.filter(slot -> ((SlotInvId)slot).getInvSlot() == inventory.selectedSlot)
			.findAny()
			.map(slot -> slot.canTakeItems((PlayerEntity)(LivingEntity)this))
			.orElse(false);
		if (canTakeItems) {
			info.cancel();
			info.setReturnValue(null);
			// this should always be called from the server side.
			ServerPlayerEntity player = ((ServerPlayerEntity)(LivingEntity)this);
			// is there a better way to do this than sending the whole inventory?
			player.networkHandler.sendPacket(new InventoryS2CPacket(playerContainer.syncId, playerContainer.getStacks()));
		}
	}
}
