package co.pandorapvp.pandoraoutposts.outposts;

import java.util.TimerTask;

public class StageTimer extends TimerTask {

    private final Outpost outpost;

    public StageTimer(Outpost outpost){
        this.outpost = outpost;
    }

    @Override
    public void run() {

        System.out.println("hello");
        this.outpost.getBossBar().updateText("Claimed");
        this.outpost.nextStage();

    }
}
