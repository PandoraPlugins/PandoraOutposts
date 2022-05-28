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
import java.util.stream.Collectors;

public class Outpost {

    private final static Map<String, Outpost> outposts = new HashMap<>();
    private final static PandoraOutposts plugin = PandoraOutposts.getPlugin(PandoraOutposts.class);
    private static final long durationTillNeutral = plugin.outpostConfig.get("durations.tillNeutral");
    private static final long durationTillNotNeutral = plugin.outpostConfig.get("durations.tillNotNeutral");
    private final ProtectedRegion region;
    private final BossBar bossBar;
    private OutpostStage state = OutpostStage.NEUTRAL;
    private StageTimer runningTimer;
    private Faction factionClaimed;

    public Outpost(ProtectedRegion region) {
        this.region = region;
        bossBar = BossBarManager.getBossBarMap().get(region.getId());
        outposts.put(region.getId(), this);
    }

    /**
     * Checks if the members inside the outpost region are all part of the same function
     *
     * @return false if everyone is in the same faction. True if there is at least one person not in the same faction.
     * This will ignore people who are not in a faction. If the only person in the outpost is not in a faction, this will return true to ignore them.
     */
    public boolean doesOutpostContainDiffFactionMembers() {
        final FPlayers instance = FPlayers.getInstance();
        final List<Player> players = this.getPlayersInFaction();

        if (players.size() == 0) return true;//if nobody in here it's not valid to continue
        final Player firstPlayer = players.get(0);
        final FPlayer firstFacPlayer = instance.getByPlayer(firstPlayer);

        final String factionName = firstFacPlayer.getFaction().getId();

        return !players.stream().allMatch(player -> {
            final FPlayer fPlayer = instance.getByPlayer(player);
            if (!fPlayer.hasFaction()) return true;
            final String fName = fPlayer.getFaction().getId();
            return fName.equals(factionName);
        });
    }

    /**
     * Checks if any member inside the outpost region is not a member of the faction that has currently claimed it
     * @return true if there is at least one person who is not in the claimed faction false if everyone is part of the claimed faction
     */
    public boolean doesContainNonClaimedMembers() {

        final List<Player> playersInFaction = this.getPlayersInFaction();
        if (playersInFaction.size() == 0) return false;
        final FPlayers instance = FPlayers.getInstance();
        return !playersInFaction.stream().allMatch(player -> {
            final FPlayer byPlayer = instance.getByPlayer(player);
            if (!byPlayer.hasFaction()) return true;
            return byPlayer.getFaction().getId().equals(this.factionClaimed.getId());
        });

    }

    public void playerLeave(Player playerLeft) {
        final FPlayers instance = FPlayers.getInstance();
        final FPlayer fPlayer = instance.getByPlayer(playerLeft);
        final Faction faction = fPlayer.getFaction();
        if(runningTimer != null && this.getPlayersInFaction().isEmpty()) {
            runningTimer.cancel();
            runningTimer = null;
            return;
        }
        if (this.state == OutpostStage.CLAIMED && faction.getId().equals(this.factionClaimed.getId()) && this.doesContainNonClaimedMembers()) {
            this.startNeutralCountdown();
        }

        if(this.state == OutpostStage.NEUTRAL && !this.doesOutpostContainDiffFactionMembers()){
            this.startClaimedCountdown();
        }

    }

    public void playerEnter(Player playerTriggered) {

        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerTriggered);
        if(runningTimer != null && this.doesOutpostContainDiffFactionMembers()) {
            runningTimer.cancel();
            runningTimer = null;
        }
        switch (this.state) {
            case CLAIMED:
                if (fPlayer.hasFaction() && !fPlayer.getFaction().getId().equals(this.factionClaimed.getId())) {
                    this.startNeutralCountdown();
                }
                break;
            case NEUTRAL:
                this.startClaimedCountdown();
        }

    }

    private void startClaimedCountdown() {

        if (this.doesOutpostContainDiffFactionMembers()) return;
        final StageTimer stageTimer = new StageTimer(this, durationTillNotNeutral);
        this.runningTimer = stageTimer;
        stageTimer.runTaskTimerAsynchronously(plugin, 20, 20);

    }

    /**
     * Will start a countdown to go to neutral if the requirements are met to do so - everyone in region has same faction
     */
    private void startNeutralCountdown() {

        if (this.doesOutpostContainDiffFactionMembers()) return;

        final StageTimer stageTimer = new StageTimer(this, durationTillNeutral);
        this.runningTimer = stageTimer;
        stageTimer.runTaskTimerAsynchronously(plugin, 20, 20);

    }

    private void setFactionClaimed() {
        final Map<UUID, Player> players = this.getPlayers();
        if (players.isEmpty()) return;

        final UUID firstUUID = players.keySet().iterator().next();
        if (firstUUID == null) return;

        final Player player = players.get(firstUUID);
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        this.factionClaimed = fPlayer.getFaction();
        this.setState(OutpostStage.CLAIMED);
    }

    public void nextStage() {
        runningTimer = null;
        switch (this.state) {
            case NEUTRAL:
                this.setFactionClaimed();
                break;
            case CLAIMED:
                this.factionClaimed = null;
                this.setState(OutpostStage.NEUTRAL);
                this.startClaimedCountdown();
        }
    }

    private void setState(OutpostStage state) {
        final Faction factionClaimed = this.factionClaimed;
        this.bossBar.updateText(state.type +  (factionClaimed != null ? "Faction: " + factionClaimed.getTag() : ""));
        this.state = state;
    }

    public List<Player> getPlayersInFaction() {
        final FPlayers instance = FPlayers.getInstance();
        return this.getPlayers().values().stream()//filter out players without a faction
                .filter(player -> instance.getByPlayer(player).hasFaction()).collect(Collectors.toList());

    }

    public Map<UUID, Player> getPlayers() {
        return this.bossBar.getPlayers();
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public static Outpost get(String regionName, World world) {
        final Outpost outpost = outposts.get(regionName);
        if (outpost != null) {
            return outpost;
        } else {
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
