package ru.kpfu.itis.gr201.ponomarev.model

data class TrackModel(
    var name: String? = null,
    var artists: MutableList<String?> = mutableListOf(),
    var albumName: String? = null,
    var albumType: String? = null,
    var duration: Int? = null,
    var explicit: Boolean? = null,
    var previewUrl: String? = null,
    var imageUrl: String? = null,
) {

    fun durationString(): String {
        val sec = ((duration ?: 0) / 1000) % 60
        val min = (duration ?: 0) / 1000 / 60
        return String.format("%d:%02d", min, sec)
    }

    fun artistsString(): String = artists.filterNotNull().joinToString()

    override fun toString(): String {
        val sb = StringBuilder()
        val artistsFiltered = artists.filterNotNull()
        if (artistsFiltered.isNotEmpty()) {
            sb.append(artistsFiltered.joinToString()).append(" - ")
        }
        sb.append(name)
        return sb.toString()
    }
}
