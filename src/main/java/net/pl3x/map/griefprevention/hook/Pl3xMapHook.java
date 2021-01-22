package net.pl3x.map.griefprevention.hook;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.griefprevention.Pl3xmapGriefprevention;
import net.pl3x.map.griefprevention.task.Pl3xMapTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pl3xMapHook {
    private final Pl3xmapGriefprevention plugin;
    private final Map<UUID, Pl3xMapTask> provider = new HashMap<>();

    public Pl3xMapHook(Pl3xmapGriefprevention plugin) {
        this.plugin = plugin;

        Pl3xMapProvider.get().mapWorlds().forEach(world -> {
            if (plugin.getGPHook().isWorldEnabled(world.uuid())) {
                SimpleLayerProvider provider = SimpleLayerProvider.builder("GriefPrevention").showControls(true).defaultHidden(false).build();
                world.layerRegistry().register(Key.of("griefprevention_" + world.uuid()), provider);
                Pl3xMapTask task = new Pl3xMapTask(plugin, world, provider);
                task.runTaskTimerAsynchronously(plugin, 0, 20 * 300);
                this.provider.put(world.uuid(), task);
            }
        });
    }

    public void disable() {
        provider.values().forEach(Pl3xMapTask::disable);
        provider.clear();
    }
}