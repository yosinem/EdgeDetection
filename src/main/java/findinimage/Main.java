package findinimage;

import findinimage.data.CompareType;
import findinimage.data.Constants;
import findinimage.data.Square;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ImageManager imageManager = new ImageManager();
        imageManager.createSmallImagesFromScreenshot();
        Square smallerImage = imageManager.getSmallerImage();
        imageManager.resizeOriginalImages(smallerImage);// todo resize also cropped images
        ImageComparator imageComparator = new ImageComparator();

        File croppedImagesDirectory = new File(Constants.CROPPED_IMAGES_DIRECTORY_PATH);
        File[] croppedImages = croppedImagesDirectory.listFiles();

        for (int i = 0; i < 10; i++) {
            File imageToFind = croppedImages[i];
            File originalImagesDirectory = new File(Constants.RESIZED_ORIGINAL_IMAGES_DIRECTORY_PATH);
            imageComparator.compareImages(imageToFind, originalImagesDirectory, CompareType.COUPLED_HUE_SAT);
        }
    }
}
//CompareType.COUPLED_HUE_SAT 1 of 10 resized same like first
