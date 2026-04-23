package funs.gamez.model;


import android.os.Parcel;
import android.os.Parcelable;

import funs.gamez.view.Coords;

public class Player implements Parcelable {

	private float x;
	private float y;

	/* --- Constructors -------------------------------------------- */

    public Player(Coords p) {
        this(p.getX(), p.getY());
    }

	public Player(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/* --- Getters / Setters --------------------------------------- */
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void moveTo(Coords p) {
	    setX(p.getX());
	    setY(p.getY());
    }

	/* --- Implementation of Parcelable ---------------------------- */
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(x);
		dest.writeFloat(y);
	}
	
	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {

		@Override
		public Player createFromParcel(Parcel source) {
			return new Player(
					source.readFloat(),
					source.readFloat()
			);
		}

		@Override
		public Player[] newArray(int size) {
			return new Player[size];
		}
	};

}
