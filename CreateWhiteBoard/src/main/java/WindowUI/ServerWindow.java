package WindowUI;

import PaintFunction.Board;
import Users.UserGroup;
import WebFunction.Server;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.Objects;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class ServerWindow extends JFrame implements MyBorder, Serializable {
    public Server server;
    public ServerActionListener serverActionListener = new ServerActionListener(this);

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

    public ServerWindow(Server server) throws HeadlessException {
        this.server = server;
        setTitle( "Administrator's" + "Draw Board");
        setSize(1800,1000);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(serverActionListener);
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
    class MenuBar extends JMenuBar{
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        JMenu tools = new JMenu("Tools");
        JMenu peers = new JMenu("Peers");
        ServerWindow serverWindow;


        // The constructor
        public MenuBar(ServerWindow serverWindow) {
            this.serverWindow = serverWindow;
            //this.board = mainWindow.board;
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
            JMenuItem create = new JMenuItem("new");
            JMenuItem open = new JMenuItem("open file");
            JMenuItem save = new JMenuItem("save");
            JMenuItem saveAS = new JMenuItem("save as...");
            JMenuItem close = new JMenuItem("close");

            // add corresponding action listener to each button
            create.setActionCommand("create");
            create.addActionListener(e -> creatOnClick());
            create.addActionListener(serverActionListener);
            //--------------------
            save.addActionListener(e -> saveOnClick());
            saveAS.addActionListener(e -> saveAsOnClick());
            // pack buttons to menu
            file.add(create);
            file.addSeparator();
            file.add(open);
            file.addSeparator();
            file.add(save);
            file.addSeparator();
            file.add(saveAS);
            file.addSeparator();
            file.add(close);

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

            // for administrator to create a new board
            if (serverWindow.state == BoardState.NO_PAINT) {
                serverWindow.remove(serverWindow.display);
                serverWindow.add(serverWindow.board,BorderLayout.CENTER);
                serverWindow.state = BoardState.WHITE_PAINT;
                String result = JOptionPane.showInputDialog(serverWindow,"Please name your file.");
                if (result == null) {
                    serverWindow.setTitle("Administrator's Draw Board - untitled");
                }
                else {
                    serverWindow.setTitle("Administrator's Draw Board - " +result);
                }

                SwingUtilities.updateComponentTreeUI(serverWindow);
            }
            // if the board is created but no paint on it.
            else if (serverWindow.state == BoardState.WHITE_PAINT) {
                JOptionPane.showMessageDialog(serverWindow,"Already have board");
            }
            // for painted board
            else if (serverWindow.state == BoardState.HAS_PAINT) {
                int value = JOptionPane.showConfirmDialog(serverWindow,
                        "Do you want to save current painting?",
                        "Current painting not saved!", JOptionPane.YES_NO_OPTION);
                if(value == JOptionPane.YES_OPTION){
                    System.out.println("save");
                    saveFile(true);
                }
                if(value == JOptionPane.NO_OPTION){
                    serverWindow.board.paintListener.getPath().clear();
                    serverWindow.board.repaint();
                    serverWindow.state = BoardState.WHITE_PAINT;
                }
            }

        }
        public void clearOnClick() {
            serverWindow.board.removeAll();
            serverWindow.board.paintListener.getPath().clear();
            serverWindow.board.repaint();
        }
        public void saveOnClick() {
            if (serverWindow.state == BoardState.HAS_PAINT) {
                saveFile(true);
            }
            else {
                JOptionPane.showMessageDialog(serverWindow,"No paint to save.");
            }

        }
        public void saveAsOnClick() {
            if (serverWindow.state == BoardState.HAS_PAINT) {
                saveFile(false);
            }
            else {
                JOptionPane.showMessageDialog(serverWindow,"No paint to save.");
            }

        }

        /**
         * @param autoSave true if button [save] clicked, then the file will save to default folder.
         *                 false if button [save as] clicked, user choose where to save.
         *                 this method output points collection as java serialized object.
         */
        public void saveFile(Boolean autoSave){
            //choose file location
            File file;
            if (autoSave) {
                file = new File(serverWindow.getTitle());
            }
            else {
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(serverWindow);
                file = chooser.getSelectedFile();

            }

            if(file == null) {
                JOptionPane.showMessageDialog(serverWindow, "You have not choose a file");
            }
            else {

                try {
                    // serialize object
                    FileOutputStream fis = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fis);
                    // write out data to given file
                    System.out.println(file.getPath());
                    oos.writeObject(serverWindow.board.paintListener.getPath());
                    JOptionPane.showMessageDialog(serverWindow, "Saved Successful");
                    oos.close();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(serverWindow, "Something wrong!\nPlease try again");
                }
            }
        }
    }
    class LeftSide extends JPanel {
        public ServerWindow serverWindow;
        public JToolBar letToolBar = new JToolBar();
        public JPanel leftBottom = new JPanel();
        JButton[] buttons = new JButton[5];
        //public PaintListener paintListener;

        public LeftSide(ServerWindow serverWindow) {
            this.serverWindow = serverWindow;
            this.setLayout(new BorderLayout());
            this.setLetToolBar();
            this.add(letToolBar, BorderLayout.CENTER);
            this.setLeftBottom();
            this.add(leftBottom, BorderLayout.SOUTH);
            //this.paintListener = new PaintListener(mainWindow);

        }

        private void setLetToolBar() {
            letToolBar.setOrientation(SwingConstants.VERTICAL);
            letToolBar.setLayout(new GridLayout(4,2));
            letToolBar.setFloatable(true);
            creatPaneBorder(letToolBar, "Tools");

            // normal pen icon
            JButton pen = setButton("pen.png");
            pen.addActionListener(serverActionListener);
            pen.addActionListener(e -> changeSelection(pen));
            pen.setActionCommand("pen");

            // rectangle button
            JButton rectangle = setButton("rectangle.png");
            rectangle.addActionListener(serverActionListener);
            rectangle.addActionListener(e -> changeSelection(rectangle));
            rectangle.setActionCommand("rectangle");

            // line button
            JButton line = setButton("Line.png");
            line.addActionListener(serverActionListener);
            line.addActionListener(e -> changeSelection(line));
            line.setActionCommand("line");

            // triangle button
            JButton triangle = setButton("triangle.png");
            triangle.addActionListener(serverActionListener);
            triangle.addActionListener(e -> changeSelection(triangle));
            triangle.setActionCommand("triangle");

            // circle button
            JButton circle = setButton("circle.png");
            circle.addActionListener(serverActionListener);
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
            //button.addActionListener(e -> mainWindow.chatField.chatHistory.append(filename + "\n"));
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
            Color userSelectedColor = JColorChooser.showDialog(serverWindow,"please choose your color",
                    serverWindow.board.getPenColor());
            serverWindow.board.setPenColor(userSelectedColor);
        }
        private void changeSize(JSlider sizeSlider) {
            serverWindow.board.setPenSize(sizeSlider.getValue());
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

    /**
     * This class is located left side of the main frame, perform all the chat function
     */
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
            input.addKeyListener(serverActionListener);

        }
        private void setSend() {
            send.setPreferredSize(new Dimension(input.getWidth()/2,30));
            send.setActionCommand("send message");
            send.addActionListener(serverActionListener);
        }

    }






}
