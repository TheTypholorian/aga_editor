package net.typho.aga_editor.window

import java.awt.Component
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <V> Component.property(initial: V, propertyChangedNotif: (String, V, V) -> Unit) = object : ReadWriteProperty<Any?, V> {
    var value: V = initial

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        val old = this.value
        this.value = value

        if (old != value) {
            propertyChangedNotif(property.name, old, value)
        }
    }
}