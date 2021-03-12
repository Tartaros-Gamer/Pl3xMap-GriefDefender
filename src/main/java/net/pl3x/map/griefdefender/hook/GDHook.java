package net.pl3x.map.griefdefender.hook;

import java.util.List;
import java.util.UUID;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class GDHook {
    public static boolean isWorldEnabled(UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        return GriefDefender.getCore().isEnabled(world.getUID());
    }

    public static List<Claim> getClaims() {
        return GriefDefender.getCore().getAllClaims();
    }
}
