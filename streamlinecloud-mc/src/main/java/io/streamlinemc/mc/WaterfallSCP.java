
/*
*
* OUTDATED (SWITCHED TO VELOCITY)
*
*/

package io.streamlinemc.mc;

public final class WaterfallSCP {

    /*StreamlineCloud streamlineCloud;

    @Getter
    public static WaterfallSCP instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().info("[StreamlineCloud] -> Enabled");

        Functions.startup();

        StaticCache.setRuntime(ServerRuntime.PROXY);

        VersionPacket packet = new VersionPacket(InternalSettings.getVersion(), StreamlineAPI.getApiVersion(), InternalSettings.getBuildDate());
        Utils.postStart(packet);
        streamlineCloud = new StreamlineCloud();


        HashMap<String, Double> servers = new HashMap<>();

        List<String> allServers = new ArrayList<>();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        registerListener();

        scheduler.scheduleAtFixedRate(() -> {



            servers.putAll(new Gson().fromJson(Functions.get("get/allservers"), servers.getClass()));

            for (String s : allServers) getProxy().getServers().remove(s);
            allServers.clear();


            servers.forEach((str, port) -> {

                if (port != 1.0) {
                    if (!str.contains("proxy")) {
                        ServerInfo serverInfo = getProxy().constructServerInfo(
                                str,
                                new InetSocketAddress("localhost", Integer.parseInt(String.valueOf(port).split("\\.")[0])),
                                "Subserver of Streamline - " + str,
                                false
                        );

                        allServers.add(str);
                        getProxy().getServers().put(str, serverInfo);
                    }
                }

            });

            Utils.servers = servers;

        }, 0, 3, TimeUnit.SECONDS);



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[StreamlineCloud] -> Disabled");

    }

    private void registerListener() {
        getProxy().getPluginManager().registerListener(this, new PlayerConnectListener());
    }*/
}
