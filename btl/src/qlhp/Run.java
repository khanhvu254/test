package qlhp;
import javax.swing.*;

public class Run {
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
