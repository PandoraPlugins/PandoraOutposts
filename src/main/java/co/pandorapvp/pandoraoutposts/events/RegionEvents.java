package co.pandorapvp.pandoraoutposts.events;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import co.pandorapvp.pandoraoutposts.bossbars.BossBar;
import co.pandorapvp.pandoraoutposts.bossbars.BossBarManager;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
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
        Optional<Boolean> containsFlag = Optional.of(Optional.of(Boolean.parseBoolean(containsFlagStr)).orElse(false));
        if (containsFlag.get()) {

            final Player player = event.getPlayer();
            final String name = region.getType().name();
            try{
                final BossBar newBar = BossBarManager.createNewBar(name, player.getLocation(), 100, "Outpost 1");
                newBar.addPlayerToBar(player);
            }catch(KeyAlreadyExistsException err){
                final BossBar bossBar = BossBarManager.getBossBarMap().get(name);
                bossBar.addPlayerToBar(player);
            }


        }
    }

}
