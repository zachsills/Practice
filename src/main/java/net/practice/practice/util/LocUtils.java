package net.practice.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocUtils {

    public static String serializeLocation(Location location) {
        // This is so that we can see which part is null.
        String world = location.getWorld() != null ? location.getWorld().getName() : "null";
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return "@w;" + world
                + ":@x;" + x
                + ":@y;" + y
                + ":@z;" + z
                + ":@ya;" + yaw
                + ":@p;" + pitch;
    }

    public static String serializeLocation(CustomLoc location) {
        // This is so that we can see which part is null.
        String world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return "@w;" + world
                + ":@x;" + x
                + ":@y;" + y
                + ":@z;" + z
                + ":@ya;" + yaw
                + ":@p;" + pitch;
    }

    public static Location deserializeLocation(final String string) {
        final Location location = new Location(Bukkit.getWorlds().get(0), 0.0D, 0.0D, 0.0D);
        final String[] arr = string.split(":");
        final int len = arr.length;

        for (int i = 0; i < len; ++i) {
            final String attribute = arr[i];
            final String[] split = attribute.split(";");
            if(split[0].equalsIgnoreCase("@w"))
                location.setWorld(Bukkit.getServer().getWorld(split[1]));

            if(split[0].equalsIgnoreCase("@x"))
                location.setX(Double.parseDouble(split[1]));

            if(split[0].equalsIgnoreCase("@y"))
                location.setY(Double.parseDouble(split[1]));

            if(split[0].equalsIgnoreCase("@z"))
                location.setZ(Double.parseDouble(split[1]));

            if(split[0].equalsIgnoreCase("@ya"))
                location.setYaw(Float.parseFloat(split[1]));

            if(split[0].equalsIgnoreCase("@p"))
                location.setPitch(Float.parseFloat(split[1]));
        }

        return location;
    }
}
