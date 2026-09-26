package net.streamlinecloud.backend.component

import org.springframework.boot.SpringBootVersion
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.core.SpringVersion
import org.springframework.stereotype.Component

@Component
class SpringInfoContributor : InfoContributor {

    override fun contribute(builder: Info.Builder) {
        builder.withDetail(
            "spring",
            mapOf(
                "bootVersion" to SpringBootVersion.getVersion(),
                "frameworkVersion" to SpringVersion.getVersion()
            )
        )
    }
}