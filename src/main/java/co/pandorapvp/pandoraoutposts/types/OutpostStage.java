package co.pandorapvp.pandoraoutposts.types;

public enum OutpostStage {

    NEUTRAL("Neutral"),
    CLAIMED("Claimed");

    public String type;
    private OutpostStage(String type) {
        this.type = type;
    }
}
