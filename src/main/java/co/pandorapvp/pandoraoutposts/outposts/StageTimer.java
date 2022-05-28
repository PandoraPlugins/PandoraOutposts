package co.pandorapvp.pandoraoutposts.outposts;

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
        System.out.println("runTime = " + runTime);
        if(runTime > secondsToLast){
            this.cancel();
            this.outpost.nextStage();
        }else{
            System.out.println("((float) runTime) / secondsToLast = " + ((float) runTime) / secondsToLast);
            outpost.getBossBar().updateHealth( ((float) runTime / secondsToLast)*100);
            runTime++;
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }
}
