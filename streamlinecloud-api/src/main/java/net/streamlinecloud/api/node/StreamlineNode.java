package net.streamlinecloud.api.node;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "streamline_nodes")
public class StreamlineNode {

    @Id
    String uuid;
    String displayname;
    boolean worker;
    boolean admin;

}
