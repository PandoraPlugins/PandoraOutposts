package co.pandorapvp.pandoraoutposts.commands;

import co.pandorapvp.pandoraoutposts.PandoraOutposts;
import co.pandorapvp.pandoraoutposts.outposts.Outpost;
import me.nanigans.libnanigans.Files.JsonUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class RefreshConfig implements CommandExecutor {

    private final static PandoraOutposts plugin = PandoraOutposts.getPlugin(PandoraOutposts.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("outpostrefresh")){
            if(sender instanceof Player && !sender.hasPermission("outpost.refresh")) return false;
            plugin.outpostConfig = new JsonUtil(plugin, "config.json");
            Outpost.setDurationTillNeutral(plugin.outpostConfig.get("durations.tillNeutral"));
            Outpost.setDurationTillNotNeutral(plugin.outpostConfig.get("durations.tillNotNeutral"));

            sender.sendMessage(ChatColor.GREEN+"Configuration successfully reloaded!");

            return true;

        }

        return false;
    }
}
