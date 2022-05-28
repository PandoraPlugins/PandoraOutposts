package co.pandorapvp.pandoraoutposts.outposts;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import co.pandorapvp.pandoraoutposts.bossbars.BossBar;
import co.pandorapvp.pandoraoutposts.bossbars.BossBarManager;
import co.pandorapvp.pandoraoutposts.types.OutpostStage;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Outpost {

    private final static Map<String, Outpost> outposts = new HashMap<>();
    private final static PandoraOutposts plugin = PandoraOutposts.getPlugin(PandoraOutposts.class);
    private static final long durationTillNeutral = plugin.outpostConfig.get("durations.tillNeutral");
    private static final long durationTillNotNeutral = plugin.outpostConfig.get("durations.tillNotNeutral");
    private final ProtectedRegion region;
    private final BossBar bossBar;
    private OutpostStage state = OutpostStage.NEUTRAL;
    private Timer runningTimer;
    private Faction factionClaimed;

    public Outpost(ProtectedRegion region) {
        this.region = region;
        bossBar = BossBarManager.getBossBarMap().get(region.getId());
        outposts.put(region.getId(), this);
    }

    /**
     * Checks if the members inside the outpost region are all part of the same function
     * @return false if everyone is in the same faction. True if there is at least one person not in the same faction.
     * This will ignore people who are not in a faction. If the only person in the outpost is not in a faction, this will return true to ignore them.
     */
    public boolean doesOutpostContainDiffFactionMembers(){
        final Map<UUID, Player> players = this.getPlayers();
        System.out.println("players = " + players);
        final Player firstPlayer = players.values().iterator().next();
        final FPlayers instance = FPlayers.getInstance();
        final String factionName = instance.getByPlayer(firstPlayer).getFaction().getId();
        return players.values().stream().anyMatch(player -> {
            final FPlayer fPlayer = instance.getByPlayer(player);
            if(players.size() == 1 && !fPlayer.hasFaction()) return true;
            else if(!fPlayer.hasFaction()) return false;
            final String fName = fPlayer.getFaction().getId();
            return !fName.equals(factionName);
        });
    }

    public void startNeutralCountdown(){

        if(this.doesOutpostContainDiffFactionMembers()) return;

        final StageTimer stageTimer = new StageTimer(this);
        final Timer timer = new Timer();
        this.runningTimer = timer;
        timer.schedule(stageTimer, durationTillNeutral*100);

    }

    private void setFactionClaimed(){
        final Map<UUID, Player> players = this.getPlayers();
        if(players.isEmpty()) return;

        final UUID firstUUID = players.keySet().iterator().next();
        if(firstUUID == null) return;

        final Player player = players.get(firstUUID);
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        this.factionClaimed = fPlayer.getFaction();
        this.setState(OutpostStage.CLAIMED);
    }

    public void nextStage(){
        switch (this.state) {
            case NEUTRAL:
                this.setFactionClaimed();
                break;
            case CLAIMED:
                this.factionClaimed = null;
                this.setState(OutpostStage.NEUTRAL);
        }
    }

    public void setState(OutpostStage state) {
        this.state = state;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public Map<UUID, Player> getPlayers(){
        return this.bossBar.getPlayers();
    }

    public static Outpost get(String regionName, World world){
        final Outpost outpost = outposts.get(regionName);
        if(outpost != null){
            return outpost;
        }else {
            final Map<String, BossBar> bossBarMap = BossBarManager.getBossBarMap();
            final ProtectedRegion region = plugin.worldGuardPlugin.getRegionManager(world).getRegion(regionName);
            if (ProtectedRegion.isValidId(regionName) && region != null) {
                final Outpost outpost1 = new Outpost(region);
                outposts.put(regionName, outpost1);
                return outpost1;
            }
        }
        return null;
    }
}
