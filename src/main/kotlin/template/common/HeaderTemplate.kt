package ru.kpfu.itis.gr201.ponomarev.template.common

import io.ktor.server.html.*
import kotlinx.html.*
import ru.kpfu.itis.gr201.ponomarev.server.UserSession
import ru.kpfu.itis.gr201.ponomarev.template.base.BaseTemplate

class HeaderTemplate(override val session: UserSession? = null) : BaseTemplate<FlowContent> {
    val pageName = Placeholder<FlowContent>()

    override fun FlowContent.apply() {
        header {
            h1 {
                +"spotifydb/"
                insert(pageName)
            }
            p {
                if (session?.spotifyCredentials != null) {
                    +"authed as ${session.spotifyCredentials?.displayName ?: "&lt;unknown&gt;"} "
                    a(href = "/logout") { +"logout" }
                } else {
                    +"guest "
                    a(href = "/auth") { +"auth" }
                }
            }
            ul {
                li { a(href = "/") { +"home" } }
                li { a(href = "/search") { +"search" } }
                li { a(href = "/playlist") { +"playlist" } }
                li { a(href = "/liked") { +"liked" } }
            }
        }
    }
}