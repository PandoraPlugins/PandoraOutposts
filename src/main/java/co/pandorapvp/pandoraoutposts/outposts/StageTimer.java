package co.pandorapvp.pandoraoutposts.outposts;

import co.pandorapvp.pandoraoutposts.bossbars.BossBar;
import co.pandorapvp.pandoraoutposts.types.OutpostStage;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;


public class StageTimer extends BukkitRunnable {

    private final Outpost outpost;
    private final long secondsToLast;
    private int runTime = 0;

    public StageTimer(Outpost outpost, long secondsToLast){
        this.outpost = outpost;
        this.secondsToLast = secondsToLast;
    }

    @Override
    public void run() {
        if(runTime > secondsToLast){
            this.cancel();
            outpost.playSoundToPlayers(Sound.LEVEL_UP);
            this.outpost.nextStage();
        }else{
            outpost.playSoundToPlayers(Sound.CLICK);//todo: config the sounds
            if(outpost.getState() == OutpostStage.NEUTRAL)
                outpost.getBossBar().updateHealth( ((float) runTime / secondsToLast)*100);
            else outpost.getBossBar().updateHealth( ((float) (secondsToLast-runTime) / secondsToLast)*100);
            runTime++;
        }
    }

    /**
     * If the countdown gets interrupted because of a player event this will reset the boss bar
     */
    public void reset(){
        final BossBar bossBar = outpost.getBossBar();
        if(outpost.getState() == OutpostStage.NEUTRAL)
            bossBar.updateHealth(1);
        else bossBar.updateHealth(100);
        outpost.setState(outpost.getState());
        outpost.playSoundToPlayers(Sound.BLAZE_DEATH);
        super.cancel();
    }

}
