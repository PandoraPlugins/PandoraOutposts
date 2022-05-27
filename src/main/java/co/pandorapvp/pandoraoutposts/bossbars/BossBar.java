package co.pandorapvp.pandoraoutposts.bossbars;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar {

    private final Map<UUID, Player> players = new HashMap<>();
    private final EntityEnderDragon dragon;
    private final PacketPlayOutSpawnEntityLiving packet;
    private final String name;

    public BossBar(String name, Location loc, float healthPercent, String text) {
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

        EntityEnderDragon dragon = new EntityEnderDragon(world);
        dragon.setInvisible(true);
        dragon.setLocation(loc.getX(), loc.getY() - 100, loc.getZ(), 0, 0);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);

        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) 0x20);
        watcher.a(6, (healthPercent * 200) / 100);
        watcher.a(10, text);
        watcher.a(2, text);
        watcher.a(11, (byte) 1);
        watcher.a(3, (byte) 1);

        try {
            Field t = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            t.setAccessible(true);
            t.set(packet, watcher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.dragon = dragon;
        this.packet = packet;
        this.name = name;

    }


    public void addPlayerToBar(Player p) {
        final UUID uniqueId = p.getUniqueId();
        if (!this.players.containsKey(uniqueId)) {
            this.players.put(uniqueId, p);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(this.packet);
        }
    }

    public void removeBarForPlayer(Player p) {
        if (this.players.containsKey(p.getUniqueId())) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.dragon.getId());
            this.players.remove(p.getUniqueId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            if (this.players.isEmpty())
                BossBarManager.getBossBarMap().remove(this.name);
        }
    }

    public void deleteBar() {
        this.players.forEach((id, p) -> this.removeBarForPlayer(p));
        BossBarManager.getBossBarMap().remove(this.name);
    }

//    public static void teleportBar(Player p) {
//        if(dragons.containsKey(p.getName())) {
//            Location loc = p.getLocation();
//            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(dragons.get(p.getName()).getId(),
//                    (int) loc.getX() * 32, (int) (loc.getY() - 100) * 32, (int) loc.getZ() * 32,
//                    (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360), false);
//            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
//        }
//    }

    public void updateText(String text) {
        this.updateBar(text, -1);
    }

    public void updateHealth(float healthPercent) {
        this.updateBar(null, healthPercent);
    }

    public void updateBar(String text, float healthPercent) {
        final DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) 0x20);
        if (healthPercent != -1) watcher.a(6, (healthPercent * 200) / 100);
        if (text != null) {
            watcher.a(10, text);
            watcher.a(2, text);
        }
        watcher.a(11, (byte) 1);
        watcher.a(3, (byte) 1);

        final PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.dragon.getId(), watcher, true);
        this.players.forEach((id, player) -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    public Map<UUID, Player> getPlayers() {
        return players;
    }
}
