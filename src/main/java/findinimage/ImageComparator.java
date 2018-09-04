package findinimage;

import boofcv.alg.color.ColorHsv;
import boofcv.alg.descriptor.UtilFeature;
import boofcv.alg.feature.color.GHistogramFeatureOps;
import boofcv.alg.feature.color.HistogramFeatureOps;
import boofcv.alg.feature.color.Histogram_F64;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.image.ScaleOptions;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.TupleDesc_F64;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import findinimage.data.CompareType;
import org.ddogleg.nn.FactoryNearestNeighbor;
import org.ddogleg.nn.NearestNeighbor;
import org.ddogleg.nn.NnData;
import org.ddogleg.struct.FastQueue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
 * Demonstration of how to find similar images using color histograms.  findinimage.data.Constants color histograms here are treated as
 * features and extracted using a more flexible algorithm than when they are used for image processing.  It's
 * more flexible in that the bin size can be varied and n-dimensions are supported.
 * <p>
 * In this example, histograms for about 150 images are generated.  A target image is selected and the 10 most
 * similar images, according to Euclidean distance of the histograms, are found. This illustrates several concepts;
 * 1) How to construct a histogram in 1D, 2D, 3D, ..etc,  2) Histograms are just feature descriptors.
 * 3) Advantages of different color spaces.
 * <p>
 * Euclidean distance is used here since that's what the nearest-neighbor search uses.  It's possible to compare
 * two histograms using any of the distance metrics in DescriptorDistance too.
 *
 * @author Peter Abeles
 */
public class ImageComparator {

    /**
     * HSV stores color information in Hue and Saturation while intensity is in Value.  This computes a 2D histogram
     * from hue and saturation only, which makes it lighting independent.
     */

    public void compareImages(File imageToFind, File directoryContainsImages, CompareType compareType) {
        //String imagePath = UtilIO.pathExample("recognition/vacation");
        List<File> images = Arrays.asList(directoryContainsImages.listFiles());
        Collections.sort(images);
        ArrayList<File> targetImage = new ArrayList<File>();
        targetImage.add(imageToFind);
        // Different color spaces you can try
        int numberOfBands = getNumberOfBands(images.get(0));
        List<double[]> originalImagesPoints = getPoints(images, numberOfBands, compareType);

        double[] targetPoint = getPoints(targetImage, 3, compareType).get(0);
        // A few suggested image you can try searching for

        // Use a generic NN search algorithm.  This uses Euclidean distance as a distance metric.
        NearestNeighbor<File> nearestNeighbor = FactoryNearestNeighbor.exhaustive();
        FastQueue<NnData<File>> results = new FastQueue(NnData.class, true);

        nearestNeighbor.init(targetPoint.length);
        nearestNeighbor.setPoints(originalImagesPoints, images);
        nearestNeighbor.findNearest(targetPoint, -1, 10, results);

        showResults(imageToFind, results);
    }

    private List<double[]> getPoints(List<File> images, int numberOfBands, CompareType compareType) {
        List<double[]> points = new ArrayList<double[]>();
        switch (compareType) {
            case COUPLED_HUE_SAT:
                points = coupledHueSat(images, numberOfBands);
                break;
            case INDEPENDENT_HUE_SAT:
                points = independentHueSat(images);
                break;
            case COUPLED_RGB:
                points = coupledRGB(images);
                break;
            case HISTOGRAM_GRAY:
                points = histogramGray(images);
                break;
        }
        return points;
    }

