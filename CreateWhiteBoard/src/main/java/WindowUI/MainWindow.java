package WindowUI;

import PaintFunction.Board;
import Users.UserGroup;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.Serializable;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class MainWindow extends JFrame implements MyBorder, Serializable {
    public UserGroup userGroup;

    // Board layout center part:
    public Board board = new Board(this);
    public Display display = new Display();

    //The top menu bar
    public WindowUI.MenuBar menuBar = new MenuBar(userGroup, this);

    // Right-hand side chat field
    public ChatField chatField = new ChatField();
    // Left-hand side toolbar
    public LeftSide leftSide = new LeftSide(this);

    public BoardState state = BoardState.NO_PAINT;

    public MainWindow(UserGroup identification) throws HeadlessException {
        this.userGroup = identification;
        setTitle("Draw Board");
        setSize(1800,1000);
        setDefaultCloseOperation(MainWindow.DISPOSE_ON_CLOSE);
        // when the window is initialized, put it at middle of the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
        // root container
        JPanel root = new JPanel();
        this.setContentPane(root);
        root.setLayout(new BorderLayout());

        // Top menu bar
        root.add(menuBar, BorderLayout.NORTH);


        // draw board init

        root.add(display,BorderLayout.CENTER);



        // left side of the frame
        leftSide.leftBottom.setPreferredSize(new Dimension(this.getWidth()/10,this.getHeight()/2));
        root.add(leftSide, BorderLayout.WEST);

        // right side of the frame
        root.add(chatField,BorderLayout.EAST);
        //this.pack();
        setVisible(true);

        //refresh ui
        //SwingUtilities.updateComponentTreeUI(root);
    }



    @Override
    public void creatPaneBorder(JComponent component, String title) {
        component.setBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(borderColor, thickness),
                        title,
                        TitledBorder.CENTER,TitledBorder.TOP,
                        new Font("Droid Sans Mono",Font.PLAIN,18),
                        fontColor));
    }
    /*private void undoAction() {
        board.removeAll();
        int index = board.paintListener.getPath().size() - 1;
        board.paintListener.getPath().remove(index);
        board.repaint();
    }*/

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow(UserGroup.ADMINISTRATOR);
    }


}
