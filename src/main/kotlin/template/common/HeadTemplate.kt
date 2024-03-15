package ru.kpfu.itis.gr201.ponomarev.template.common

import kotlinx.html.HTML
import kotlinx.html.head
import kotlinx.html.link

fun HTML.insertCommonHead() {
    head {
        link(rel = "stylesheet", href = "/styles.css", type = "text/css")

        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
        link(rel = "stylesheet", href = "https://fonts.googleapis.com/css2?family=Lato&display=swap")
    }
}