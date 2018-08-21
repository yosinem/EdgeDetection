package imageprocessing;

import georegression.struct.point.Point2D_I32;

import java.util.List;

public class Square {

    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    public Square(List<Point2D_I32> points) {

        minY = 5000;
        minX = 5000;
        for (Point2D_I32 point : points) {
            int y = point.getY();
            int x = point.getX();

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
