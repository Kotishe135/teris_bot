package com.codenjoy.dojo.tetris.client;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.tetris.model.Elements;
import com.codenjoy.dojo.tetris.transformations.TransformationMatrixGenerator;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BestMoveFinder {
    private static final int UNIFORM_COORDINATES_LENGTH = 3;
    private static final int BUILDUP_LIMIT = 7;
    private Board board;
    private boolean buildup;
    private List<Point> currentFigurePoints;

    private enum NeighbourType {
        RIGHT(1, 0),
        DOWN(0,-1),
        LEFT(-1,0),
        UP(0,1),
        UP_RIGHT(1, 1),
        UP_LEFT(-1, 1),
        DOWN_RIGHT(1, -1),
        DOWN_LEFT(-1, -1);

        private int offsetX;
        private int offsetY;

        NeighbourType(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }
    }

    public BestMoveFinder() {
        buildup = true;
    }

    private Point getNeighbour(Point point, NeighbourType neighbourType) {
        Point pointNeighbour = point.copy();
        pointNeighbour.setX(point.getX() + neighbourType.getOffsetX());
        pointNeighbour.setY(point.getY() + neighbourType.getOffsetY());
        return pointNeighbour;
    }

    public void setBoard(Board board) {
        this.board = board;
        currentFigurePoints = getFigurePoints(board.getCurrentFigureType(),
                board.getCurrentFigurePoint());
    }

    private List<Point> getFigurePoints(Elements figure, Point fulcrumPoint) {
        List<Point> points = new ArrayList<>();
        points.add(fulcrumPoint);
        switch (figure) {
            case YELLOW:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.RIGHT));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.DOWN));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.DOWN_RIGHT));
                break;
            case BLUE:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP));
                Point downPoint = getNeighbour(fulcrumPoint, NeighbourType.DOWN);
                points.add(downPoint);
                points.add(getNeighbour(downPoint, NeighbourType.DOWN));
                break;
            case ORANGE:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.DOWN));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.DOWN_RIGHT));
                break;
            case CYAN:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.DOWN));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.DOWN_LEFT));
                break;
            case GREEN:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP_RIGHT));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.LEFT));
                break;
            case RED:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP_LEFT));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.RIGHT));
                break;
            case PURPLE:
                points.add(getNeighbour(fulcrumPoint, NeighbourType.UP));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.LEFT));
                points.add(getNeighbour(fulcrumPoint, NeighbourType.RIGHT));
                break;
        }
        return points;
    }

    private char[][] getData() {
        String[] layers = board.getGlass().boardAsString().split("\\n");
        char[][] data = new char[layers.length][layers.length];
        for (int i = layers.length - 1; i >= 0; --i) {
            data[17 - i] = layers[i].toCharArray();
        }
        for (Point point : currentFigurePoints) {
            data[point.getY()][point.getX()] = '.';
        }
        return data;
    }

    private List<Point> downFigure(List<Point> figure) {
        List<Point> newPosition = new ArrayList<>();
        for (Point point : figure) {
            Point newPoint = point.copy();
            newPoint.setY(point.getY() - 1);
            newPosition.add(newPoint);
        }
        return newPosition;
    }

    private boolean isAllowedLowering(char data[][], List<Point> figure) {
        for (Point point : figure) {
            if (point.getY() == 0 || data[point.getY() - 1][point.getX()] != '.') {
                return false;
            }
        }
        return true;
    }

    private List<Point> findFinalFigurePosition(char[][] data, List<Point> figure) {
        List<Point> finalFigurePosition = new ArrayList<>(figure);
        while (isAllowedLowering(data, finalFigurePosition)) {
            finalFigurePosition = downFigure(finalFigurePosition);
        }
        return finalFigurePosition;
    }

    private char[][] apply(char data[][], List<Point> finalPosition) {
        for (Point point : finalPosition) {
            data[point.getY()][point.getX()] =
                    board.getCurrentFigureType().ch();
        }
        return data;
    }

    private RealMatrix pointPositionToUniformCoordinates(Point point) {
        double[] uniformCoordinates = {point.getX(), point.getY(), 1};
        return MatrixUtils.createRowRealMatrix(uniformCoordinates);
    }

    private List<Point> getRotateFigure(List<Point> figure, double angle) {
        List<Point> rotateFigure = new ArrayList<>();
        Point firstPoint = figure.get(0);
        RealMatrix rotationMatrix =
                TransformationMatrixGenerator.getMotionMatrix(-firstPoint.getX(), -firstPoint.getY())
                        .multiply(TransformationMatrixGenerator.getRotationMatrix(angle))
                        .multiply(TransformationMatrixGenerator.getMotionMatrix(firstPoint.getX(), firstPoint.getY()));
        for (Point point : figure) {
            Point newPoint = point.copy();
            RealMatrix uniformCoord = pointPositionToUniformCoordinates(newPoint)
                    .multiply(rotationMatrix);
            newPoint.setX((int) uniformCoord.getEntry(0, 0));
            newPoint.setY((int) uniformCoord.getEntry(0, 1));
            rotateFigure.add(newPoint);
        }
        return rotateFigure;
    }

    private List<List<Point>> getAllRotationsFigure(List<Point> figure, Elements typeFigure) {
        List<List<Point>> allRotations = new ArrayList<>();
        allRotations.add(new ArrayList<>(figure));
        if (typeFigure == Elements.YELLOW) {
            return allRotations;
        }
        allRotations.add(new ArrayList<>(getRotateFigure(figure, 90)));
        if (typeFigure == Elements.GREEN || typeFigure == Elements.BLUE
                || typeFigure == Elements.RED) {
            return allRotations;
        }
        allRotations.add(new ArrayList<>(getRotateFigure(figure, 180)));
        allRotations.add(new ArrayList<>(getRotateFigure(figure, 270)));
        return allRotations;
    }

    private List<Point> moveToZeroX(List<Point> figure) {
        List<Point> newPosition = new ArrayList<>();
        int indexLeftPoint = 0;
        int minX = figure.get(0).getX();
        for (int i = 0; i < figure.size(); ++i) {
            if (minX > figure.get(i).getX()) {
                minX = figure.get(i).getX();
                indexLeftPoint = i;
            }
        }
        for (Point point : figure) {
            Point newPoint = point.copy();
            newPoint.setX(newPoint.getX() - minX);
            newPosition.add(newPoint);
        }
        return newPosition;
    }

    private List<Point> moveRight(List<Point> figure) {
        List<Point> newPosition = new ArrayList<>();
        for (Point point : figure) {
            Point newPoint = point.copy();
            newPoint.setX(point.getX() + 1);
            newPosition.add(newPoint);
        }
        return newPosition;
    }

    private boolean isAllowedRighting(List<Point> figure, boolean buildup) {
        int columns = 18;
        if (buildup) {
            columns -= 2;
        }
        for (Point point : figure) {
            if (point.getX() >= columns) {
                return false;
            }
        }
        return true;
    }

    private int heightBuild(char[][] data) {
        int height = 0;
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                if (data[i][j] != '.') {
                    height++;
                    break;
                }
            }
        }
        return height;
    }

    private boolean isAppear(List<Point> figure) {
        for (Point point : figure) {
            if (point.getY() > 17) {
                return false;
            }
        }
        return true;
    }

    public String findBestMove() {
        if (!isAppear(currentFigurePoints)) {
            return "";
        }
        BoardEvaluateHelper boardEvaluateHelper = new BoardEvaluateHelper();
        MoveEvaluateHelper moveEvaluateHelper = new MoveEvaluateHelper();
        double bestEvaluation = Integer.MIN_VALUE;
        List<Point> preFinalFigure = currentFigurePoints;
        if (heightBuild(getData()) > BUILDUP_LIMIT) {
            buildup = false;
        }
        if (heightBuild(getData()) <= 3) {
            buildup = true;
        }
        int rotation = 0;
        List<List<Point>> allRotation = getAllRotationsFigure(
                currentFigurePoints, board.getCurrentFigureType());
        for (int i = 0; i < allRotation.size(); ++i) {
            List<Point> currentFigure = moveToZeroX(allRotation.get(i));
            while (isAllowedRighting(currentFigure, buildup)) {
                char[][] data = getData();
                List<Point> finalPosition = findFinalFigurePosition(data, currentFigure);
                char[][] resultData;
                try {
                    resultData = apply(getData(), finalPosition);
                } catch (ArrayIndexOutOfBoundsException e) {
                    return "ACT(0,0)";
                }
                double evaluation = boardEvaluateHelper.evaluate(resultData) +
                        moveEvaluateHelper.evaluate(finalPosition);
                if (evaluation > bestEvaluation) {
                    bestEvaluation = evaluation;
                    rotation = i;
                    preFinalFigure = currentFigure;
                }
                currentFigure = moveRight(currentFigure);
            }
        }
        String answer = "";
        List<Point> finalFigure = new ArrayList<>(currentFigurePoints);
        if (rotation > 0) {
            finalFigure = getRotateFigure(finalFigure, rotation * 90);
            for (int i = 0; i < rotation; i++) {
                answer += "ACT, ";
            }
        }
        int offsetX = preFinalFigure.get(0).getX() - finalFigure.get(0).getX();
        int k = 0;
        if (offsetX != 0) {
            k = offsetX / Math.abs(offsetX);
        }
        for (int i = offsetX; k * i > 0; i -= k) {
            if (offsetX > 0) {
                answer += "RIGHT, ";
            } else {
                answer += "LEFT, ";
            }
        }
        answer += "DOWN";
        return answer;
    }
}
