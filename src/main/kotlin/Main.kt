package ru.kpfu.itis.gr201.ponomarev

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.Time
import kotlinx.css.properties.Transforms
import kotlinx.css.properties.add
import ru.kpfu.itis.gr201.ponomarev.db.DatabaseSingleton
import ru.kpfu.itis.gr201.ponomarev.db.dao.TracksDaoImpl
import ru.kpfu.itis.gr201.ponomarev.server.SpotifyCredentials
import ru.kpfu.itis.gr201.ponomarev.server.UserSession
import ru.kpfu.itis.gr201.ponomarev.service.SpotifyService
import ru.kpfu.itis.gr201.ponomarev.template.*
import java.io.File
import java.util.Calendar

suspend fun main() {
    DatabaseSingleton.init()
    val dao = TracksDaoImpl()

    embeddedServer(Netty, port = 8080) {
        install(Sessions) {
            cookie<UserSession>("session", directorySessionStorage(File("build/.sessions")))
        }
        install(Authentication) {
            session<UserSession>("session-auth") {
                validate { session ->
                    if (session.spotifyCredentials != null) {
                        val creds = session.spotifyCredentials!!
                        if (creds.tokenExpireTimestamp < Calendar.getInstance().timeInMillis) {
                            val newCreds = SpotifyService.getInstance().refreshToken(creds.refreshToken)
                            val sessionCreds = SpotifyCredentials(
                                displayName = creds.displayName,
                                accessToken = newCreds.accessToken,
                                refreshToken = newCreds.refreshToken ?: creds.refreshToken,
                                tokenExpireTimestamp = Calendar.getInstance().apply { add(Calendar.SECOND, newCreds.expiresIn) }.timeInMillis
                            )
                            sessions.set(UserSession(sessionCreds))
                            return@validate sessionCreds
                        }
                        return@validate creds
                    }
                    return@validate null
                }
                challenge("/auth")
            }
        }

        routing {
            get("/") {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val tracks = dao.getAll(limit, page)
                val count = dao.countAll()
                call.respondHtmlTemplate(MainTemplate(call.sessions.get(), tracks, page, Math.ceilDiv(count, limit).toInt(), count)) {}
            }
            get("/search") {
                val query = call.request.queryParameters["query"]
                if (query.isNullOrBlank()) {
                    call.respondHtmlTemplate(SearchTemplate(call.sessions.get())) {}
                } else {
                    val tracks = dao.search(query)
                    call.respondHtmlTemplate(SearchTemplate(call.sessions.get(), tracks, query)) {}
                }
            }

            get("/auth") {
                if (call.request.queryParameters.contains("code")) {
                    val code = call.request.queryParameters["code"]!!
                    val creds = SpotifyService.getInstance().authCode(code)
                    val expireTimestamp = Calendar.getInstance().apply { add(Calendar.SECOND, creds.expiresIn) }.timeInMillis
                    val userName = SpotifyService.withAccessToken(creds.accessToken)
                        .currentUserProfile()
                        .displayName
                    call.sessions.set(
                        call.sessions.getOrSet { UserSession() }
                            .apply {
                                spotifyCredentials = SpotifyCredentials(
                                    displayName = userName,
                                    accessToken = creds.accessToken,
                                    refreshToken = creds.refreshToken,
                                    tokenExpireTimestamp = expireTimestamp
                                )
                            }
                    )
                    call.respondRedirect("/")
                } else {
                    val url = SpotifyService.getInstance().authCodeUrl()
                    call.respondRedirect(url)
                }
            }
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }

            authenticate("session-auth") {
                get("/playlist") {
                    val creds = call.principal<SpotifyCredentials>()!!
                    if (call.request.queryParameters.contains("id")) {
                        val id = call.request.queryParameters["id"]!!
                        val tracks = SpotifyService.withAccessToken(creds.accessToken)
                            .getFromPlaylist(id)
                        call.respondHtmlTemplate(PlaylistTemplate(call.sessions.get(), tracks)) {
                            playlistId { +id }
                        }
                        KotlinLogging.logger {}
                            .info { "adding tracks" }
                        dao.addAll(tracks)
                    } else {
                        call.respondHtmlTemplate(FindPlaylistTemplate(call.sessions.get())) {}
                    }
                }
                get("/liked") {
                    val creds = call.principal<SpotifyCredentials>()!!
                    val tracks = SpotifyService.withAccessToken(creds.accessToken)
                        .getSaved()
                    call.respondHtmlTemplate(LikedTemplate(call.sessions.get(), tracks)) {}
                    dao.addAll(tracks)
                }
            }

            get("/styles.css") {
                call.respondCss {
                    body {
                        fontFamily = "Lato"
                        backgroundColor = Color("#121212")
                        color = Color.white
                    }

                    rule("header a") {
                        color = Color.white
                    }

                    rule(".pages") {
                        padding = Padding(10.px)
                        overflowX = Overflow.auto
                    }
                    rule(".page") {
                        padding = Padding(horizontal = 16.px)
                        position = Position.relative
                        fontSize = 18.px
                        color = Color.white
                    }
                    rule(".page-a") {
                        textDecoration = TextDecoration.none
                    }
                    rule(".current-page") {
                        fontWeight = FontWeight.bold
                        color = Color("#121212")
                    }
                    rule(".page::before") {
                        content = QuotedString("")
                        width = 40.px
                        height = 40.px
                        position = Position.absolute
                        zIndex = -1
                        transform = Transforms().apply {
                            add("translateX", "-50%")
                            add("translateY", "-50%")
                        }
                        left = 50.pct
                        top = 50.pct
                        borderRadius = 50.pct
                        backgroundColor = Color.transparent
                    }
                    rule(".page:hover::before") {
                        backgroundColor = Color("#2a2a2a")
                    }
                    rule(".current-page::before") {
                        content = QuotedString("")
                        backgroundColor = Color("#1ed760")
                    }
                    rule(".current-page:hover::before") {
                        backgroundColor = Color("#1ed760")
                    }

                    rule(".track") {
                        display = Display.grid
                        gridTemplate = GridTemplate("32px 32px / 80px 1fr auto auto")
                        padding = Padding(10.px)
                        borderRadius = 8.px
                    }
                    rule(".track-image") {
                        gridColumn = GridColumn("1")
                        gridRow = GridRow("1 / 3")
                        borderRadius = 6.px
                    }
                    rule(".track-name") {
                        gridColumn = GridColumn("2")
                        gridRow = GridRow("1")
                        alignSelf = Align.end
                        fontSize = 16.px
                    }
                    rule(".track-artists") {
                        gridColumn = GridColumn("2")
                        gridRow = GridRow("2")
                        alignSelf = Align.start
                        color = Color.gray
                        fontSize = 14.px
                    }
                    rule(".track-duration") {
                        gridColumn = GridColumn("3")
                        gridRow = GridRow("1 / 3")
                        alignSelf = Align.center
                        paddingRight = 10.px
                    }
                    rule(".track-preview") {
                        gridColumn = GridColumn("4")
                        gridRow = GridRow("1 / 3")
                        alignSelf = Align.center
                        width = 0.px
                        transitionDuration = Time("300ms")
                    }
                    rule(".track:hover") {
                        backgroundColor = Color("#2a2a2a")
                    }
                    rule(".track:hover .track-preview") {
                        width = 265.px
                    }
                }
            }
        }
    }.start(wait = true)
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}