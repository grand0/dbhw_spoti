package ru.kpfu.itis.gr201.ponomarev.template.base

import io.ktor.server.html.*
import ru.kpfu.itis.gr201.ponomarev.server.UserSession

interface BaseTemplate<in TOuter> : Template<TOuter> {
    val session: UserSession?
}