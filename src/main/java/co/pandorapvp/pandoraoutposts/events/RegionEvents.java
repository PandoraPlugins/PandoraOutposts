package co.pandorapvp.pandoraoutposts.events;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import co.pandorapvp.pandoraoutposts.bossbars.BossBar;
import co.pandorapvp.pandoraoutposts.bossbars.BossBarManager;
import co.pandorapvp.pandoraoutposts.outposts.Outpost;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.management.openmbean.KeyAlreadyExistsException;

public class RegionEvents implements Listener {
    private final PandoraOutposts outposts = PandoraOutposts.getPlugin(PandoraOutposts.class);

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {

        final ProtectedRegion region = event.getRegion();
        final String outpostType = (String) region.getFlag(outposts.outpostFlag);

        if (outpostType != null) {//todo: figure out raiding outposts

            final Player player = event.getPlayer();
            final String name = region.getId();
            try{
                final World world = player.getWorld();
                final Location centerOfRegion = getCenterOfRegion(region, world).subtract(0, 100, 0);
                final BossBar newBar = BossBarManager.createNewBar(name, centerOfRegion, 100, "Outpost 1");
                newBar.addPlayerToBar(player);
                final Outpost outpost = Outpost.get(name, world);

                if(outpost != null)
                    outpost.startNeutralCountdown();

            }catch(KeyAlreadyExistsException err){
                final BossBar bossBar = BossBarManager.getBossBarMap().get(name);
                bossBar.addPlayerToBar(player);
            }

        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent event){
        final ProtectedRegion region = event.getRegion();
        final String outpostType = (String) region.getFlag(outposts.outpostFlag);

        if(outpostType != null){
            final String name = region.getId();
            BossBarManager.getBossBarMap().get(name).removeBarForPlayer(event.getPlayer());
        }

    }

    public static Location getCenterOfRegion(ProtectedRegion region, World world){
        final com.sk89q.worldedit.BlockVector maximumPoint = region.getMaximumPoint();
        final BlockVector minimumPoint = region.getMinimumPoint();
        final Vector centerLoc = maximumPoint.subtract(minimumPoint).divide(2).add(minimumPoint);
        return new Location(world, centerLoc.getX(), centerLoc.getY(), centerLoc.getZ());
    }

}
