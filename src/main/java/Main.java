import boofcv.alg.filter.binary.Contour;
import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;
import georegression.struct.point.Point2D_I32;
import imageprocessing.EdgeDetection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample("C:\\Users\\yosi\\Downloads\\Clash_Royale.jpg"));
        List<Contour> contours = EdgeDetection.getEdges(Image.path);

        List<Rectangle> squareContours = convertContoursToRectangles(contours);


    }

    private static List<Rectangle> convertContoursToRectangles(List<Contour> contours) {
        List<Rectangle> rectangles = new ArrayList<Rectangle>();
        for (Contour contour:contours){
            Rectangle rectangle = createRectangleFromContour(contour.external);
            rectangles.add(rectangle);
        }
        return rectangles;
    }

    private static Rectangle createRectangleFromContour(List<Point2D_I32> points) {
        int minX = ContourUtil.getMinX(points);
        int minY = ContourUtil.getMinY(points);
        int width = ContourUtil.getWidth(points,minX);
        int height = ContourUtil.getHeight(points,minY);

        return new Rectangle(minX,minY,width,height);
    }


    private static File cropImage(BufferedImage src, Rectangle rectangle) throws IOException {
        BufferedImage dest = src.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        File outputFile = new File("image.jpg");
        ImageIO.write(dest, "jpg", outputFile);
        return outputFile;
    }
}
