package ru.kpfu.itis.gr201.ponomarev.template

import io.ktor.server.html.*
import kotlinx.html.*
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel
import ru.kpfu.itis.gr201.ponomarev.server.UserSession
import ru.kpfu.itis.gr201.ponomarev.template.base.BaseTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.HeaderTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.TracksListTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.insertCommonHead

class MainTemplate(
    override val session: UserSession? = null,
    private val tracks: List<TrackModel>,
    private val currentPage: Int,
    private val totalPages: Int,
    private val totalCount: Long,
) : BaseTemplate<HTML> {

    override fun HTML.apply() {
        insertCommonHead()
        body {
            insert(HeaderTemplate(session)) {
                pageName { +PAGE_NAME }
            }
            h2 { +"tracks in db" }
            p {
                +"total: $totalCount"
                br
                +"showing: ${tracks.size}"
            }
            div(classes = "pages") {
                for (page in 1..totalPages) {
                    val spanPage = {
                        span(classes = "page${if (currentPage == page) " current-page" else ""}") {
                            +page.toString()
                        }
                    }
                    if (currentPage == page) {
                        spanPage()
                    } else {
                        a(href = "?page=$page", classes = "page-a") {
                            spanPage()
                        }
                    }
                }
            }
            insert(TracksListTemplate(tracks)) {}
        }
    }

    companion object {
        private const val PAGE_NAME = "main"
    }
}