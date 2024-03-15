package ru.kpfu.itis.gr201.ponomarev.template

import io.ktor.server.html.*
import kotlinx.html.*
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel
import ru.kpfu.itis.gr201.ponomarev.server.UserSession
import ru.kpfu.itis.gr201.ponomarev.template.base.BaseTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.HeaderTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.TracksListTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.insertCommonHead

class PlaylistTemplate(
    override val session: UserSession? = null,
    private val tracks: List<TrackModel>,
) : BaseTemplate<HTML> {
    val playlistId = Placeholder<FlowContent>()

    override fun HTML.apply() {
        insertCommonHead()
        body {
            insert(HeaderTemplate(session)) {
                pageName { +PAGE_NAME }
            }
            p {
                +"id: "
                insert(playlistId)
                br
                +"${tracks.size} tracks"
            }
            insert(TracksListTemplate(tracks)) {}
        }
    }

    companion object {
        private const val PAGE_NAME = "playlist"
    }
}