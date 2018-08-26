package findinimage;

import findinimage.data.Constants;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ImageManager imageManager = new ImageManager();
        imageManager.createSmallImagesFromScreenshot(Constants.SCREENSHOT_PATH);

        File imageToFind = new File("");
        File directoryContainsImages = new File("");

        imageManager.findSimilarImages(imageToFind, directoryContainsImages);

    }


}
