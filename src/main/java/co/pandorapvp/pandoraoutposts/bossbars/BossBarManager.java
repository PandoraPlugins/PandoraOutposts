package co.pandorapvp.pandoraoutposts.bossbars;

import org.bukkit.Location;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {

    private static final Map<String, BossBar> bossBarMap = new ConcurrentHashMap<>();

    public static BossBar createNewBar(String regionName, Location loc, float healthPercent, String text){
        if(bossBarMap.containsKey(regionName))
            return bossBarMap.get(regionName);
        final BossBar bar = new BossBar(regionName, loc, healthPercent, text);
        bossBarMap.put(regionName, bar);
        return bar;
    }

    public static Map<String, BossBar> getBossBarMap() {
        return bossBarMap;
    }
}
