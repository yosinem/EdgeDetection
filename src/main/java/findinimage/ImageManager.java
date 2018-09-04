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

    void createSmallImagesFromScreenshot() throws IOException {

        BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample(Constants.SCREENSHOT_PATH));
        List<Square> squareContours = EdgeDetection.getEdges(Constants.SCREENSHOT_PATH);

        for (int i = 0; i < 50; i++) {
            cropImage(image, squareContours.get(i), i);
        }
    }


    private void cropImage(BufferedImage src, Square square, int fileName) throws IOException {
        BufferedImage dest = src.getSubimage(square.getMinX(), square.getMinY(), square.getWidth(), square.getHeight());

        File outputFile = new File(Constants.CROPPED_IMAGES_DIRECTORY_PATH + fileName + ".jpg");
        ImageIO.write(dest, "jpg", outputFile);
    }

    public void resizeOriginalImages(Square imageDim) throws IOException {
        File OriginalImagesDirectory = new File(Constants.ORIGINAL_IMAGES_DIRECTORY_PATH);

        for (File file : OriginalImagesDirectory.listFiles()) {
            resizeImage(file, imageDim.getMinX(), imageDim.getMinY());
        }
    }

    public Square getSmallerImage() {

        int minX = 500;
        int minY = 500;

        File OriginalImagesDirectory = new File(Constants.ORIGINAL_IMAGES_DIRECTORY_PATH);

        for (File file : OriginalImagesDirectory.listFiles()) {
            BufferedImage image = UtilImageIO.loadImage(file.getPath());
            minX = image.getWidth() < minX ? minX : image.getWidth();
            minY = image.getHeight() < minY ? minY : image.getHeight();
        }
        return new Square(minX, minY);
    }

    public void resizeImage(File imageFile, int width, int height) throws IOException {

        BufferedImage originalImage = ImageIO.read(imageFile);
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();
        saveImage(resizedImage, imageFile.getName());
    }

    private void saveImage(BufferedImage imageToSave, String fileName) throws IOException {
        ImageIO.write(imageToSave, "png", new File(Constants.RESIZED_ORIGINAL_IMAGES_DIRECTORY_PATH + fileName + ".png"));
    }

}
