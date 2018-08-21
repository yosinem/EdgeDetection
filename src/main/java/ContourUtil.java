import georegression.struct.point.Point2D_I32;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContourUtil {

    public static int getHeight(List<Point2D_I32> points, int minY) {

        Collections.sort(points, new Comparator<Point2D_I32>() {
            public int compare(Point2D_I32 o1, Point2D_I32 o2) {
                return 0;
            }
        });
        return 0;
    }

    public static int getWidth(List<Point2D_I32> points, int minX) {
        return 0;
    }

    public static int getMinY(List<Point2D_I32> points) {
        return 0;
    }

    public static int getMinX(List<Point2D_I32> points) {
        return 0;
    }
}
