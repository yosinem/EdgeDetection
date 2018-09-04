package findinimage.data;

import georegression.struct.point.Point2D_I32;

import java.util.List;

public class Square {

    private int minX = 5000;
    private int minY = 5000;
    private int maxX = 0;
    private int maxY = 0;

    public Square(int minX, int minY) {
        this.minX = minX;
        this.minY = minY;
    }

    public Square(List<Point2D_I32> points) {

        for (Point2D_I32 point : points) {
            int y = point.getY();
            int x = point.getX();

            //maxY = y > maxY ? y : maxY;
            if (y > maxY) {
                maxY = y;
            }

            if (y < minY) {
                minY = y;
            }

            if (x > maxX){
                maxX = x;
            }

            if (x < minX){
                minX = x;
            }
        }
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getWidth(){
        return maxX - minX;
    }

    public int getHeight(){
        return maxY - minY;

    }
}
