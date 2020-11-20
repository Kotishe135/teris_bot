package com.codenjoy.dojo.tetris.transformations;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public final class TransformationMatrixGenerator {
    private static final double[][] TRANSFORMATION_MATRIX_TEMPLATE = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    };
    private static final int RADIX = 10;
    private static final int RANK = 3;
    private static final int[] CX_POSITION = {2, 0};
    private static final int[] CY_POSITION = {2, 1};
    private static final int[] KX_POSITION = {0, 0};
    private static final int[] KY_POSITION = {1, 1};
    private static final int[] FIRST_COS_POSITION = {0, 0};
    private static final int[] SECOND_COS_POSITION = {1, 1};
    private static final int[] SIN_POSITION = {1, 0};
    private static final int[] NEGATIVE_SIN_POSITION = {0, 1};

    private TransformationMatrixGenerator() {
        throw new ExceptionInInitializerError();
    }

    private static double round(double value, int rank) {
        return (double) Math.round(value * Math.pow(RADIX, rank))
                / Math.pow(RADIX, rank);
    }

    public static RealMatrix getMotionMatrix(double offsetX, double offsetY) {
        RealMatrix motionMatrix = MatrixUtils.createRealMatrix(
                TRANSFORMATION_MATRIX_TEMPLATE);
        motionMatrix.setEntry(CX_POSITION[0], CX_POSITION[1], offsetX);
        motionMatrix.setEntry(CY_POSITION[0], CY_POSITION[1], offsetY);
        return motionMatrix;
    }

    public static RealMatrix getRotationMatrix(double angleValue) {
        RealMatrix rotationMatrix = MatrixUtils.createRealMatrix(
                TRANSFORMATION_MATRIX_TEMPLATE);
        double cos = round(Math.cos(Math.toRadians(angleValue)), RANK);
        double sin = round(Math.sin(Math.toRadians(angleValue)), RANK);
        rotationMatrix.setEntry(
                FIRST_COS_POSITION[0], FIRST_COS_POSITION[1], cos);
        rotationMatrix.setEntry(
                SECOND_COS_POSITION[0], SECOND_COS_POSITION[1], cos);
        rotationMatrix.setEntry(SIN_POSITION[0], SIN_POSITION[1], sin);
        rotationMatrix.setEntry(
                NEGATIVE_SIN_POSITION[0], NEGATIVE_SIN_POSITION[1], -sin);
        return rotationMatrix;
    }

    public static RealMatrix getScalingMatrix(double kX, double kY) {
        RealMatrix motionMatrix = MatrixUtils.createRealMatrix(
                TRANSFORMATION_MATRIX_TEMPLATE);
        motionMatrix.setEntry(KX_POSITION[0], KX_POSITION[1], kX);
        motionMatrix.setEntry(KY_POSITION[0], KY_POSITION[1], kY);
        return motionMatrix;
    }
}
