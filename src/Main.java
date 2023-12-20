import java.io.IOException;

public class Main {

    static  predictiveCoding Engine = new predictiveCoding();
    static void Compress(String path) throws IOException {
        Engine.Compress(path);
    }

    static void Decompress(String path) throws IOException, ClassNotFoundException {
        Engine.Decompress(path);
    }
    public static void main(String[] args) {


    }
}