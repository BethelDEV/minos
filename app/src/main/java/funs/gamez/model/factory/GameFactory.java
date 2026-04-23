package funs.gamez.model.factory;

import funs.gamez.model.Game;
import funs.gamez.model.GameState;
import funs.gamez.model.Maze;

public class GameFactory {

    private static final String TAG = "GameFactory";
    
    public static Game createGame(GameMetrics gameMetrics) {
        
        Game game = new Game(gameMetrics);
        Maze maze = MazeFactory.createMaze(gameMetrics);
        game.setMaze(maze);
        game.setState(GameState.READY);
        return game;
    }
}
