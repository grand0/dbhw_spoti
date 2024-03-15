package ru.kpfu.itis.gr201.ponomarev.template

import io.ktor.server.html.*
import kotlinx.html.*
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel
import ru.kpfu.itis.gr201.ponomarev.server.UserSession
import ru.kpfu.itis.gr201.ponomarev.template.base.BaseTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.HeaderTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.TracksListTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.insertCommonHead

class SearchTemplate(
    override val session: UserSession? = null,
    private val tracks: List<TrackModel>? = null,
    private val query: String? = null
) : BaseTemplate<HTML> {

    override fun HTML.apply() {
        insertCommonHead()
        body {
            insert(HeaderTemplate(session)) {
                pageName { +PAGE_NAME }
            }
            h2 { +"search in db" }
            form(action = "/search", method = FormMethod.get) {
                input(type = InputType.text, name = "query") {
                    placeholder = "query"
                }
                input(type = InputType.submit) {
                    value = "submit"
                }
            }
            if (tracks != null) {
                p {
                    +"${tracks.size} results for \"$query\""
                }
                insert(TracksListTemplate(tracks)) {}
            }
        }
    }

    companion object {
        private const val PAGE_NAME = "search"
    }
}