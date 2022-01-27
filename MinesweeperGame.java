package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 99;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int score;
    private int countClosedTiles = SIDE * SIDE;
    private boolean isGameStopped;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);

    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(25) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.PURPLE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void markTile(int x, int y) {
        GameObject g = gameField[y][x];
        if (g.isOpen || (countFlags == 0 && !g.isFlag) || isGameStopped) {
            return;
        }
        if (!g.isFlag) {
            g.isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else {
            g.isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.PURPLE);
        }

    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.CORNFLOWERBLUE, "Hey nice kick :)", Color.BLACK, 50);
    }


    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.AQUAMARINE, "Game OVER BRO :(", Color.BLACK, 50);
    }

    private void  restart(){
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        isGameStopped = false;
        createGame();
    }

    private void openTile(int x, int y) {
        if (gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped)
            return;
        gameField[y][x].isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.DARKORANGE);
        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            return;
        } else {
            score += 5;
            setScore(score);
            if (gameField[y][x].countMineNeighbors == 0) {
                setCellValue(x, y, "");
                List<GameObject> neighbors = getNeighbors(gameField[y][x]);
                for (GameObject neighbor : neighbors) {
                    if (!neighbor.isOpen)
                        openTile(neighbor.x, neighbor.y);
                }

            } else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
        }
        if (countClosedTiles == countMinesOnField) {
            win();
        }
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine) {
                    gameField[y][x].countMineNeighbors = 0;
                    List<GameObject> neighbors = getNeighbors(gameField[y][x]);
                    for (GameObject neighbor : neighbors) {
                        if (neighbor.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}