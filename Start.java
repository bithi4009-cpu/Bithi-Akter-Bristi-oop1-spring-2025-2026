
import gui.PlayerGUI;
import javax.swing.SwingUtilities;

public class Start {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlayerGUI frame = new PlayerGUI();
            frame.setVisible(true);
        });
    }
}
