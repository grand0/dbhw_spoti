package ru.kpfu.itis.gr201.ponomarev.template

import io.ktor.server.html.*
import kotlinx.html.*
import ru.kpfu.itis.gr201.ponomarev.server.UserSession
import ru.kpfu.itis.gr201.ponomarev.template.base.BaseTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.HeaderTemplate
import ru.kpfu.itis.gr201.ponomarev.template.common.insertCommonHead

class FindPlaylistTemplate(override val session: UserSession? = null) : BaseTemplate<HTML> {
    override fun HTML.apply() {
        insertCommonHead()
        body {
            insert(HeaderTemplate(session)) {
                pageName { +PAGE_NAME }
            }
            form(action = "/playlist", method = FormMethod.get) {
                input(type = InputType.text, name = "id") {
                    placeholder = "playlist id"
                }
                input(type = InputType.submit) {
                    value = "submit"
                }
            }
        }
    }

    companion object {
        private const val PAGE_NAME = "playlist"
    }
}