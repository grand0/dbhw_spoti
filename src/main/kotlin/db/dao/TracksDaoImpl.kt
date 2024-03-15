package ru.kpfu.itis.gr201.ponomarev.db.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.kpfu.itis.gr201.ponomarev.db.model.*
import ru.kpfu.itis.gr201.ponomarev.model.TrackModel

class TracksDaoImpl : TracksDao {
    override suspend fun getAll(limit: Int?, page: Int?): List<TrackModel> {
        val limit = limit?.coerceIn(0, null)
        val page = page?.coerceIn(1, null) ?: 1
        return transaction {
            if (limit == null) {
                resultRowsToTracks(Values.selectAll().toList())
            } else {
                /*
                select *
                from value_item vals
                join (select item_id
                      from entity_item
                      limit :limit
                      offset (:limit * :page)) as ents on vals.item_id = ents.item_id
                 */
                val q = Values.joinQuery(
                    on = { Values.itemId eq it[Entities.id] },
                    joinPart = { Entities.select(Entities.id).limit(limit, ((page - 1) * limit).toLong()) }
                )
                resultRowsToTracks(q.selectAll().toList())
            }
        }
    }

    override suspend fun countAll(): Long {
        return transaction {
            Entities.selectAll().count()
        }
    }

    override suspend fun addTrack(track: TrackModel, skipExisting: Boolean) {
        transaction {
            val query = Entities.run {
                if (skipExisting) {
                    insertIgnore {
                        it[fullName] = track.toString()
                    }
                } else {
                    insert {
                        it[fullName] = track.toString()
                    }
                }
            }
            if (query.insertedCount != 0) {
                val generatedId = query[Entities.id]
                val values = trackToValues(generatedId, track)
                Values.batchInsert(values) {
                    this[Values.itemId] = it.itemId
                    this[Values.attributeId] = it.attributeId
                    this[Values.seqNum] = it.seqNum
                    this[Values.value] = it.value
                }
            }
        }
    }

    override suspend fun addAll(tracks: List<TrackModel>, skipExisting: Boolean) {
        for (track in tracks) {
            addTrack(track, skipExisting)
        }
    }

    override suspend fun search(query: String): List<TrackModel> {
        /*
        select vals.value_id, vals.item_id, vals.attribute_id, vals.seq_num, vals.value
        from value_item vals
        join entity_item ents on vals.item_id = ents.item_id
        where ents.full_name ilike '%e%';
         */
        return transaction {
            val stmt = Values.join(Entities, JoinType.INNER, onColumn = Entities.id, otherColumn = Values.itemId)
                .select(Values.id, Values.itemId, Values.attributeId, Values.seqNum, Values.value)
                .where(Entities.fullName ilike "%${query.replace(" ", "%")}%")
            resultRowsToTracks(stmt.toList())
        }
    }

    private fun resultRowsToTracks(rows: List<ResultRow>): List<TrackModel> = rows
        .groupBy { it[Values.itemId] }
        .map { entry ->
            val valueRows = entry.value
            val track = TrackModel()
            for (valueRow in valueRows) {
                val value = valueRow[Values.value]
                when (Attributes.AttributesDictionary.getById(valueRow[Values.attributeId])) {
                    Attributes.AttributesDictionary.NAME -> track.name = value
                    Attributes.AttributesDictionary.ARTIST -> track.artists.add(value)
                    Attributes.AttributesDictionary.ALBUM_NAME -> track.albumName = value
                    Attributes.AttributesDictionary.ALBUM_TYPE -> track.albumType = value
                    Attributes.AttributesDictionary.DURATION -> track.duration = value?.toIntOrNull()
                    Attributes.AttributesDictionary.EXPLICIT -> track.explicit = value?.toBooleanStrictOrNull()
                    Attributes.AttributesDictionary.PREVIEW_URL -> track.previewUrl = value
                    Attributes.AttributesDictionary.IMAGE_URL -> track.imageUrl = value
                }
            }
            track
        }

    private fun trackToValues(itemId: Int, track: TrackModel): List<Value> {
        val list = mutableListOf<Value>()
        for (attr in Attributes.AttributesDictionary.entries) {
            when (attr) {
                Attributes.AttributesDictionary.NAME -> track.name?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }

                Attributes.AttributesDictionary.ARTIST -> list.addAll(
                    track.artists.filterNotNull()
                        .mapIndexed { index, it ->
                            Value(
                                itemId = itemId,
                                attributeId = attr.id,
                                seqNum = index + 1,
                                value = it
                            )
                        }
                )

                Attributes.AttributesDictionary.ALBUM_NAME -> track.albumName?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }

                Attributes.AttributesDictionary.ALBUM_TYPE -> track.albumType?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }

                Attributes.AttributesDictionary.DURATION -> track.duration?.toString()?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }

                Attributes.AttributesDictionary.EXPLICIT -> track.explicit?.toString()?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }

                Attributes.AttributesDictionary.PREVIEW_URL -> track.previewUrl?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }

                Attributes.AttributesDictionary.IMAGE_URL -> track.imageUrl?.let {
                    list.add(
                        Value(
                            itemId = itemId,
                            attributeId = attr.id,
                            value = it
                        )
                    )
                }
            }
        }
        return list
    }

    infix fun<T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> =
        object : ComparisonOp(this@ilike, QueryParameter(pattern, columnType), "ILIKE") {}
}