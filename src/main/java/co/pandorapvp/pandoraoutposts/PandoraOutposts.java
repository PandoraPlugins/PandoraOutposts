package co.pandorapvp.pandoraoutposts;

import co.pandorapvp.pandoraoutposts.events.RegionEvents;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.nanigans.libnanigans.Files.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class PandoraOutposts extends JavaPlugin {

    public WorldGuardPlugin worldGuardPlugin = getWorldGuard();
    public Flag outpostFlag;
    public YamlGenerator outPostYaml = new YamlGenerator(this, "Outposts/outPostYaml.yml");

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new RegionEvents(), this);
        try {
            YamlGenerator.createFolder(this, "Outposts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Pandora Outposts Loaded Successfully");
    }

    @Override
    public void onLoad() {

        final FlagRegistry flagRegistry = worldGuardPlugin.getFlagRegistry();
        try{
            final StringFlag isOutpostFlag = new StringFlag("is-outpost", "true");
            flagRegistry.register(isOutpostFlag);
        }catch(FlagConflictException e){
            e.printStackTrace();
            Flag<?> existing = flagRegistry.get("is-outpost");
            if(existing instanceof StateFlag){
                outpostFlag = existing;
            }else{
                Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD+""+ChatColor.RED+"Getting this error is bad news for the OUTPOST plugin, something is conflicing with the 'is-outpost' flag in worldguard");
            }
        }

    }

    public WorldGuardPlugin getWorldGuard(){
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if(!(plugin instanceof WorldGuardPlugin)) return null;
        return (WorldGuardPlugin) plugin;
    }

}

