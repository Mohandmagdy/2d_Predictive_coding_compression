import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReaderWriter {

    public static int width, height;
    public static int[][] readImage(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
            height = (int) image.getHeight();
            width = (int) image.getWidth();
            int[][] data = new int[height][width];
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    int pixel = image.getRGB(w,h);
                    data[h][w] = (pixel & 0x00ff0000) >> 16;
                }
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getBufferedImage(int[][] imagePixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y < imagePixels.length && x < imagePixels[y].length) {
                    int pixelIntensity = imagePixels[y][x];
                    int gray = (pixelIntensity << 16) | (pixelIntensity << 8) | pixelIntensity;
                    image.setRGB(x, y, gray);
                }
            }
        }
        return image;
    }


    public static void writeImage(int[][] imagePixels, int width, int height, String outPath) {
        BufferedImage image = getBufferedImage(imagePixels, width, height);
        File ImageFile = new File(outPath);
        try {
            ImageIO.write(image, "jpg", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
