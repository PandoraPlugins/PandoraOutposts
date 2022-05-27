package co.pandorapvp.pandoraoutposts;

import co.pandorapvp.pandoraoutposts.events.RegionEvents;
import co.pandorapvp.pandoraoutposts.types.OutpostTypes;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nanigans.libnanigans.Files.JsonUtil;
import me.nanigans.libnanigans.Files.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class PandoraOutposts extends JavaPlugin {

    public WorldGuardPlugin worldGuardPlugin = getWorldGuard();
    public Flag<?> outpostFlag;
    public YamlGenerator outpostYaml = new YamlGenerator(this, "outpostData.yml");
    public JsonUtil outpostConfig = new JsonUtil(this, "config.json");

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new RegionEvents(), this);
        try {
            YamlGenerator.createFolder(this, "Outposts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Pandora Outposts Loaded Successfully");
    }

    @Override
    public void onLoad() {
        outpostFlag = registerFlag("is-outpost", OutpostTypes.NORMAL.label);
    }

    private Flag<?> registerFlag(final String flagName, final String defaultVal){
        final FlagRegistry flagRegistry = worldGuardPlugin.getFlagRegistry();
        Flag<?> flag = null;
        try{
            final StringFlag isOutpostFlag = new StringFlag(flagName, defaultVal);
            flagRegistry.register(isOutpostFlag);
            flag = flagRegistry.get(flagName);
        }catch(FlagConflictException e){
            e.printStackTrace();
            Flag<?> existing = flagRegistry.get(flagName);
            if(existing instanceof StateFlag){
                flag = existing;
            }else{
                Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD+""+ChatColor.RED+"Getting this error is bad news for the OUTPOST plugin, something is conflicting with the "+ flagName +" flag in worldguard");
            }
        }
        return flag;
    }

    public WorldGuardPlugin getWorldGuard(){
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if(!(plugin instanceof WorldGuardPlugin)) return null;
        return (WorldGuardPlugin) plugin;
    }

    public static boolean regionContainsFlag(ProtectedRegion region, String name) {
        for (Map.Entry<Flag<?>, Object> flagObjectEntry : region.getFlags().entrySet()) {
            if (flagObjectEntry.getKey().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}

