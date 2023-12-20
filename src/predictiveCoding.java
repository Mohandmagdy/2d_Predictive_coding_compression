import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class predictiveCoding {

    private static ScalarQuantizer scalarQuantizer = new ScalarQuantizer(4);
    public void Compress(String path) throws IOException {
        int[][] image = ReaderWriter.readImage(path);
        int originalHeight = ReaderWriter.height;
        int originalWidth = ReaderWriter.width;

        int[][] decodedImage = new int[originalHeight][originalWidth];
        int[] quantizedDifference = new int[(originalHeight*originalWidth)/8];
        int[] firstColumn = new int[originalHeight];
        int[] firstRow = new int[originalWidth];

        for (int h = 0; h < originalHeight; h++) {
            decodedImage[h][0] = image[h][0];
            firstColumn[h] = image[h][0];
        }

        for (int w = 0; w < originalWidth; w++) {
            decodedImage[0][w] = image[0][w];
            firstRow[w] = image[0][w];
        }

        int index = 0;
        for (int h = 1; h < originalHeight; h++) {
            for (int w = 1; w < originalWidth; w += 8) {
                int collected = 0;

                for (int i = 0; i < 8; i++) {
                    int predicted, difference, deQuantizedDifference, quantizedDiff, width = w + i;

                    if (width < originalWidth) {
                        if (decodedImage[h - 1][width - 1] <= Math.min(decodedImage[h - 1][width], decodedImage[h][width - 1])) {
                            predicted = Math.max(decodedImage[h - 1][width], decodedImage[h][width - 1]);
                        } else if (decodedImage[h - 1][width - 1] > Math.max(decodedImage[h - 1][width], decodedImage[h][width - 1])) {
                            predicted = Math.min(decodedImage[h - 1][width], decodedImage[h][width - 1]);
                        } else {
                            predicted = decodedImage[h - 1][width] + decodedImage[h][width - 1] - decodedImage[h - 1][width - 1];
                        }

                        difference = image[h][width] - predicted;
                        quantizedDiff = scalarQuantizer.quantize(difference);
                        collected = (collected << 4) | (quantizedDiff & 0xF);
                        deQuantizedDifference = scalarQuantizer.dequantize(quantizedDiff);
                        decodedImage[h][width] = predicted + deQuantizedDifference;
                    } else {
                        break;
                    }
                }
                if (index < originalWidth*originalHeight/8)
                    quantizedDifference[index++] = collected;
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(path.substring(0, path.lastIndexOf('.')) + ".bin");
        ObjectOutputStream ObjectOutputStream = new ObjectOutputStream(fileOutputStream);

        ObjectOutputStream.writeObject(originalWidth);
        ObjectOutputStream.writeObject(originalHeight);
        ObjectOutputStream.writeObject(firstRow);
        ObjectOutputStream.writeObject(firstColumn);
        ObjectOutputStream.writeObject(quantizedDifference);

        ObjectOutputStream.close();
    }

    public void Decompress(String path) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(path);
        ObjectInputStream input = new ObjectInputStream(file);

        int originalWidth = (int) input.readObject();
        int originalHeight = (int) input.readObject();
        int[] firstRow = (int[]) input.readObject();
        int[] firstColumn = (int[]) input.readObject();
        int[] quantizedDifference = (int[]) input.readObject();

        file.close();
        input.close();

        int[][] decodedImage = new int[originalHeight][originalWidth];

        for(int h = 0; h < originalHeight; h++)
            decodedImage[h][0] = firstColumn[h];

        for(int w = 0; w < originalWidth; w++)
            decodedImage[0][w] = firstRow[w];

        int index = 0;

        for(int h = 1; h < originalHeight; h++){
            for(int w = 1; w < originalWidth; w+=8) {
                if (index < originalWidth * originalHeight / 8) {
                    int bit = 0, collected = quantizedDifference[index++];
                    for (int i = 0; i < 8; i++) {
                        int width = w + i, deQuantizedDifference, quantizedDiff;

                        if (width < originalWidth) {
                            int predicted;

                            if (decodedImage[h - 1][width - 1] <= Math.min(decodedImage[h - 1][width], decodedImage[h][width - 1])) {
                                predicted = Math.max(decodedImage[h - 1][width], decodedImage[h][width - 1]);
                            } else if (decodedImage[h - 1][width - 1] > Math.max(decodedImage[h - 1][width], decodedImage[h][width - 1])) {
                                predicted = Math.min(decodedImage[h - 1][width], decodedImage[h][width - 1]);
                            } else {
                                predicted = decodedImage[h - 1][width] + decodedImage[h][width - 1] - decodedImage[h - 1][width - 1];
                            }
                            quantizedDiff = ((collected >> (28 - bit)) & 0xF);
                            deQuantizedDifference = scalarQuantizer.dequantize(quantizedDiff);
                            decodedImage[h][width] = predicted + deQuantizedDifference;
                            bit += 4;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        ReaderWriter.writeImage(decodedImage, originalWidth, originalHeight, path.substring(0,path.lastIndexOf('.')) + "_decompressed.jpg");
    }
}