package net.pl3x.map.griefprevention.hook;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Core;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Collection;
import java.util.UUID;

public class GDHook {
    public static boolean isWorldEnabled(UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        return GriefDefender.getCore().isEnabled(world);
    }

    public static Collection<Claim> getClaims() {
        return GriefDefender.getCore().getAllClaims();
    }
}
