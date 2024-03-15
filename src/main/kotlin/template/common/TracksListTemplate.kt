package ru.kpfu.itis.gr201.ponomarev.template.common

import io.ktor.server.html.*
import kotlinx.html.*
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel

class TracksListTemplate(
    private val tracks: List<TrackModel>,
) : Template<FlowContent> {

    override fun FlowContent.apply() {
        div(classes = "track-list") {
            for (track in tracks) {
                div(classes = "track") {
                    if (track.imageUrl != null) {
                        img(src = track.imageUrl, classes = "track-image") {
                            width = "64"
                            height = "64"
                        }
                    }
                    span(classes = "track-name") {
                        +(track.name ?: "")
                    }
                    span(classes = "track-artists") {
                        +track.artistsString()
                    }
                    span(classes = "track-duration") {
                        +track.durationString()
                    }
                    if (track.previewUrl != null) {
                        audio(classes = "track-preview") {
                            src = track.previewUrl.toString()
                            controls = true
                        }
                    }
                }
            }
        }
    }
}