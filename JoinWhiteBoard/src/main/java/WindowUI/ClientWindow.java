package WindowUI;

import PaintFunction.Board;
import UserAction.Client;
import Users.UserGroup;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Objects;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class ClientWindow extends JFrame implements MyBorder, Serializable {
    public UserGroup userGroup;
    public Client client;
    public MyActionListener myActionListener = new MyActionListener(this);

    // Board layout center part:
    public Board board = new Board(this);
    public Display display = new Display();

    //The top menu bar
    public MenuBar menuBar = new MenuBar(this);

    // Right-hand side chat field
    public ChatField chatField = new ChatField();
    // Left-hand side toolbar
    public LeftSide leftSide = new LeftSide(this);

    public BoardState state = BoardState.NO_PAINT;

    public ClientWindow(UserGroup identification, String username, Client client) throws HeadlessException {
        this.userGroup = identification;
        this.client = client;
        setTitle( username + "'s " + "Draw Board");
        setSize(1800,1000);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //super.windowClosing(e);
                int result = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want leave?");
                if (result == JOptionPane.YES_OPTION) {


                    client.packing("message","request-to-exit");
                    client.connected = false;
                    System.exit(0);
                }
            }
        });
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
    private void undoAction() {
        board.removeAll();
        int index = board.paintListener.getPath().size() - 1;
        board.paintListener.getPath().remove(index);
        board.repaint();
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

    /**
     * Top menu bar initialization and button action set.
     */
    public class MenuBar extends JMenuBar{
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        JMenu tools = new JMenu("Tools");
        JMenu peers = new JMenu("Peers");
        ClientWindow clientWindow;


        // The constructor
        public MenuBar(ClientWindow clientWindow) {
            this.clientWindow = clientWindow;
            //this.board = clientWindow.board;
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.setFile();
            this.add(file);
            this.setEdit();
            this.add(edit);
            this.setHelp();
            this.add(help);
            this.setTools();
            this.add(tools);
            this.setPeers();
            this.add(peers);
        }
        private void setFile() {
            file.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
            file.setEnabled(false);

        }
        private void setEdit() {
            edit.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
            JMenuItem undo = new JMenuItem("undo");
            JMenuItem clear = new JMenuItem("clear screen");
            undo.addActionListener(e -> undoAction());
            clear.addActionListener(e -> clearOnClick());
            edit.addSeparator();
            edit.add(undo);
            edit.addSeparator();
            edit.add(clear);

        }
        private void setHelp() {
            help.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
            JMenuItem guide = new JMenuItem("user guide");
            help.add(guide);

        }
        private void setTools() {
            tools.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
            JMenuItem hide = new JMenuItem("show/hide chat box");
            tools.add(hide);
        }
        private void setPeers() {
            peers.setFont(new Font("Droid Sans Mono",Font.PLAIN,18));
            JMenuItem showPeers = new JMenuItem("Show current online peers");
            peers.add(showPeers);
        }

        public void creatOnClick() {
            // for client to receive administrator's creat command
            clientWindow.remove(clientWindow.display);
            clientWindow.add(clientWindow.board,BorderLayout.CENTER);
            clientWindow.state = BoardState.WHITE_PAINT;
            SwingUtilities.updateComponentTreeUI(clientWindow);
        }
        public void clearOnClick() {
            clientWindow.board.removeAll();
            clientWindow.board.paintListener.getPath().clear();
            clientWindow.board.repaint();
        }

    }
    class LeftSide extends JPanel {
        public ClientWindow clientWindow;
        public JToolBar letToolBar = new JToolBar();
        public JPanel leftBottom = new JPanel();
        JButton[] buttons = new JButton[5];
        //public PaintListener paintListener;

        public LeftSide(ClientWindow clientWindow) {
            this.clientWindow = clientWindow;
            this.setLayout(new BorderLayout());
            this.setLetToolBar();
            this.add(letToolBar, BorderLayout.CENTER);
            this.setLeftBottom();
            this.add(leftBottom, BorderLayout.SOUTH);
            //this.paintListener = new PaintListener(clientWindow);

        }

        private void setLetToolBar() {
            letToolBar.setOrientation(SwingConstants.VERTICAL);
            letToolBar.setLayout(new GridLayout(4,2));
            letToolBar.setFloatable(true);
            creatPaneBorder(letToolBar, "Tools");

            // normal pen icon
            JButton pen = setButton("pen.png");
            pen.addActionListener(myActionListener);
            pen.addActionListener(e -> changeSelection(pen));
            pen.setActionCommand("pen");

            // rectangle button
            JButton rectangle = setButton("rectangle.png");
            rectangle.addActionListener(myActionListener);
            rectangle.addActionListener(e -> changeSelection(rectangle));
            rectangle.setActionCommand("rectangle");

            // line button
            JButton line = setButton("Line.png");
            line.addActionListener(myActionListener);
            line.addActionListener(e -> changeSelection(line));
            line.setActionCommand("line");

            // triangle button
            JButton triangle = setButton("triangle.png");
            triangle.addActionListener(myActionListener);
            triangle.addActionListener(e -> changeSelection(triangle));
            triangle.setActionCommand("triangle");

            // circle button
            JButton circle = setButton("circle.png");
            circle.addActionListener(myActionListener);
            circle.addActionListener(e -> changeSelection(circle));
            circle.setActionCommand("circle");

            // color button
            JButton colorPicker = setButton("color-picker.png");
            colorPicker.addActionListener(e -> changeColor());
            colorPicker.setActionCommand("color-picker");

            buttons[0] = pen;
            buttons[1] = line;
            buttons[2] = rectangle;
            buttons[3] = triangle;
            buttons[4] = circle;

            letToolBar.add(pen);
            letToolBar.add(line);
            letToolBar.add(rectangle);
            letToolBar.add(triangle);
            letToolBar.add(circle);
            letToolBar.add(colorPicker);
        }
        private void setLeftBottom() {
            JSlider sizeSlider = new JSlider(1,30,1);
            sizeSlider.setOrientation(SwingConstants.VERTICAL);
            sizeSlider.setToolTipText("Resize the pen");
            sizeSlider.addChangeListener(e -> changeSize(sizeSlider));
            leftBottom.add(sizeSlider,BorderLayout.CENTER);
        }
        private JButton setButton(String filename) {
            URL url = this.getClass().getResource("/img/" + filename);
            //button.addActionListener(e -> clientWindow.chatField.chatHistory.append(filename + "\n"));
            return new JButton(new ImageIcon(Objects.requireNonNull(url)));
        }
        private void changeSelection(JButton thisButton) {
            for (JButton button:buttons) {
                if (button.getBackground() == Color.GRAY) {
                    button.setBackground(null);
                }
            }
            thisButton.setBackground(Color.GRAY);
        }
        public void changeColor() {
            Color userSelectedColor = JColorChooser.showDialog(clientWindow,"please choose your color",
                    clientWindow.board.getPenColor());
            clientWindow.board.setPenColor(userSelectedColor);
        }
        private void changeSize(JSlider sizeSlider) {
            clientWindow.board.setPenSize(sizeSlider.getValue());
        }

    }
    class Display extends JPanel {

        public Display() {
            this.setBackground(Color.DARK_GRAY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.white);
            g.setFont(new Font("Droid Sans Mono",Font.BOLD,25));

            g.drawString("If you are users:",this.getWidth()/3,this.getHeight()/3);
            g.drawString("Please wait for administrator to open a board",this.getWidth()/3,this.getHeight()/3+26);
            g.drawString("If you are administrator:",this.getWidth()/3,this.getHeight()/3+26*2);
            g.drawString("Go to File to creat new board or open an existing board",
                    this.getWidth()/3,this.getHeight()/3+26*3);
        }
    }
    public class ChatField extends JPanel {
        public JTextArea input = new JTextArea(10,20);
        public JTextArea chatHistory = new JTextArea();
        public JScrollPane scrollPane = new JScrollPane(chatHistory);
        public JScrollPane inputPane = new JScrollPane(input);
        public JButton send = new JButton("send");

        public ChatField() {
            this.setLayout(new BorderLayout());
            setInput();
            setChatHistory();
            this.add(scrollPane, BorderLayout.NORTH);
            this.add(inputPane, BorderLayout.CENTER);
            setSend();

            this.add(send, BorderLayout.SOUTH);

        }
        private void setChatHistory() {
            chatHistory.setToolTipText("chat history");
            chatHistory.setEditable(false);
            creatPaneBorder(scrollPane,"Chat");
            scrollPane.setPreferredSize(new Dimension(100,750));
        }
        private void setInput(){
            input.setToolTipText("please enter your message");
            creatPaneBorder(inputPane, "Message");
            input.addKeyListener(myActionListener);

        }
        private void setSend() {
            send.setPreferredSize(new Dimension(input.getWidth()/2,30));
            send.setActionCommand("send message");
            send.addActionListener(myActionListener);
        }

    }






}
