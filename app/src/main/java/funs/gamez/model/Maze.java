package funs.gamez.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Maze implements Parcelable {

    private int cols;
    private int rows;
    private int size;
    private List<Cell> cells;

    /* --- Static methods ------------------------------------------ */

    private static List<Cell> createCells(int cols, int rows) {
        return createCells(cols * rows);
    }

    private static List<Cell> createCells(int size) {
        List<Cell> cells = new ArrayList<Cell>(size);
        for (int i = 0; i < size; i++) {
            cells.add(new Cell());
        }
        return cells;
    }

    /* --- Constructors -------------------------------------------- */

    public Maze(int cols, int rows) {
        this(cols, rows, createCells(cols, rows));
    }

    private Maze(int cols, int rows, List<Cell> cells) {
        this.cols = cols;
        this.rows = rows;
        this.size = cols * rows;
        if (cells.size() != size) {
            throw new IllegalArgumentException(
                    "Number of cells does not match calculated size!");
        }
        this.cells = cells;
    }

    /* --- Getters / Setters --------------------------------------- */

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public Map<Direction,Cell> getConnectedNeighbours(Cell cell) {
        return getConnectedNeighbours(cell, null);
    }
    
    public Map<Direction,Cell> getConnectedNeighbours(Cell cell, List<Direction> query) {
        return getConnectedNeighbours(getCol(cell), getRow(cell), query);
    }
    
    public Map<Direction,Cell> getConnectedNeighbours(int col, int row) {
        return getConnectedNeighbours(col, row, null);
    }
    
    public Map<Direction,Cell> getConnectedNeighbours(int col, int row, List<Direction> query) {
        Cell cell = getCell(col, row);
        Map<Direction, Cell> neighbours = getNeighbours(col, row);
        Map<Direction, Cell> connectedNeighbours = new HashMap<>();
        for (Direction direction : (query == null ? neighbours.keySet() : query)) {
            if (neighbours.containsKey(direction) && !cell.hasWalls(direction.bit())) {
                connectedNeighbours.put(direction, neighbours.get(direction));
            }
        }
        return connectedNeighbours;
    }
    
    public Map<Direction,Cell> getNeighbours(Cell cell) {
        int index = cells.indexOf(cell);
        int col = getCol(index);
        int row = getRow(index);
        return getNeighbours(col, row);
    }
    
    public Map<Direction,Cell> getNeighbours(int col, int row) {
        Map<Direction, Cell> neighbours = new HashMap<>();
        for (Direction direction : Direction.values()) {
            Cell neighbour = getNeighbour(col, row, direction);
            if (neighbour != null) {
                neighbours.put(direction, neighbour);
            }
        }
        return neighbours;
    }
    
    public Cell getNeighbour(Cell cell, Direction direction) {
        int index = cells.indexOf(cell);
        int col = getCol(index);
        int row = getRow(index);
        return getNeighbour(col,  row, direction);
    }
    
    public Cell getNeighbour(int col, int row,
            Direction direction) {
        Cell neighbour = null;
        if (hasNeighbour(col, row, direction)) {
            switch (direction) {
            case NORTH:
                neighbour = getCell(col, row - 1);
                break;
            case EAST:
                neighbour = getCell(col + 1, row);
                break;
            case SOUTH:
                neighbour = getCell(col, row + 1);
                break;
            case WEST:
                neighbour = getCell(col - 1, row);
                break;
            }
        }
        return neighbour;
    }

    public boolean hasNeighbour(Cell cell, Direction direction) {
        int index = cells.indexOf(cell);
        int col = getCol(index);
        int row = getRow(index);
        return hasNeighbour(col, row, direction);
    }

    private boolean hasNeighbour(int col, int row, Direction direction) {
        switch (direction) {
        case NORTH:
            return row > 0;
        case EAST:
            return col < cols - 1;
        case SOUTH:
            return row < rows - 1;
        case WEST:
            return col > 0;
        default:
            return false;
        }
    }

    public void tearDownWall(Cell cell, Direction direction) {
        Cell neighbour = getNeighbour(cell, direction);
        if (neighbour != null) {
            cell.tearDownWalls(direction.bit());
            neighbour.tearDownWalls(direction.getOpposite().bit());
        }
    }

    /* --- Other methods ------------------------------------------- */

    public Cell getCell(int col, int row) {
        return cells.get(getIndex(col, row));
    }

    public int getIndex(int col, int row) {
        return cols * row + col;
    }

    public int getCol(Cell cell) {
        return getCol(cells.indexOf(cell));
    }
    
    public int getCol(int index) {
        return index % cols;
    }

    public int getRow(Cell cell) {
        return getRow(cells.indexOf(cell));
    }
    
    public int getRow(int index) {
        return index / cols;
    }

    /* --- Implementation of Parcelable ---------------------------- */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cols);
        dest.writeInt(rows);
        dest.writeList(cells);
    }

    public static final Parcelable.Creator<Maze> CREATOR = new Parcelable.Creator<Maze>() {

        @Override
        public Maze createFromParcel(Parcel source) {
            int cols = source.readInt();
            int rows = source.readInt();
            List<Cell> cells = new ArrayList<Cell>(cols * rows);
            source.readList(cells, null);
            return new Maze(cols, rows, cells);
        }

        @Override
        public Maze[] newArray(int size) {
            return new Maze[size];
        }
    };

}
