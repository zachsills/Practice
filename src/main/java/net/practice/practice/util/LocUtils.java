package net.practice.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocUtils {

    public static String serializeLocation(final Location location) {
        return new StringBuilder()
                .append("@w;" + location.getWorld().getName())
                .append(":@x;" + location.getX())
                .append(":@x;" + location.getX())
                .append(":@y;" + location.getY())
                .append(":@z;" + location.getZ())
                .append(":@p;" + location.getPitch())
                .append(":@ya;" + location.getYaw())
                .toString();
    }

    public static Location deserializeLocation(final String string) {
        final Location location = new Location(Bukkit.getWorlds().get(0), 0.0D, 0.0D, 0.0D);
        final String[] arr = string.split(":");
        final int len = arr.length;

        for(int i = 0; i < len; ++i) {
            final String attribute = arr[i];
            final String[] split = attribute.split(";");
            if(split[0].equalsIgnoreCase("@w"))
                location.setWorld(Bukkit.getWorld(split[1]));

            if(split[0].equalsIgnoreCase("@x"))
                location.setX(Double.parseDouble(split[1]));

            if(split[0].equalsIgnoreCase("@y"))
                location.setY(Double.parseDouble(split[1]));

            if(split[0].equalsIgnoreCase("@z"))
                location.setZ(Double.parseDouble(split[1]));

            if(split[0].equalsIgnoreCase("@p"))
                location.setPitch(Float.parseFloat(split[1]));

            if(split[0].equalsIgnoreCase("@ya"))
                location.setYaw(Float.parseFloat(split[1]));
        }

        return location;
    }
}
