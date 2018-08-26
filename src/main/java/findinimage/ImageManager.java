package findinimage;

import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;
import findinimage.data.Constants;
import findinimage.data.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageManager {

    void createSmallImagesFromScreenshot(String imagePath) throws IOException {
        BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample(imagePath));

        List<Square> squareContours = EdgeDetection.getEdges(imagePath);

        for (int i = 0; i < 50; i++) {
            cropImage(image, squareContours.get(i), i);
        }
    }


    private void cropImage(BufferedImage src, Square square, int fileName) throws IOException {
        BufferedImage dest = src.getSubimage(square.getMinX(), square.getMinY(), square.getWidth(), square.getHeight());

        File outputFile = new File(Constants.CROPPED_IMAGES_DIRECTORY_PATH + fileName + ".png");
        ImageIO.write(dest, "png", outputFile);
    }

    public void resizeImage(File imageFile, int width, int height) throws IOException {

        BufferedImage originalImage = ImageIO.read(imageFile);
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();
        saveImage(resizedImage);
    }

    private void saveImage(BufferedImage imageToSave) throws IOException {

        ImageIO.write(imageToSave, "jpg", new File("C:\\Users\\yosi\\Desktop\\images\\pekka_cro_resized.jpg"));
    }

    public void findSimilarImages(File imageToFind, File directoryContainsImages) {

    }
}
