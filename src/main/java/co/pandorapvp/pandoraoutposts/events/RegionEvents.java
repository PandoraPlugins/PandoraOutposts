package co.pandorapvp.pandoraoutposts.events;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import co.pandorapvp.pandoraoutposts.bossbars.BossBar;
import co.pandorapvp.pandoraoutposts.bossbars.BossBarManager;
import co.pandorapvp.pandoraoutposts.outposts.Outpost;
import com.massivecraft.factions.FPlayers;
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

import java.util.Map;

public class RegionEvents implements Listener {
    private final PandoraOutposts outposts = PandoraOutposts.getPlugin(PandoraOutposts.class);

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {

        final ProtectedRegion region = event.getRegion();
        final String outpostType = (String) region.getFlag(outposts.outpostFlag);

        if (outpostType != null) {//todo: figure out raiding outposts

            final Player player = event.getPlayer();
            final String name = region.getId();
            final World world = player.getWorld();

            final Map<String, BossBar> bossBarMap = BossBarManager.getBossBarMap();

            if (bossBarMap.containsKey(name)) {
                final BossBar bossBar = bossBarMap.get(name);
                bossBar.addPlayerToBar(player);
            } else {
                final Location centerOfRegion = getCenterOfRegion(region, world).subtract(0, 100, 0);
                final BossBar newBar = BossBarManager.createNewBar(name, centerOfRegion, 100, "Neutral");
                newBar.addPlayerToBar(player);
            }

            final boolean playerInFaction = FPlayers.getInstance().getByPlayer(player).hasFaction();
            if (!playerInFaction) return;
            final Outpost outpost = Outpost.get(name, world);
            if (outpost != null) {
                outpost.startNextCountdown(player);
            }

        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent event) {
        final ProtectedRegion region = event.getRegion();
        final String outpostType = (String) region.getFlag(outposts.outpostFlag);

        if (outpostType != null) {
            final String name = region.getId();
            final Player player = event.getPlayer();
            final World world = player.getWorld();

            BossBarManager.getBossBarMap().get(name).removeBarForPlayer(player);
            final Outpost outpost = Outpost.get(name, world);
            final boolean playerInFaction = FPlayers.getInstance().getByPlayer(player).hasFaction();

            if (outpost != null && outpost.getPlayers().size() > 0 && playerInFaction) {
                System.out.println(5);
                outpost.playerLeave(player);
//                outpost.startNextCountdown(player);
            }
        }

    }

    public static Location getCenterOfRegion(ProtectedRegion region, World world) {
        final com.sk89q.worldedit.BlockVector maximumPoint = region.getMaximumPoint();
        final BlockVector minimumPoint = region.getMinimumPoint();
        final Vector centerLoc = maximumPoint.subtract(minimumPoint).divide(2).add(minimumPoint);
        return new Location(world, centerLoc.getX(), centerLoc.getY(), centerLoc.getZ());
    }

}
