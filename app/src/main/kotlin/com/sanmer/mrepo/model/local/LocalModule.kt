package com.sanmer.mrepo.model.local

import android.os.Parcel
import android.os.Parcelable
import com.sanmer.mrepo.utils.ModuleUtils

data class LocalModule(
    val id: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val updateJson: String,
    val state: State,
    val lastUpdated: Long
) : Parcelable {
    val versionDisplay get() = ModuleUtils.getVersionDisplay(version, versionCode)

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        version = parcel.readString() ?: "",
        versionCode = parcel.readInt(),
        author = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        updateJson = parcel.readString() ?: "",
        state = parcel.readString()?.let(State::valueOf) ?: State.DISABLE,
        lastUpdated = parcel.readLong()
    )

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is LocalModule -> id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(version)
        parcel.writeInt(versionCode)
        parcel.writeString(author)
        parcel.writeString(description)
        parcel.writeString(updateJson)
        parcel.writeString(state.name)
        parcel.writeLong(lastUpdated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<LocalModule> {
            override fun createFromParcel(parcel: Parcel): LocalModule {
                return LocalModule(parcel)
            }

            override fun newArray(size: Int): Array<LocalModule?> {
                return arrayOfNulls(size)
            }
        }

        fun example() = LocalModule(
            id = "local_example",
            name = "Example",
            version = "2022.08.16",
            versionCode = 1703,
            author = "Sanmer",
            description = "This is an example!",
            updateJson = "",
            state = State.ENABLE,
            lastUpdated = 0L
        )
    }
}