package funs.gamez.model.factory;

import android.util.DisplayMetrics;
import android.view.View;

import funs.common.tools.CLogger;
import funs.gamez.minos.R;
import funs.gamez.model.SettingsFormData;
import funs.gamez.view.Coords;
import funs.gamez.view.DisplayUtils;

// 各种参数指标
public class GameMetrics {

    private final String TAG = "GameMetrics";
    
    public static final float MIN_CELL_SIZE_INCH = 0.25f;
    public static final int MIN_SHORT_EDGE_CELLS_COUNT = 6;

    private static final float LOG2 = (float) Math.log(2);
    private static final float LOG2_MIN_SHORT_EDGE_CELLS_COUNT = (float) Math
            .log(MIN_SHORT_EDGE_CELLS_COUNT) / LOG2;

    private int wallThickness = 1;
    private int cols = 4;
    private int rows = 4;
    private int cellSize = 40;

    /* --- Constructors -------------------------------------------- */

    public GameMetrics(View gameContainer, SettingsFormData gameSettings) {

        wallThickness = (int) gameContainer.getResources().getDimension(
                R.dimen.wall_thickness);

        int containerWidth = gameContainer.getWidth();
        int containerHeight = gameContainer.getHeight();
        DisplayMetrics displayMetrics = DisplayUtils
                .getDisplayMetrics(gameContainer.getContext());
        float absWidth = ((float) (containerWidth - 2 * wallThickness))
                / displayMetrics.xdpi;
        float absHeight = ((float) (containerHeight - 2 * wallThickness))
                / displayMetrics.ydpi;
        float absShort = Math.min(absWidth, absHeight);
        float absLong = Math.max(absWidth, absHeight);
        int maxShortEdgeCells = (int) Math.floor(absShort / MIN_CELL_SIZE_INCH);
        int shortEdgeCellCount = MIN_SHORT_EDGE_CELLS_COUNT;
        if (maxShortEdgeCells > MIN_SHORT_EDGE_CELLS_COUNT) {
            float log2maxShortEdgeCellsCount = (float) Math.log(maxShortEdgeCells)
                    / LOG2;
            float dLog2 = log2maxShortEdgeCellsCount
                    - LOG2_MIN_SHORT_EDGE_CELLS_COUNT;
            float log2shortEdgeCellsCount = LOG2_MIN_SHORT_EDGE_CELLS_COUNT
                    + gameSettings.getMazeSize() * dLog2;
            shortEdgeCellCount = (int) Math.floor(Math.pow(2,
                    log2shortEdgeCellsCount));
        }
        int longEdgeCellsCount = (int) Math.floor((float) shortEdgeCellCount
                * absLong / absShort);
        cols = absWidth < absHeight ? shortEdgeCellCount : longEdgeCellsCount;
        rows = absWidth < absHeight ? longEdgeCellsCount : shortEdgeCellCount;
        int horizCellSize = (int) Math
                .floor((float) (containerWidth - 2 * wallThickness)
                        / (float) cols);
        int vertCellSize = (int) Math
                .floor((float) (containerHeight - 2 * wallThickness)
                        / (float) rows);
        cellSize = Math.min(horizCellSize, vertCellSize);
        
        CLogger.i(TAG, String.format("ctor: containerWidth=%s, containerHeight=%s, cols=%s, rows=%s, cellSize=%s", containerWidth, containerHeight, cols, rows, cellSize));
        
    }

    public Coords getPlayerStartPosition() {
        return new Coords(getCols() - 1, 0);
    }

    public Coords getDestinationPosition() {
        return new Coords(0, getRows() - 1);
    }

    public boolean cellContains(Coords p, float x, float y) {
        return cellContains(p.getCol(), p.getRow(), x, y);
    }

    public boolean cellContains(int col, int row, float x, float y) {
        return (Math.abs(col - x) <= 0.5) && (Math.abs(row - y) <= 0.5);
    }

    /* --- Getters / Setters --------------------------------------- */

    public int getWallThickness() {
        return wallThickness;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCellSize() {
        return cellSize;
    }

    /* --- Other methods ------------------------------------------- */

    public int getWidth() {
        return cols * cellSize + 2 * wallThickness;
    }

    public int getHeight() {
        return rows * cellSize + 2 * wallThickness;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GameMetrics) {
            GameMetrics other = (GameMetrics) o;
            return this.cellSize == other.cellSize && this.cols == other.cols
                    && this.rows == other.rows;
        } else {
            return false;
        }
    }
}
