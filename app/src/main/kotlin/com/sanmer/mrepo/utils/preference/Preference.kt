package com.sanmer.mrepo.utils.preference

import kotlin.reflect.KProperty

data class Preference<T>(
    var value: T
) {
    operator fun getValue(
        thisObj: Any?, property:
        KProperty<*>
    ): T {
        return SPUtils.getValue(property.name, value)
    }

    operator fun setValue(
        thisObj: Any?,
        property: KProperty<*>,
        value: T
    ) {
        SPUtils.putValue(property.name, value)
        this.value = value
    }
}

fun <T>mutablePreferenceOf(value: T) = Preference(value)
