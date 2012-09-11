package com.android.whIsmydog;

import android.os.Parcel;
import android.os.Parcelable;

public class Pet implements Parcelable {
	Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {

		@Override
		public Pet createFromParcel(Parcel parcel) {
			return new Pet(parcel);
		}

		@Override
		public Pet[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Pet[size];
		}
	};
	public String name;
	public String color;
	public String breed;
	public boolean founded;
	public String photos[];
	public String owner;
	public String reward;
	public LatLng where;
	public String spices;
	public String gender;

	public static class LatLng {
		double lat;
		double lng;
	}

	public Pet() {

	}

	public Pet(Parcel parcel) {
		name = parcel.readString();
		color = parcel.readString();
		breed = parcel.readString();
		founded = parcel.readInt() == 1;
		parcel.readStringArray(photos);
		double lat = parcel.readDouble();
		double lng = parcel.readDouble();
		where = new LatLng();
		where.lat = lat;
		where.lng = lng;
		reward = parcel.readString();
		spices = parcel.readString();
		gender = parcel.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(name);
		parcel.writeString(color);
		parcel.writeString(breed);
		parcel.writeInt(founded ? 1 : 0);
		parcel.writeStringArray(photos);
		parcel.writeDouble(where.lat);
		parcel.writeDouble(where.lng);
		parcel.writeString(reward);
		parcel.writeString(spices);
		parcel.writeString(gender);

	}
}
