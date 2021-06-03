package com.cartoonhero.source.actors.express

import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class Courier : Actor() {

    private fun <T> beApplyExpress(
            sender: Actor, recipient: String, content: T,
            complete: (Parcel<T>) -> Unit) {
        val senderName = sender.javaClass.name
        val parcel = Parcel(senderName, content)
        sender.send {
            complete(parcel)
        }
    }

    private fun beClaim(
            recipient: Actor, complete: (HashSet<Parcel<*>>) -> Unit) {
        val parcelSet = LogisticsCenter.collectParcels(recipient)
        recipient.send {
            parcelSet?.let { complete(it) }
        }
    }

    private fun <T> beCancel(recipient: String, parcel: Parcel<T>) {
        LogisticsCenter.cancelExpress(recipient, parcel)
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun <T> toBeApplyExpress(
            sender: Actor, recipient: String, content: T,
            complete: (Parcel<T>) -> Unit) {
        send {
            beApplyExpress(sender, recipient, content, complete)
        }
    }

    fun toBeClaim(
            recipient: Actor, complete: (HashSet<Parcel<*>>) -> Unit) {
        send {
            beClaim(recipient, complete)
        }
    }

    fun <T> toBeCancel(recipient: String, parcel: Parcel<T>) {
        send {
            beCancel(recipient, parcel)
        }
    }

    // ------------------------------------------------------------------------
    // MARK: - Private
    private object LogisticsCenter {
        private val warehouse:
                MutableMap<String, HashSet<Parcel<*>>> = mutableMapOf()

        inline fun <reified T> storeParcel(recipient: String, parcel: Parcel<T>) {
            val parcelSet: HashSet<Parcel<*>> =
                    if (warehouse[recipient] == null) {
                        hashSetOf()
                    } else {
                        warehouse[recipient]!!
                    }
            if (!parcelSet.contains(parcel)) {
                parcelSet.add(parcel)
                warehouse[recipient] = parcelSet
            }
        }

        fun collectParcels(recipient: Actor): HashSet<Parcel<*>>? {
            val key = recipient.javaClass.name
            val parcelSet = warehouse[key]?.toHashSet()
            warehouse.remove(key)
            return parcelSet
        }

        fun <T> cancelExpress(recipient: String, parcel: Parcel<T>) {
            val parcelSet = warehouse[recipient]?.toHashSet()
            if (parcelSet?.contains(parcel) == true) {
                parcelSet.remove(parcel)
                if (parcelSet.count() == 0) {
                    warehouse.remove(recipient)
                } else {
                    warehouse[recipient] = parcelSet
                }
            }
        }
    }
}

data class Parcel<T>(
        var sender: String = "",
        val content: T
)