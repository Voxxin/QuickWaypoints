package cat.ella.quickwaypoints;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Waypoint {
    public final String icon;
    public final Vec3 blockPosition;
    public int renderTicks;
    public Vec2 screenPosition;

    public Waypoint(String icon, Vec3 blockPosition, int renderTicks) {
        this.icon = icon;
        this.blockPosition = blockPosition;
        this.renderTicks = renderTicks;
    }

    public Waypoint(String icon, Vec3 blockPosition) {
        this(icon, blockPosition, -1);
    }


}