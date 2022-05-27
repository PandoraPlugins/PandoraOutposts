package co.pandorapvp.pandoraoutposts.outposts;

import java.util.TimerTask;

public class StageTimer extends TimerTask {

    private final Outpost outpost;

    public StageTimer(Outpost outpost){
        this.outpost = outpost;
    }

    @Override
    public void run() {

        this.outpost.getBossBar().updateText("Claimed");

    }
}
