package pw.kitl.bound_hotbar.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import pw.kitl.bound_hotbar.SlotInvId;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile) {
		super(clientWorld, gameProfile);
	}

	@Inject(at = @At("HEAD"), method = "dropSelectedItem", cancellable = true)
	public void client_cancel_drop_if_bound(boolean boolean_1, CallbackInfoReturnable<ItemEntity> info) {
		Boolean canTakeItems = playerContainer.slotList.stream()
				.filter(slot -> ((SlotInvId)slot).getInvSlot() == inventory.selectedSlot)
				.findAny()
				.map(slot -> slot.canTakeItems((PlayerEntity)this))
				.orElse(false);
		if (canTakeItems) {
			info.cancel();
			info.setReturnValue(null);
		}
	}
}