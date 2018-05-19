package be.bluexin.raidingorganizer.restclient

import be.bluexin.raidingorganizer.database.*
import be.bluexin.raidingorganizer.webserver.JacksonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import org.jetbrains.exposed.sql.transactions.transaction

object Silveress {
    const val silveress_base = "https://api.silveress.ie/bns/v3/"

    open class SilveressArgs(
            val type: String? = null,
            val limit: Int? = null,
            val skip: Int? = null,
            val beautify: String? = "min"
    ) {
        override fun toString(): String {
            val l = mutableMapOf<String, String>()
            if (type != null) l["type"] = type
            if (limit != null) l["limit"] = limit.toString()
            if (skip != null) l["skip"] = skip.toString()
            if (beautify != null) l["beautify"] = beautify
            return l.entries.joinToString(separator = "&")
        }
    }

    enum class Ordering {
        NEW,
        OLD,
        COUNT // Not actually an ordering, but we'll pretend it is
        ;

        override fun toString(): String {
            return super.toString().toLowerCase()
        }
    }

    //@Location("equipment/{ordering?}")
    data class GetEquipment(val ordering: Ordering? = null, val args: SilveressArgs? = SilveressArgs()) {
        override fun toString(): String {
            return buildString {
                append("equipment")
                if (ordering != null) {
                    append('/')
                    append(ordering)
                }
                if (args != null) {
                    append('?')
                    append(args)
                }
            }
        }
    }

    suspend fun grabEquipment() {
        val client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = JacksonSerializer
            }
        }

        val a = client.get<Array<Equipment>>("$silveress_base${GetEquipment()}")
        transaction {
            a.forEach {
                DbItem.createOrUpdate(find = { ItemsTable.name eq it.name }) {
                    name = it.name
                    image = it.img
                    type = ItemType.createIfAbsent(find = { TypesTable.name eq it.type }) {
                        name = it.type
                    }
                    rank = it.rank
                }
            }
        }
        println("Saved ${a.size} pieces of equipment.")
    }
}