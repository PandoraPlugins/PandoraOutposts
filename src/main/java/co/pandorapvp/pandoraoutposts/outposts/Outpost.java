package co.pandorapvp.pandoraoutposts.outposts;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.libnanigans.Files.JsonUtil;

public class Outpost {

    private final static PandoraOutposts plugin = PandoraOutposts.getPlugin(PandoraOutposts.class);
    private final ProtectedRegion region;

    public Outpost(ProtectedRegion region) {
        this.region = region;
//        plugin.outpostConfig.get("durationTillNormal");
    }
}
