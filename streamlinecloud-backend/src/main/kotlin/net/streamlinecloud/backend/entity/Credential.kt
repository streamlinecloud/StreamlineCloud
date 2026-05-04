package net.streamlinecloud.backend.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "credentials")
class Credential(
    @Id
    var identifier: String,
    var key: String
)