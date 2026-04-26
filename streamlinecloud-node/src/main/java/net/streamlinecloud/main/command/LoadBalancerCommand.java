package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.backend.LoadBalancer;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import java.io.IOException;

public class LoadBalancerCommand extends CloudCommand {

    public LoadBalancerCommand() {
        setName("loadbalancer");
        setAliases(new String[]{"lb"});
        setDescription("Manage the load balancers");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            help();
            return;
        }

        String command = args[1];

        switch (command) {
            case "list":
                StreamlineCloud.log("Load balancers:");
                for (LoadBalancer loadBalancer : Cache.i().getConfig().getNetwork().getLoadBalancers()) {
                    StreamlineCloud.log("- " + loadBalancer.getName() + " - " + loadBalancer.getPort() + " (" + loadBalancer.getGroup() + ")");
                }
                break;

            case "create":
                if (args.length != 5) {
                    StreamlineCloud.log("loadbalancer create <name> <port> <targetGroup>");
                    return;
                }

                for (LoadBalancer loadBalancer : Cache.i().getConfig().getNetwork().getLoadBalancers()) {
                    if (loadBalancer.getName().equals(args[2])) {
                        StreamlineCloud.log("LoadBalancer " + loadBalancer.getName() + " already exists");
                        return;
                    }
                }

                if (!StreamlineCloud.getGroupManager().groupExists(args[4])) {
                    StreamlineCloud.log("The group " + args[4] + " does not exist");
                    return;
                }

                try {
                    LoadBalancer loadBalancer = new LoadBalancer(args[2], args[4], Integer.parseInt(args[3]));
                    Cache.i().getConfig().getNetwork().getLoadBalancers().add(loadBalancer);
                    MainConfig.saveConfig();
                    StreamlineCloud.log("LoadBalancer " + args[2] + " created.");

                    loadBalancer.start();
                } catch (NumberFormatException e) {
                    StreamlineCloud.log("Please enter a valid number");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "delete":
                if (args.length != 3) {
                    StreamlineCloud.log("loadbalancer delete <name>");
                    return;
                }

                for (LoadBalancer loadBalancer : Cache.i().getConfig().getNetwork().getLoadBalancers()) {
                    if (loadBalancer.getName().equals(args[2])) {
                        Cache.i().getConfig().getNetwork().getLoadBalancers().remove(loadBalancer);
                        StreamlineCloud.log("LoadBalancer " + loadBalancer.getName() + " deleted. Please restart StreamlineCloud.");
                        MainConfig.saveConfig();
                        return;
                    }
                }

                StreamlineCloud.log("The load balancer " + args[2] + " does not exist");
                break;

            case "help":
                help();
                break;
        }

    }

    private void help() {
        StreamlineCloud.log("loadbalancer list");
        StreamlineCloud.log("loadbalancer create <name> <port> <targetGroup>");
        StreamlineCloud.log("loadbalancer delete <name>");
    }
}