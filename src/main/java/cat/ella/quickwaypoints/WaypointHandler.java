package cat.ella.quickwaypoints;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class WaypointHandler {
    private final List<Waypoint> waypoints = new ArrayList<>();
    private final Minecraft minecraft = Minecraft.getInstance();

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public void renderOnScreen(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (minecraft.player == null) return;

        var pose = guiGraphics.pose();
        for (Waypoint waypoint : waypoints) {
            if (waypoint.screenPosition == null) continue;

            pose.pushPose();
            drawTexture(guiGraphics, ResourceLocation.withDefaultNamespace(waypoint.icon),
                    waypoint.screenPosition.x, waypoint.screenPosition.y,
                    16, 16,
                    0, 0,
                    16, 16,
                    16, 16);
            pose.popPose();
        }
    }

    public void calculateWaypointPosition(PoseStack poseStack, Matrix4f projectionMatrix, Camera camera) {
        var modelViewMatrix = poseStack.last().pose().invert();
        for (Waypoint waypoint : waypoints) {
            waypoint.screenPosition = project3Dto2D(waypoint.blockPosition, modelViewMatrix, projectionMatrix, camera);
        }
    }

    public @Nullable Vec2 project3Dto2D(Vec3 worldPosition, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Camera camera) {

        var cameraPosition = camera.getPosition();
        var cameraRotation = camera.rotation();
        var in3d = cameraPosition.subtract(worldPosition);

        var wnd = minecraft.getWindow();
        var quaternion = new Quaternionf((float) in3d.x, (float) in3d.y, (float) in3d.z, 1.f);

        Matrix4f m = modelViewMatrix.rotate(cameraRotation.invert()); // this
        var product = mqProduct(projectionMatrix, mqProduct(m, quaternion));
        modelViewMatrix.rotate(cameraRotation); // undo that

        if (product.w <= 0f) {
            return null;
        }

        var screenPos = qToScreen(product);
        var x = screenPos.x * wnd.getGuiScaledWidth();
        var y = screenPos.y * wnd.getGuiScaledHeight();

        if (Float.isInfinite(x) || Float.isInfinite(y)) {
            return null;
        }

        return new Vec2(x, y);
    }

    private static Quaternionf mqProduct(Matrix4f m, Quaternionf q) {
        return new Quaternionf(
                m.m00() * q.x + m.m10() * q.y + m.m20() * q.z + m.m30() * q.w,
                m.m01() * q.x + m.m11() * q.y + m.m21() * q.z + m.m31() * q.w,
                m.m02() * q.x + m.m12() * q.y + m.m22() * q.z + m.m32() * q.w,
                m.m03() * q.x + m.m13() * q.y + m.m23() * q.z + m.m33() * q.w);
    }

    private static Quaternionf qToScreen(Quaternionf q) {
        var w = 1f / q.w * 0.5f;

        return new Quaternionf(
                q.x * w + 0.5f,
                q.y * w + 0.5f,
                q.z * w + 0.5f,
                w);
    }

    private static void drawTexture(GuiGraphics context, ResourceLocation texture, float x1, float y1, float width, float height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        var x2 = x1 + width;
        var y2 = y1 + height;
        var z = 0.F;
        var u1 = (u + 0.0F) / (float) textureWidth;
        var u2 = (u + (float) regionWidth) / (float) textureWidth;
        var v1 = (v + 0.0F) / (float) textureHeight;
        var v2 = (v + (float) regionHeight) / (float) textureHeight;

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.disableCull();

        Matrix4f matrix4f = context.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, x1, y1, z).setUv(u1, v1);
        bufferBuilder.addVertex(matrix4f, x1, y2, z).setUv(u1, v2);
        bufferBuilder.addVertex(matrix4f, x2, y2, z).setUv(u2, v2);
        bufferBuilder.addVertex(matrix4f, x2, y1, z).setUv(u2, v1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.enableCull();
    }
}
