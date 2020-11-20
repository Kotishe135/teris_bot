package com.codenjoy.dojo.tetris.client;

public class BoardEvaluateHelper {

    private int getRowTransitions(char[][] data) {
        int transitions = 0;

        for (int i = 0; i < data.length; ++i) {
            char lastElem = '#';
            for (int j = 0; j < data[i].length; ++j) {
                char currElem = data[i][j];
                if (lastElem != currElem) {
                    ++transitions;
                }
                lastElem = currElem;
                if (j == data[i].length - 1 && currElem == '.') {
                    ++transitions;
                }
            }
        }
        return transitions;
    }

    private int getColumnTransitions(char[][] data) {
        int transitions = 0;

        for (int j = 0; j < data[0].length; ++j) {
            char lastElem = '#';
            for (int i = 0; i < data.length; ++i) {
                char currElem = data[i][j];
                if (lastElem != currElem) {
                    ++transitions;
                }
                lastElem = currElem;
                if (i == data.length - 1 && currElem == '.') {
                    ++transitions;
                }
            }
        }
        return transitions;
    }

    private int getHoles(char[][] data) {
        int holes = 0;

        for (int i = data.length - 2; i >= 0; --i) {
            for (int j = 0; j < data[i].length; ++j) {
                if (data[i][j] == '.' && data[i + 1][j] != '.') {
                    ++holes;
                    for (int k = i - 1; k >= 0; --k) {
                        if(data[k][j] == '.') {
                            ++holes;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return holes;
    }

    private int getWellSums(char[][] data) {
        int wellSums = 0;

        //Поиск колодцев
        for (int j = 0; j < data[0].length; ++j) {
            for (int i = data.length - 1; i >= 0; --i) {
                if (data[i][j] != '.') {
                    break;
                }
                if (data[i][j] == '.'
                        && ((j - 1 < 0) || (data[i][j - 1] != '.'))
                        && ((j + 1 >= data[i].length) || (data[i][j + 1] != '.'))) {
                    ++wellSums;
                    boolean kBreak = false;
                    for (int k = i - 1; k >= 0; --k) {
                        if (data[k][j] == '.') {
                            ++wellSums;
                        } else {
                            kBreak = true;
                            break;
                        }
                    }
                    if (kBreak) {
                        break;
                    }
                }
            }
        }
        return wellSums;
    }

    private int getRemovedRows(char[][] data) {
        int removedRows = 0;
        for (int i = 0; i < data.length; ++i) {
            boolean fullRows = true;
            for (int j = 0; j < data[i].length; ++j) {
                if (data[i][j] != '.') {
                    fullRows = false;
                    break;
                }
            }
            if (fullRows) {
                removedRows++;
            }
        }
        return removedRows;
    }

    public double evaluate(char[][] data) {
        return (double) getRowTransitions(data) * -3.2178882868487753
                + (double) getColumnTransitions(data) * -9.348695305445199
                + (double) getHoles(data) * -7.899265427351652
                + (double) getWellSums(data) * -3.3855972247263626
                + (double) getRemovedRows(data) * 3.4181268101392694;
    }
}
