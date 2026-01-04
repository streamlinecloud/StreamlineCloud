package net.streamlinecloud.api.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all information about the whitelist
 */

@Getter @Setter
@AllArgsConstructor
public class WhitelistConfigurationPacket {

    /**
     * Defines if the whitelist is enabled or disabled
     */
    boolean enabled;

    /**
     * A list of player names that can join while the whitelist is enabled
     */
    List<String> whitelistedPlayers;

    /**
     * A list of player names. All privileged players can use the ingame /server command.
     * A join request from a privileged player can kick a non-privileged player if the network is full.
     */
    List<String> privilegedPlayers;

    /**
     * A list of server names. Only whitelisted players can join these servers. Even if the whitelist is disabled.
     * You can add an entire group by using the group name like 'lobby-*'
     */
    List<String> maintenanceServers;

}
