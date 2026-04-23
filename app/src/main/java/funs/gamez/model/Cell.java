package funs.gamez.model;

import android.os.Parcel;
import android.os.Parcelable;

// 密室单元格
public class Cell implements Parcelable {

	private int walls;
	private boolean visible;
	private boolean visited;

	/* --- 构造函数 -------------------------------------------- */
	
	public Cell() {
		this(Direction.ALL, true, false);
	}
	
	private Cell(int walls, boolean visible, boolean visited) {
		this.walls = walls;
		this.visible = visible;
		this.visited = visited;
	}
	
	/* --- Getters / Setters --------------------------------------- */
	
	public int getWalls() {
		return walls;
	}

	public void setWalls(int walls) {
		this.walls = walls;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/* --- Other methods ------------------------------------------- */
	
	public boolean hasWalls(int bits) {
	    return (walls & bits) == bits;
	}
	
	public void erectWalls(int bits) {
	    walls |= bits;
	}
	
	public void tearDownWalls(int bits) {
	    walls &= (~bits);
	}
	
	/* --- Implementation of Parcelable ---------------------------- */
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(walls);
		dest.writeInt(visible ? 1 : 0);
		dest.writeInt(visited ? 1 : 0);
	}
	
	public static final Parcelable.Creator<Cell> CREATOR = new Parcelable.Creator<Cell>() {

		@Override
		public Cell createFromParcel(Parcel source) {
			return new Cell(
					source.readInt(),
					source.readInt() == 1,
					source.readInt() == 1
			);
		}

		@Override
		public Cell[] newArray(int size) {
			return new Cell[size];
		}
	};

}
