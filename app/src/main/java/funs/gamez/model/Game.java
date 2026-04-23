package funs.gamez.model;

import android.os.Parcel;
import android.os.Parcelable;

import funs.gamez.model.factory.GameMetrics;

public class Game implements Parcelable {

	private Maze maze;
	private Player player;
	private GameState state = GameState.NASCENT;

    private volatile GameMetrics metrics;

	/* --- 构造函数 -------------------------------------------- */

	public Game(GameMetrics gameMetrics) {
		this(new Maze(gameMetrics.getCols(), gameMetrics.getRows()), new Player(gameMetrics.getPlayerStartPosition()), GameState.NASCENT);
		this.metrics = gameMetrics;
	}

	private Game(Maze maze, Player player, GameState state) {
		this.maze = maze;
		this.player = player;
	}

	/* --- Getters / Setters --------------------------------------- */
	
	public Maze getMaze() {
		return maze;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public GameState getState() {
	    return state;
	}
	
	public void setState(GameState state) {
	    this.state = state;
	}

	public GameMetrics getMetrics() {
	    return metrics;
	}
	
	public void setMetrics(GameMetrics metrics) {
	    this.metrics = metrics;
	}
	
	/* --- Implementation of Parcelable ---------------------------- */

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(maze);
		dest.writeValue(player);
		dest.writeString(state.name());
	}

	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {

		@Override
		public Game createFromParcel(Parcel source) {
			Maze maze = source.readParcelable(null);
			Player player = source.readParcelable(null);
			GameState state = GameState.valueOf(source.readString());
			return new Game(maze, player, state);
		}

		@Override
		public Game[] newArray(int size) {
			return new Game[size];
		}
	};

}
