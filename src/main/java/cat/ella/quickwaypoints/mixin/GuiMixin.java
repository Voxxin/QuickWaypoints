package cat.ella.quickwaypoints.mixin;

import cat.ella.quickwaypoints.QuickWaypoints;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(at = @At("TAIL"), method = "render")
    private void onRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // Equivalent of using 'HudRenderCallback.EVENT.register((context, tickDelta)' with FabricAPI.
        QuickWaypoints.waypoints.renderOnScreen(guiGraphics, deltaTracker);
    }
}