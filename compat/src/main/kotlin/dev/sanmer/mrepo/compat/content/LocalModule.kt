package dev.sanmer.mrepo.compat.content

import android.os.Parcel
import android.os.Parcelable

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
    constructor(parcel: Parcel) : this(
        id = checkNotNull(parcel.readString()),
        name = checkNotNull(parcel.readString()),
        version = checkNotNull(parcel.readString()),
        versionCode = parcel.readInt(),
        author = checkNotNull(parcel.readString()),
        description = checkNotNull(parcel.readString()),
        updateJson = checkNotNull(parcel.readString()),
        state = checkNotNull(parcel.readString()).let(State::valueOf),
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
    }
}