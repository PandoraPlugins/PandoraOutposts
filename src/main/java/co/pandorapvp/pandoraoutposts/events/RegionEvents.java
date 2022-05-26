package co.pandorapvp.pandoraoutposts.events;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import co.pandorapvp.pandoraoutposts.bossbars.BossBar;
import co.pandorapvp.pandoraoutposts.bossbars.BossBarManager;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.Optional;

public class RegionEvents implements Listener {
    private final PandoraOutposts outposts = PandoraOutposts.getPlugin(PandoraOutposts.class);

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {

        final ProtectedRegion region = event.getRegion();
        final String containsFlagStr = (String) region.getFlag(outposts.outpostFlag);
        boolean containsFlag = Optional.of(Boolean.parseBoolean(containsFlagStr)).orElse(false);
        if (containsFlag) {

            final Player player = event.getPlayer();
            final String name = region.getId();
            try{
                final BossBar newBar = BossBarManager.createNewBar(name, player.getLocation(), 100, "Outpost 1");
                newBar.addPlayerToBar(player);
            }catch(KeyAlreadyExistsException err){
                final BossBar bossBar = BossBarManager.getBossBarMap().get(name);
                bossBar.addPlayerToBar(player);
            }


        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent event){
        final ProtectedRegion region = event.getRegion();
        final String containsFlagStr = (String) region.getFlag(outposts.outpostFlag);
         boolean containsFlag = Optional.of(Boolean.parseBoolean(containsFlagStr)).orElse(false);

        if(containsFlag){
            final String name = region.getId();
            System.out.println("name = " + name);
            BossBarManager.getBossBarMap().get(name).removeBarForPlayer(event.getPlayer());
        }

    }

}