    private static void showResults(File target, FastQueue<NnData<File>> results) {
        ListDisplayPanel gui = new ListDisplayPanel();

        // Add the target which the other images are being matched against
        gui.addImage(UtilImageIO.loadImage(target.getPath()), "Target", ScaleOptions.ALL);

        // The results will be the 10 best matches, but their order can be arbitrary.  For display purposes
        // it's better to do it from best fit to worst fit
        Collections.sort(results.toList(), new Comparator<NnData>() {
            public int compare(NnData o1, NnData o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });

        // Add images to GUI -- first match is always the target image, so skip it
        for (int i = 1; i < results.size; i++) {
            File file = results.get(i).data;
            double error = results.get(i).distance;
            BufferedImage image = UtilImageIO.loadImage(file.getPath());
            gui.addImage(image, String.format("Error %6.3f", error), ScaleOptions.ALL);
        }

        ShowImages.showWindow(gui, "Similar Images", true);
    }


    private static List<double[]> coupledHueSat(List<File> images, int numberOfBands) {

        List<double[]> points = new ArrayList<double[]>();

        for (File imageFile : images) {
            BufferedImage buffered = UtilImageIO.loadImage(imageFile.getPath());
            if (buffered == null) throw new RuntimeException("Can't load image!");

            Planar<GrayF32> rgb = new Planar<GrayF32>(GrayF32.class, 1, 1, numberOfBands);
            Planar<GrayF32> hsv = new Planar<GrayF32>(GrayF32.class, 1, 1, numberOfBands);
            rgb.reshape(buffered.getWidth(), buffered.getHeight());
            hsv.reshape(buffered.getWidth(), buffered.getHeight());

            ConvertBufferedImage.convertFrom(buffered, rgb, true);
            ColorHsv.rgbToHsv_F32(rgb, hsv);

            Planar<GrayF32> hs = hsv.partialSpectrum(0, 1);

            // The number of bins is an important parameter.  Try adjusting it
            Histogram_F64 histogram = new Histogram_F64(12, 12);
            histogram.setRange(0, 0, 2.0 * Math.PI); // range of hue is from 0 to 2PI
            histogram.setRange(1, 0, 1.0);         // range of saturation is from 0 to 1

            // Compute the histogram
            GHistogramFeatureOps.histogram(hs, histogram);

            UtilFeature.normalizeL2(histogram); // normalize so that image size doesn't matter

            points.add(histogram.value);
        }

        return points;
    }

    /**
     * Computes two independent 1D histograms from hue and saturation.  Less affects by sparsity, but can produce
     * worse results since the basic assumption that hue and saturation are decoupled is most of the time false.
     */
    private static List<double[]> independentHueSat(List<File> images) {
        List<double[]> points = new ArrayList<double[]>();

        // The number of bins is an important parameter.  Try adjusting it
        TupleDesc_F64 histogramHue = new TupleDesc_F64(10000);
        TupleDesc_F64 histogramValue = new TupleDesc_F64(10000);

        List<TupleDesc_F64> histogramList = new ArrayList<TupleDesc_F64>();
        histogramList.add(histogramHue);
        histogramList.add(histogramValue);

        for (File f : images) {
            BufferedImage buffered = UtilImageIO.loadImage(f.getPath());
            if (buffered == null) throw new RuntimeException("Can't load image!");

            int numberOfBands = getNumberOfBands(f);
            Planar<GrayF32> rgb = new Planar<GrayF32>(GrayF32.class, 1, 1, numberOfBands);
            Planar<GrayF32> hsv = new Planar<GrayF32>(GrayF32.class, 1, 1, numberOfBands);

            rgb.reshape(buffered.getWidth(), buffered.getHeight());
            hsv.reshape(buffered.getWidth(), buffered.getHeight());
            ConvertBufferedImage.convertFrom(buffered, rgb, true);
            ColorHsv.rgbToHsv_F32(rgb, hsv);

            GHistogramFeatureOps.histogram(hsv.getBand(0), 0, 2 * Math.PI, histogramHue);
            GHistogramFeatureOps.histogram(hsv.getBand(1), 0, 1, histogramValue);

            // need to combine them into a single descriptor for processing later on
            TupleDesc_F64 imageHist = UtilFeature.combine(histogramList, null);

            UtilFeature.normalizeL2(imageHist); // normalize so that image size doesn't matter

            points.add(imageHist.value);
        }

        return points;
    }

    private static int getNumberOfBands(File file) {
        String fileName = file.getName();

        if (fileName.endsWith(".png")) {
            return 4;
        } else {
            return 3;
        }
    }

    /**
     * Constructs a 3D histogram using RGB.  RGB is a popular color space, but the resulting histogram will
     * depend on lighting conditions and might not produce the accurate results.
     */
    private static List<double[]> coupledRGB(List<File> images) {
        List<double[]> points = new ArrayList<double[]>();

        for (File f : images) {

            int numberOfBands = getNumberOfBands(f);
            Planar<GrayF32> rgb = new Planar<GrayF32>(GrayF32.class, 1, 1, numberOfBands);

            BufferedImage buffered = UtilImageIO.loadImage(f.getPath());
            if (buffered == null) throw new RuntimeException("Can't load image!");

            rgb.reshape(buffered.getWidth(), buffered.getHeight());
            ConvertBufferedImage.convertFrom(buffered, rgb, true);

            // The number of bins is an important parameter.  Try adjusting it
            Histogram_F64 histogram = new Histogram_F64(10, 10, 10);
            histogram.setRange(0, 0, 255);
            histogram.setRange(1, 0, 255);
            histogram.setRange(2, 0, 255);

            GHistogramFeatureOps.histogram(rgb, histogram);

            UtilFeature.normalizeL2(histogram); // normalize so that image size doesn't matter

            points.add(histogram.value);
        }

        return points;
    }

    /**
     * Computes a histogram from the gray scale intensity image alone.  Probably the least effective at looking up
     * similar images.
     */
    private static List<double[]> histogramGray(List<File> images) {
        List<double[]> points = new ArrayList<double[]>();

        GrayU8 gray = new GrayU8(1, 1);
        for (File f : images) {
            BufferedImage buffered = UtilImageIO.loadImage(f.getPath());
            if (buffered == null) throw new RuntimeException("Can't load image!");

            gray.reshape(buffered.getWidth(), buffered.getHeight());
            ConvertBufferedImage.convertFrom(buffered, gray, true);

            TupleDesc_F64 imageHist = new TupleDesc_F64(150);
            HistogramFeatureOps.histogram(gray, 255, imageHist);

            UtilFeature.normalizeL2(imageHist); // normalize so that image size doesn't matter

            points.add(imageHist.value);
        }

        return points;
    }


}