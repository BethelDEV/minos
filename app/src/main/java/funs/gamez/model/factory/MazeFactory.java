package funs.gamez.model.factory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;

import funs.gamez.model.Cell;
import funs.gamez.model.Direction;
import funs.gamez.model.Maze;

public class MazeFactory {

    private static final Random random = new Random();

    public static Maze createMaze(GameMetrics gameMetrics) {
        Maze maze = new Maze(gameMetrics.getCols(), gameMetrics.getRows());

        List<Cell> unvisitedCells = new ArrayList<>(maze.getCells());
        Deque<Cell> stack = new ArrayDeque<>();

        // 随机选择一个单元格并将其称为“当前单元格”
        Cell currentCell = unvisitedCells.get(random.nextInt(unvisitedCells
                .size()));

        // 将初始单元格设置为当前单元格并将其标记为已访问
        currentCell.setVisited(true);
        unvisitedCells.remove(currentCell);

        // 当存在未访问过的单元格...
        while (unvisitedCells.size() > 0) {
            Map<Direction, Cell> unvisitedNeighbours = getUnvisitedNeighbours(
                    maze, currentCell);
            // 如果当前单元格 存在未访问过的邻居单元格
            if (unvisitedNeighbours.size() > 0) {
                // 随机选择一个未访问过的邻居单元格
                List<Direction> directions = new ArrayList<>(
                        unvisitedNeighbours.keySet());
                Direction direction = directions.get(random.nextInt(directions
                        .size()));
                Cell nextCell = maze.getNeighbour(currentCell, direction);
                // 将当前单元格压入堆栈
                stack.push(currentCell);
                // 移除当前单元格和所选单元格之间的墙壁
                maze.tearDownWall(currentCell, direction);
                // 使所选单元格成为当前单元格并将其标记为已访问
                currentCell = nextCell;
                currentCell.setVisited(true);
                unvisitedCells.remove(currentCell);
            }
            // 否则如果堆栈不为空
            else if (stack.size() > 0) {
                // 从堆栈中弹出一个单元格并将其设为当前单元格
                currentCell = stack.pop();
            }
            else {
                // 随机选择一个未访问的单元格，将其设为当前单元格并将其标记为已访问
                currentCell = unvisitedCells.get(random.nextInt(unvisitedCells
                        .size()));
                currentCell.setVisited(true);
                unvisitedCells.remove(currentCell);
            }
        }

        return maze;
    }

    private static Map<Direction, Cell> getUnvisitedNeighbours(Maze maze,
            Cell cell) {
        Map<Direction, Cell> unvisitedNeighbours = maze.getNeighbours(cell);
        List<Direction> directions = new ArrayList<>(unvisitedNeighbours.keySet());
        for (Direction direction : directions) {
            if (unvisitedNeighbours.get(direction).isVisited()) {
                unvisitedNeighbours.remove(direction);
            }
        }
        return unvisitedNeighbours;
    }

}
