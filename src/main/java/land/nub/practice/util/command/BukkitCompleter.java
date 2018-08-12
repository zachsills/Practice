package land.nub.practice.util.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command Framework - BukkitCompleter <br>
 * An implementation of the TabCompleter class allowing for multiple tab
 * completers per command
 *
 * @author minnymin3
 *
 */
public class BukkitCompleter implements TabCompleter {

    private Map<String, Map.Entry<Method, Object>> completers = new HashMap<>();

    public void addCompleter(final String label, final Method m, final Object obj) {
        completers.put(label, new AbstractMap.SimpleEntry<>(m, obj));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        for(int i = args.length; i >= 0; i--) {
            // OLD WAY - final StringBuffer buffer = new StringBuffer();
            final StringBuilder builder = new StringBuilder(); // SBuilder more efficient in single-threaded situations as SBuffer is synchronized
            builder.append(label.toLowerCase());
            for(int x = 0; x < i; x++) {
                if(!args[x].equals("") && !args[x].equals(" "))
                    builder.append("." + args[x].toLowerCase());
            }

            final String cmdLabel = builder.toString();
            if(completers.containsKey(cmdLabel)) {
                final Map.Entry<Method, Object> entry = completers.get(cmdLabel);
                try {
                    return (List<String>) entry.getKey().invoke(entry.getValue(),
                            new CommandArgs(sender, command, label, args, cmdLabel.split("\\.").length - 1));
                } catch(final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch(final IllegalAccessException e) {
                    e.printStackTrace();
                } catch(final InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
