import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Line Strike");

        frame.setSize(400, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);

        frame.setVisible(true);
    }
}