import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SWMS_main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new WeatherMonitoringSystem().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
