package net.streamlinecloud.mc.paper.task;

import net.streamlinecloud.mc.SpigotSCP;
import net.streamlinecloud.mc.common.utils.StaticCache;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class StopCountdownTask {

    List<Integer> warningsSent = new ArrayList<>();

    public StopCountdownTask() {

        if (StaticCache.serverData.getStopTime() == -1) return;

        new BukkitRunnable() {
            @Override
            public void run() {

                int minutes = (int) (StaticCache.serverData.getStopTime() - System.currentTimeMillis()) / (1000 * 60);
                int[] warnings = {30, 10, 5, 1};
                for (int warning : warnings) if (minutes + 1 == warning) warning(minutes + 1);

            }
        }.runTaskTimerAsynchronously(SpigotSCP.getInstance(), 0L, 20L);

    }

    public void warning(int minutes) {
        if (warningsSent.contains(minutes)) return;
        warningsSent.add(minutes);

        Bukkit.getOnlinePlayers().forEach(p -> {
           p.sendMessage("§cThis server will restart in about §e" + minutes + "§c minutes!");
        });
    }
}
