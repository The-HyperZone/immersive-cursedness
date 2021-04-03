package nl.theepicblock.immersive_cursedness;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CursednessServer {
    private final MinecraftServer server;
    private volatile boolean isServerActive = true;
    private long nextTick;
    private int tickCount;
    private final Map<ServerPlayerEntity, PlayerManager> playerManagers = new HashMap<>();
    private final Config config = AutoConfig.getConfigHolder(Config.class).getConfig();

    public CursednessServer(MinecraftServer server) {
        this.server = server;
    }

    public void start() {
        System.out.println("Starting immersive cursedness thread");
        while (isServerActive) {
            try {
                tick();
                tickCount++;
                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println("Exception occurred whilst ticking the immersive cursedness thread");
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        System.out.println("Stopping immersive cursedness thread");
        isServerActive = false;
    }

    public void tick() {
        //Sync player managers
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();

        playerManagers.entrySet().removeIf(i -> !playerList.contains(i.getKey()));
        for (ServerPlayerEntity player : playerList) {
            if (!playerManagers.containsKey(player)) {
                playerManagers.put(player, new PlayerManager(player, config));
            }
        }

        //Tick player managers
        playerManagers.forEach((player, manager) -> {
            manager.tick(tickCount);
        });
    }

    public PlayerManager getManager(ServerPlayerEntity player) {
        return playerManagers.get(player);
    }
}
