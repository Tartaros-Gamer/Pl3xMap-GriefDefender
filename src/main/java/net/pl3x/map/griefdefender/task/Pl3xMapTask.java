package net.pl3x.map.griefdefender.task;

import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.api.marker.Rectangle;
import net.pl3x.map.griefdefender.configuration.Config;
import net.pl3x.map.griefdefender.hook.GDHook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Pl3xMapTask extends BukkitRunnable {
    private final MapWorld world;
    private final SimpleLayerProvider provider;

    private boolean stop;

    public Pl3xMapTask(MapWorld world, SimpleLayerProvider provider) {
        this.world = world;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }
        updateClaims();
    }

    void updateClaims() {
        provider.clearMarkers(); // TODO track markers instead of clearing them
        Collection<Claim> topLevelClaims = GDHook.getClaims();
        if (topLevelClaims != null) {
            topLevelClaims.stream()
                    .filter(claim -> claim.getWorldUniqueId().equals(this.world.uuid()))
                    .filter(claim -> claim.getParent() == null)
                    .forEach(this::handleClaim);
        }
    }

    private void handleClaim(Claim claim) {
        Vector3i min = claim.getLesserBoundaryCorner();
        Vector3i max = claim.getGreaterBoundaryCorner();
        if (min == null) {
            return;
        }

        Rectangle rect = Marker.rectangle(Point.of(min.getX(), min.getZ()), Point.of(max.getX() + 1, max.getZ() + 1));

        List<UUID> builders = claim.getUserTrusts(TrustTypes.BUILDER);
        List<UUID> containers = claim.getUserTrusts(TrustTypes.CONTAINER);
        List<UUID> accessors = claim.getUserTrusts(TrustTypes.ACCESSOR);
        List<UUID> managers = claim.getUserTrusts(TrustTypes.MANAGER);

        World claimWorld = Bukkit.getWorld(claim.getWorldUniqueId());
        String worldName = claimWorld.getName();

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.STROKE_COLOR)
                .strokeWeight(Config.STROKE_WEIGHT)
                .strokeOpacity(Config.STROKE_OPACITY)
                .fillColor(Config.FILL_COLOR)
                .fillOpacity(Config.FILL_OPACITY)
                .clickTooltip((claim.isAdminClaim() ? Config.ADMIN_CLAIM_TOOLTIP : Config.CLAIM_TOOLTIP)
                        .replace("{world}", worldName)
                        .replace("{uuid}", claim.getUniqueId().toString())
                        .replace("{owner}", claim.getOwnerName())
                        .replace("{managers}", getNames(managers))
                        .replace("{builders}", getNames(builders))
                        .replace("{containers}", getNames(containers))
                        .replace("{accessors}", getNames(accessors))
                        .replace("{area}", Integer.toString(claim.getArea()))
                        .replace("{width}", Integer.toString(claim.getWidth()))
                        .replace("{height}", Integer.toString(claim.getHeight()))
                );

        if (claim.isAdminClaim()) {
            options.strokeColor(Color.BLUE).fillColor(Color.BLUE);
        }

        rect.markerOptions(options);

        String markerid = "griefdefender_" + worldName + "_region_" + claim.getUniqueId();
        this.provider.addMarker(Key.of(markerid), rect);
    }

    private static String getNames(List<UUID> list) {
        List<String> names = new ArrayList<>();
        for (UUID str : list) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(str);
                names.add(offlinePlayer.getName());
            } catch (Exception e) {
                names.add(str.toString());
            }
        }
        return String.join(", ", names);
    }

    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}

