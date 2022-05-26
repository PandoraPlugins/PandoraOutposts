package co.pandorapvp.pandoraoutposts.types;

public enum OutpostTypes {

    NORMAL("normal"),
    RAIDING("raiding");

    public final String label;

    private OutpostTypes(String label){
        this.label = label;
    }

}
