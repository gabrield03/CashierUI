import javax.swing.*;

/**
 * Driver class for the Cashier UI and Invoice classes
 */
public class CashierDriver
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            UI frame = new UI();
            frame.setVisible(true);
        });
    }
}