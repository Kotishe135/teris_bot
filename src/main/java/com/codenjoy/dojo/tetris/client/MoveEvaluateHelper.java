package com.codenjoy.dojo.tetris.client;

import com.codenjoy.dojo.services.Point;

import java.util.List;

public class MoveEvaluateHelper {
    private double getHeight(List<Point> figure) {
        double minY = 19;
        double maxY = 0;
        for (Point point : figure) {
            if (minY > point.getY()) {
                minY = point.getY();
            }
            if (maxY < point.getY()) {
                maxY = point.getY();
            }
        }
        return minY + (maxY - minY) / 2;
    }

    public double evaluate(List<Point> figure) {
        return getHeight(figure) * -4.500158825082766;
    }
}
