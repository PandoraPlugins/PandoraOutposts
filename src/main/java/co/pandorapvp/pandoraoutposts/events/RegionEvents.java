package co.pandorapvp.pandoraoutposts.events;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;

public class RegionEvents implements Listener {

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event){

        System.out.println(event.getPlayer());

    }

}
