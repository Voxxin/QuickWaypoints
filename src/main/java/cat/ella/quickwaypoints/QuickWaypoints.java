package cat.ella.quickwaypoints;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.phys.Vec3;

public class QuickWaypoints implements ModInitializer {
    public static final WaypointHandler waypoints = new WaypointHandler();
    Waypoint currentWaypoint = new Waypoint("textures/block/dirt.png", new Vec3(0.5, -58.5, 0.5));


    @Override
    public void onInitialize() {
        waypoints.addWaypoint(currentWaypoint);
    }
}
