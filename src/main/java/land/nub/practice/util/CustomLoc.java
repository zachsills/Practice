package land.nub.practice.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

public class CustomLoc {

    @Getter @Setter private long time;
    @Getter @Setter private String world;
    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;

    public CustomLoc(String world, double x, double y, double z, float yaw, float pitch) {
        this.time = System.currentTimeMillis();
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public boolean equals(CustomLoc other) {
        if (!getWorld().equals(other.getWorld())) return false;
        if (getX() != other.getX()) return false;
        if (getY() != other.getY()) return false;
        if (getZ() != other.getZ()) return false;
        if (getYaw() != other.getYaw()) return false;
        if (getPitch() != other.getPitch()) return false;
        return true;
    }

    public static CustomLoc fromBukkit(Location location, World world) {
        if (location != null) {
            return new CustomLoc(world.getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }
        return null;
    }

    public Location toBukkit(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public double distanceXZ(CustomLoc location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.z - location.z, 2.0));
    }

    public double distance(CustomLoc location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.y - location.y, 2.0) + Math.pow(this.z - location.z, 2.0));
    }

    @Override
    public CustomLoc clone() {
        try {
            return (CustomLoc) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public CustomLoc add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
}