package be.bluexin.raidingorganizer.database

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.joda.time.DateTime
import java.time.LocalDateTime
import kotlin.properties.Delegates
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun LocalDateTime.toJoda() = DateTime(year, month.value, dayOfMonth, hour, minute, second)
fun DateTime.toJava() = LocalDateTime.of(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute)

fun <ID : Comparable<ID>, EN : Entity<ID>> EntityClass<ID, EN>.createOrUpdate(find: SqlExpressionBuilder.() -> Op<Boolean>, update: EN.() -> Unit) = this.find(find).firstOrNull()?.also { it.update() }
        ?: this.new(update)

fun <ID : Comparable<ID>, EN : Entity<ID>> EntityClass<ID, EN>.createOrUpdate(id: ID, update: EN.() -> Unit) = this.findById(id)?.also { it.update() }
        ?: this.new(id, update)

fun <ID : Comparable<ID>, EN : Entity<ID>> EntityClass<ID, EN>.createIfAbsent(find: SqlExpressionBuilder.() -> Op<Boolean>, create: EN.() -> Unit) = this.find(find).firstOrNull()
        ?: this.new(create)

fun <ID : Comparable<ID>, EN : Entity<ID>> EntityClass<ID, EN>.createIfAbsent(id: ID, create: EN.() -> Unit) = this.findById(id)
        ?: this.new(id, create)

class CachedReference<ID : Comparable<ID>, EN : Entity<ID>, FROM : Comparable<FROM>>(private val ref: Reference<ID, EN>, e: Entity<FROM>) {
    private var cachedValue by Delegates.notNull<EN>()

    /*init {
        getValue(e, ::ref)
    }*/

    operator fun getValue(user: Entity<FROM>, desc: KProperty<*>): EN {
        return if (TransactionManager.currentOrNull() == null)
            cachedValue
        else {
            with(user) {
                ref.getValue(user, desc).apply {
                    cachedValue = this
                }
            }
        }
    }

    operator fun setValue(user: Entity<FROM>, desc: KProperty<*>, to: EN) {
        if (TransactionManager.currentOrNull() == null)
            cachedValue = to
        else {
            with(user) {
                ref.setValue(user, desc, to)
                cachedValue = to
            }
        }
    }
}

fun <ID : Comparable<ID>, EN : Entity<ID>, FROM : Comparable<FROM>> Reference<ID, EN>.cached(e: Entity<FROM>) =
        CachedReference(this, e)

abstract class EagerEntityClass<ID : Comparable<ID>, out T : Entity<ID>>(table: IdTable<ID>, entityType: Class<T>? = null) :
        EntityClass<ID, T>(table, entityType) {

    override fun findById(id: EntityID<ID>): T? {
        return super.findById(id)?.apply {
            this::class.declaredMemberProperties.forEach {
                it.isAccessible = true
                @Suppress("USELESS_CAST")
                (it as KProperty1<*, *>)(this)
            }
        }
    }

    override fun new(id: ID?, init: T.() -> Unit): T {
        return super.new(id, init).apply {
            this::class.declaredMemberProperties.forEach {
                it.isAccessible = true
                @Suppress("USELESS_CAST")
                (it as KProperty1<*, *>)(this)
            }
        }
    }

    fun allEager(): SizedIterable<T> {
        return super.all().onEach {
            with(it) {
                this::class.declaredMemberProperties.forEach {
                    it.isAccessible = true
                    @Suppress("USELESS_CAST")
                    (it as KProperty1<*, *>)(this)
                }
            }
        }
    }
}