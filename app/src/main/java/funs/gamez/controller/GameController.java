package funs.gamez.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import funs.common.tools.CLogger;
import funs.gamez.minos.R;
import funs.gamez.model.Cell;
import funs.gamez.model.Direction;
import funs.gamez.model.Game;
import funs.gamez.model.GameState;
import funs.gamez.view.Coords;
import funs.gamez.view.GameView;
import funs.gamez.view.render.GameRenderer;

// 游戏控制器
public class GameController {

    private static final String TAG = "GameController";

    private Context context;
    private final Game game;
    private final GameView gameView;

    private final Vibrator vibrator;

    private boolean isDragging = false;
    private Coords screenDragStartPos;
    private Coords playerDragStartPos;
    private Coords lastPlayerDragPos;
    private int dragPointerId;

    private final MediaPlayer finishedPlayer;
    
    public GameController(Context context, Game game, GameView gameView) {
        this.context = context;
        this.game = game;
        this.gameView = gameView;

        this.vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        finishedPlayer = MediaPlayer.create(context, R.raw.win);
    }

    /* --- 操控"小球"拖放 -------------------------------------- */

    public boolean handleDrag(View view, MotionEvent ev) {

        if (game.getState() == GameState.PLAYING) {
            final int action = ev.getAction();

            switch (action) {
            case MotionEvent.ACTION_DOWN: {
                int pointerIndex = ev.getPointerId(action);
                float x = ev.getX(pointerIndex);
                float y = ev.getY(pointerIndex);
                if (playerContainsPoint(x, y)) {
                    isDragging = true;
                    dragPointerId = ev.getPointerId(0);
                    screenDragStartPos = new Coords(x, y);
                    playerDragStartPos = createBracketedMazeCoords(game
                            .getPlayer().getX(), game.getPlayer().getY());
                    lastPlayerDragPos = playerDragStartPos;
                    CLogger.i(TAG,
                            String.format(
                                    "Start dragging at screenDragStart=%s, playerDragStart=%s",
                                    screenDragStartPos, playerDragStartPos));
                    performInitDragFeedback();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (isDragging) {
                    int pointerIndex = ev.findPointerIndex(dragPointerId);
                    float x = ev.getX(pointerIndex);
                    float y = ev.getY(pointerIndex);
                    Coords playerDelta = Coords.toMazeCoords(game.getMetrics(),
                            x - screenDragStartPos.getX(), y
                                    - screenDragStartPos.getY());
                    Coords playerDragTargetPos = createBracketedMazeCoords(
                            playerDragStartPos.getX() + playerDelta.getX(),
                            playerDragStartPos.getY() + playerDelta.getY());
                    CLogger.i(TAG,
                            String.format(
                                    "Dragging to x=%s, y=%s, playerDragPos=%s, playerDelta=%s",
                                    x, y, playerDragTargetPos, playerDelta));

                    List<Cell> route = findRoute(lastPlayerDragPos,
                            playerDragTargetPos);
                    Coords newPlayerDragPos = lastPlayerDragPos;
                    if (route.size() > 0) {
                        Cell dest = route.get(route.size() - 1);
                        newPlayerDragPos = createBracketedMazeCoords(game
                                .getMaze().getCol(dest),
                                game.getMaze().getRow(dest));
                    }
                    Cell playerCell = game.getMaze().getCell(
                            newPlayerDragPos.getCol(),
                            newPlayerDragPos.getRow());
                    Set<Direction> availableDirections = game.getMaze()
                            .getConnectedNeighbours(playerCell).keySet();
                    newPlayerDragPos = createConstrainedMazeCoords(
                            newPlayerDragPos, playerDragTargetPos,
                            availableDirections);
                    game.getPlayer().setX(newPlayerDragPos.getX());
                    game.getPlayer().setY(newPlayerDragPos.getY());

                    if (hasPlayerReachedDestination()) {
                        game.getPlayer().moveTo(game.getMetrics().getDestinationPosition());
                        setGameState(GameState.FINISHED);
                    } else {
                        // 触壁了
                    }

                    gameView.invalidate();
                    lastPlayerDragPos = newPlayerDragPos;
                }

                break;
            }
            case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                isDragging = false;
                break;
            }

                case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);

                if (pointerId == dragPointerId) {
                    isDragging = false;
                }
                break;
            }
            }

        }

        return true;
    }

    private boolean hasPlayerReachedDestination() {
        return game.getMetrics().cellContains(game.getMetrics().getDestinationPosition(), game.getPlayer().getX(), game.getPlayer().getY());
    }

    private List<Cell> findRoute(Coords start, Coords dest) {
        Cell currentCell = game.getMaze().getCell(start.getCol(),
                start.getRow());
        Cell destCell = game.getMaze().getCell(dest.getCol(), dest.getRow());
        List<Cell> route = new ArrayList<>();
        int dCols = dest.getCol() - start.getCol();
        int dRows = dest.getRow() - start.getRow();
        CLogger.i(TAG, String.format("findRoute: dCols=%s, dRows=%s", dCols, dRows));

        List<Direction> queryDirections = getQueryDirections(dCols, dRows);
        Map<Direction, Cell> candidates = game.getMaze()
                .getConnectedNeighbours(currentCell, queryDirections);

        while (currentCell != destCell && candidates.size() > 0) {
            currentCell = null;
            for (Direction queryDirection : queryDirections) {
                CLogger.i(TAG, String.format("  queryDirection=%s", queryDirection));
                if (candidates.containsKey(queryDirection)) {
                    currentCell = candidates.get(queryDirection);
                    route.add(currentCell);
                    dCols = dest.getCol() - game.getMaze().getCol(currentCell);
                    dRows = dest.getRow() - game.getMaze().getRow(currentCell);
                    queryDirections = getQueryDirections(dCols, dRows);
                    candidates = game.getMaze().getConnectedNeighbours(
                            currentCell, queryDirections);
                    break;
                }
            }
        }
        return route;
    }

    private List<Direction> getQueryDirections(int dCols, int dRows) {
        List<Direction> queryDirections = new ArrayList<>();
        if (Math.abs(dCols) > Math.abs(dRows)) {
            queryDirections.add(dCols > 0 ? Direction.EAST : Direction.WEST);
            if (dRows > 0) {
                queryDirections.add(Direction.SOUTH);
            } else if (dRows < 0) {
                queryDirections.add(Direction.NORTH);
            }
        } else if (dRows != 0) {
            queryDirections.add(dRows > 0 ? Direction.SOUTH : Direction.NORTH);
            if (dCols > 0) {
                queryDirections.add(Direction.EAST);
            } else if (dCols < 0) {
                queryDirections.add(Direction.WEST);
            }
        }
        return queryDirections;
    }

    /* --- 坐标工具函数 -------------------------------------------- */

    private boolean playerContainsPoint(float x, float y) {
        Coords mazeCoords = Coords.toMazeCoords(game.getMetrics(), x, y);
        float dx = game.getPlayer().getX() - mazeCoords.getX() + 0.5f;
        float dy = game.getPlayer().getY() - mazeCoords.getY() + 0.5f;
        return Math.sqrt(dx * dx + dy * dy) <= GameRenderer.REL_PLAYER_SIZE;
    }

    private Coords createBracketedMazeCoords(float col, float row) {
        if (col < 0) {
            col = 0f;
        } else if (col > game.getMaze().getCols() - 1) {
            col = game.getMaze().getCols() - 1;
        }
        if (row < 0) {
            row = 0f;
        } else if (row > game.getMaze().getRows() - 1) {
            row = game.getMaze().getRows() - 1;
        }
        return new Coords(col, row);
    }

    private Coords createConstrainedMazeCoords(Coords cellCoords,
            Coords pullCoords, Set<Direction> availableDirections) {
        float pullCol = pullCoords.getX() - cellCoords.getCol();
        float pullRow = pullCoords.getY() - cellCoords.getRow();
        float dCol = pullCol;
        float dRow = pullRow;
        if (!availableDirections.contains(pullCol > 0 ? Direction.EAST
                : Direction.WEST)) {
            dCol = 0;
        }
        if (!availableDirections.contains(pullRow > 0 ? Direction.SOUTH
                : Direction.NORTH)) {
            dRow = 0;
        }
        if (Math.abs(dCol) > Math.abs(dRow)) {
            dRow = 0;
        } else {
            dCol = 0;
        }
        return createBracketedMazeCoords(cellCoords.getCol() + dCol,
                cellCoords.getRow() + dRow);
    }

    /* --- 游戏状态处理 ------------------------------------- */

    public void setGameState(GameState state) {

        switch (game.getState()) {
        case READY:
            if (state == GameState.PLAYING) {
                performGameStartedFeedback();
            }
            break;
        case PLAYING:
            if (state == GameState.FINISHED) {
                performGameFinishedFeedback();
            }
            break;
        }

        game.setState(state);
        gameView.invalidate();

    }

    /* --- 处理反馈 --------------------------------------- */

    private void performInitDragFeedback() {
        vibrator.vibrate(100);
    }

    private void performWallHitFeedback() {
        vibrator.vibrate(20);
    }

    private void performGameStartedFeedback() {
//        playAudio(startingPlayer);
        vibrator.vibrate(200);

        if (mListener != null) {
            mListener.onGameStarted();
        }
    }

    private void performGameFinishedFeedback() {
        playAudio(finishedPlayer);
        vibrator.vibrate(200);

        if (mListener != null) {
            mListener.onGameFinished();
        }
    }

    private void playAudio(MediaPlayer player) {
//        if (player.isPlaying()) {
//            player.stop();
//            player.seekTo(0);
//        }
        player.start();
    }

    public void release() {
        if (finishedPlayer != null) {
            finishedPlayer.release();
        }
    }

    private OnGameStateChangedListener mListener;
    public interface OnGameStateChangedListener{
        void onGameStarted();
        void onGameFinished();
    }

    public void setOnGameStateChangedListener(OnGameStateChangedListener mListener) {
        this.mListener = mListener;
    }
}
