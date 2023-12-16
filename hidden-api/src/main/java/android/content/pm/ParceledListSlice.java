package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ParceledListSlice<T> implements Parcelable {

    public ParceledListSlice(List<T> list) {
        throw new RuntimeException("Stub!");
    }

    public List<T> getList() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @SuppressWarnings("rawtypes")
    public static final ClassLoaderCreator<ParceledListSlice> CREATOR = new ClassLoaderCreator<>() {
        public ParceledListSlice createFromParcel(Parcel in) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public ParceledListSlice createFromParcel(Parcel in, ClassLoader loader) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public ParceledListSlice[] newArray(int size) {
            throw new RuntimeException("Stub!");
        }
    };
}