import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("2d predictive coding");
        setSize(600, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel();
        JButton compressButton = new JButton("Compress");
        compressButton.setFocusPainted(false);

        JButton decompressButton = new JButton("Decompress");
        decompressButton.setFocusPainted(false);
        buttonPanel.add(compressButton);
        buttonPanel.add(decompressButton);

        compressButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(compressButton, "Select file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.getSelectedFile() == null) return;
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                Main.Compress(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(buttonPanel, "Compressed Successfully!");
        });

        decompressButton.addActionListener(e -> {
            JFileChooser compressionChooser = new JFileChooser();
            compressionChooser.showDialog(compressButton, "Select compression file");
            compressionChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (compressionChooser.getSelectedFile() == null) return;
            String path = compressionChooser.getSelectedFile().getAbsolutePath();
            try {
                Main.Decompress(path);
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(buttonPanel, "Decompressed Successfully!");
        });

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        UIManager.put("Button.font", new FontUIResource(new Font("Helvetica", Font.BOLD, 20)));
        new MainFrame();
    }
}
