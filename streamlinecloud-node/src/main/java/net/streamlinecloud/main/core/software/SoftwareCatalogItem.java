package net.streamlinecloud.main.core.software;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class SoftwareCatalogItem {

    String software;
    String version;
    String url;

}
