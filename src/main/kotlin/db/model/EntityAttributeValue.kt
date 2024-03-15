package ru.kpfu.itis.gr201.ponomarev.db.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Entities : Table("entity_item") {
    val id = integer("item_id").autoIncrement()
    val fullName = varchar("full_name", 1024).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}

object Attributes : Table("attribute_item") {
    val id = integer("attribute_id").autoIncrement()
    val name = varchar("name", 128)

    override val primaryKey = PrimaryKey(id)

    enum class AttributesDictionary(val id: Int) {
        NAME(1),
        ARTIST(2),
        ALBUM_NAME(3),
        ALBUM_TYPE(4),
        DURATION(5),
        EXPLICIT(6),
        PREVIEW_URL(7),
        IMAGE_URL(8);

        companion object {
            fun getById(id: Int) = entries.first { it.id == id }
        }
    }
}

object Values : Table("value_item") {
    val id = integer("value_id").autoIncrement()
    val itemId = integer("item_id").references(Entities.id, onDelete = ReferenceOption.CASCADE)
    val attributeId = integer("attribute_id").references(Attributes.id, onDelete = ReferenceOption.CASCADE)
    val seqNum = integer("seq_num").default(1)
    val value = text("value").nullable()

    override val primaryKey = PrimaryKey(id)
}

data class Value(
    val itemId: Int,
    val attributeId: Int,
    val seqNum: Int = 1,
    val value: String?,
)
