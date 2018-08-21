import boofcv.alg.filter.binary.Contour;
import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;
import georegression.struct.point.Point2D_I32;
import imageprocessing.EdgeDetection;
import imageprocessing.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        File originalImage = new File("C:\\Users\\yosi\\Desktop\\card-png\\pekka.png");
        File cropedImage = new File("C:\\Users\\yosi\\Desktop\\images\\35.jpg");
        float precentege = compareImage(originalImage,cropedImage);

        BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample("C:\\Users\\yosi\\Downloads\\Clash_Royale.jpg"));
        List<Contour> contours = EdgeDetection.getEdges(Image.path);

        List<Square> squareContours = convertContoursToSquares(contours);

        for (int i = 0; i < 100; i++) {
            cropImage(image,squareContours.get(i),i);
        }
    }

    private static List<Square> convertContoursToSquares(List<Contour> contours) {
        List<Square> squares = new ArrayList<Square>();
        for (Contour contour:contours){
            Square square = new Square(contour.external);
            squares.add(square);
        }
        return squares;
    }


    private static File cropImage(BufferedImage src, Square square, int fileName) throws IOException {
        BufferedImage dest = src.getSubimage(square.getMinX(), square.getMinY(), square.getWidth(), square.getHeight());

        File outputFile = new File("C:\\Users\\yosi\\Desktop\\images\\"+ fileName +".png");
        ImageIO.write(dest, "png", outputFile);
        return outputFile;
    }

    public static float compareImage(File fileA, File fileB) {

        float percentage = 0;
        try {
            // take buffer data from both image files //
            BufferedImage biA = ImageIO.read(fileA);
            DataBuffer dbA = biA.getData().getDataBuffer();
            int sizeA = dbA.getSize();
            BufferedImage biB = ImageIO.read(fileB);
            DataBuffer dbB = biB.getData().getDataBuffer();
            int sizeB = dbB.getSize();
            int count = 0;
            // compare data-buffer objects //
            if (sizeA == sizeB) {

                for (int i = 0; i < sizeA; i++) {

                    if (dbA.getElem(i) == dbB.getElem(i)) {
                        count = count + 1;
                    }

                }
                percentage = (count * 100) / sizeA;
            } else {
                System.out.println("Both the images are not of same size");
            }

        } catch (Exception e) {
            System.out.println("Failed to compare image files ...");
        }
        return percentage;
    }
}
