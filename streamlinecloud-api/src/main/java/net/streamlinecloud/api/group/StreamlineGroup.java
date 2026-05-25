package net.streamlinecloud.api.group;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.server.ServerRuntime;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "streamline_groups")
public class StreamlineGroup implements StreamlineGroupImpl {

    @Id
    String name;
    String javaExec;
    @Convert(converter = StringListConverter.class)
    List<String> templates;
    String softwareName;
    ServerRuntime runtime;
    boolean staticGroup = false;
    int minOnlineCount;
    int autoRestartMinutes = -1;
    int priority = 0;
    int minimumHeap = 512;
    int maximumHeap = 1024;

}
