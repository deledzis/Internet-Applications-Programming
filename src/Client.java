import java.nio.ByteBuffer;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.net.*;

public class Client extends JFrame {
    private JTextField mLastSetPointXCoordinateTextField, mLastSetPointYCoordinateTextField;
    private CanvasController mCanvasController;
    private static ResourceBundle mLocalizationBundle;

    private Client() { initUI(); }

    private void initUI() {
        int CENTER_PANEL_SIZE = 600, X_OFFSET = 20, Y_OFFSET = 44;

        setMinimumSize(new Dimension(CENTER_PANEL_SIZE + X_OFFSET, CENTER_PANEL_SIZE + Y_OFFSET));

        JSlider radiusSlider = createSlider();
        JLabel radiusLabel = new JLabel("R: " + radiusSlider.getValue());
        JLabel setRadiusLabel = new JLabel(mLocalizationBundle.getString("SetRadius"));
        setRadiusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Kontur.setRadius(radiusSlider.getValue());
        initLastSetPointTextFields();

        JPanel southPanelSliderPanel = new JPanel();
        southPanelSliderPanel.setLayout(new BoxLayout(southPanelSliderPanel, BoxLayout.X_AXIS));
        southPanelSliderPanel.add(Box.createRigidArea(new Dimension(getWidth() / 10, 0)));
        southPanelSliderPanel.add(radiusSlider);
        southPanelSliderPanel.add(Box.createRigidArea(new Dimension(getWidth() / 20, 0)));
        southPanelSliderPanel.add(radiusLabel);
        southPanelSliderPanel.add(Box.createRigidArea(new Dimension(getWidth() / 10, 0)));

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        southPanel.add(setRadiusLabel);
        southPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        southPanel.add(southPanelSliderPanel);
        southPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel eastPanel = packEastPanel();

        mCanvasController = new CanvasController(radiusSlider.getValue());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(mCanvasController, BorderLayout.CENTER);

        setTitle(mLocalizationBundle.getString("Title"));
        add(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        radiusSlider.addChangeListener(e -> {
            radiusLabel.setText("R: " + radiusSlider.getValue());
            mCanvasController.setRadius(radiusSlider.getValue());
            Kontur.setRadius(radiusSlider.getValue());
            Vector<Vertex> tmpVertexVector = mCanvasController.getVertexVector();
            for (Vertex vertex : tmpVertexVector)
                vertex.setColor(-1);
            mCanvasController.repaint();
        });

        mCanvasController.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mCanvasController.addPoint(new Vertex(e.getX()  - (mCanvasController.getWidth() / 2),
                        (mCanvasController.getHeight() / 2) - e.getY(), -1));
                updateTextFields(mCanvasController.getVectorVertexLastItem().getCoordinateX(),
                        mCanvasController.getVectorVertexLastItem().getCoordinateY());
                mCanvasController.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        mCanvasController.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            int tmpRadius = radiusSlider.getValue();
            if (notches < 0) {
                /* Mouse wheel moved UP */
                tmpRadius += 10;
                radiusSlider.setValue(tmpRadius);
                radiusSlider.getChangeListeners()[0].stateChanged(new ChangeEvent(e));
            } else {
                /* Mouse wheel moved DOWN */
                tmpRadius -= 10;
                radiusSlider.setValue(tmpRadius);
                radiusSlider.getChangeListeners()[0].stateChanged(new ChangeEvent(e));
            }
        });
    }

    private void initLastSetPointTextFields() {
        mLastSetPointXCoordinateTextField = new JTextField();
        mLastSetPointXCoordinateTextField.setPreferredSize(new Dimension(40, 30));
        mLastSetPointXCoordinateTextField.setMaximumSize(new Dimension(40, 30));
        mLastSetPointXCoordinateTextField.setEnabled(false);
        mLastSetPointXCoordinateTextField.setDisabledTextColor(Color.BLACK);

        mLastSetPointYCoordinateTextField = new JTextField();
        mLastSetPointYCoordinateTextField.setPreferredSize(new Dimension(40, 30));
        mLastSetPointYCoordinateTextField.setMaximumSize(new Dimension(40, 30));
        mLastSetPointYCoordinateTextField.setEnabled(false);
        mLastSetPointYCoordinateTextField.setDisabledTextColor(Color.BLACK);
    }

    private JPanel packLastSetPointPanel() {
        JLabel lastSetPointXCoordinateLabel= new JLabel("X: ");
        JLabel lastSetPointYCoordinateLabel = new JLabel("Y: ");

        JPanel eastPanelLastSetPointCoordinatesPanel = new JPanel();
        eastPanelLastSetPointCoordinatesPanel.setLayout(new BoxLayout(
                eastPanelLastSetPointCoordinatesPanel, BoxLayout.X_AXIS));
        eastPanelLastSetPointCoordinatesPanel.add(Box.createRigidArea(new Dimension(getWidth()/40, 0)));
        eastPanelLastSetPointCoordinatesPanel.add(lastSetPointXCoordinateLabel);
        eastPanelLastSetPointCoordinatesPanel.add(Box.createRigidArea(new Dimension(getWidth()/50, 0)));
        eastPanelLastSetPointCoordinatesPanel.add(mLastSetPointXCoordinateTextField);
        eastPanelLastSetPointCoordinatesPanel.add(Box.createRigidArea(new Dimension(getWidth()/50, 0)));
        eastPanelLastSetPointCoordinatesPanel.add(lastSetPointYCoordinateLabel);
        eastPanelLastSetPointCoordinatesPanel.add(Box.createRigidArea(new Dimension(getWidth()/50, 0)));
        eastPanelLastSetPointCoordinatesPanel.add(mLastSetPointYCoordinateTextField);
        eastPanelLastSetPointCoordinatesPanel.add(Box.createRigidArea(new Dimension(getWidth()/40, 0)));

        return eastPanelLastSetPointCoordinatesPanel;
    }

    private JSlider createSlider() {
        JSlider radiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 100);
        radiusSlider.setMinorTickSpacing(10);
        radiusSlider.setMajorTickSpacing(50);
        radiusSlider.setSnapToTicks(true);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);

        return radiusSlider;
    }

    private JPanel packEastPanel() {
        JLabel lastSetPointCoordinatesFirstPartLabel = new JLabel(mLocalizationBundle.getString("lastPointFirst"));
        JLabel lastSetPointCoordinatesSecondPartLabel = new JLabel(mLocalizationBundle.getString("lastPointSecond"));
        lastSetPointCoordinatesFirstPartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lastSetPointCoordinatesSecondPartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel addSomePointLabel = new JLabel(mLocalizationBundle.getString("addPoint"));
        JLabel addSomePointXCoordinateLabel = new JLabel(mLocalizationBundle.getString("XCoordinate"));
        JLabel addSomePointYCoordinateLabel = new JLabel(mLocalizationBundle.getString("YCoordinate"));
        addSomePointLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addSomePointXCoordinateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addSomePointYCoordinateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        DefaultListModel<Double> defaultListModel = new DefaultListModel<>();
        for (int i = -10; i <= 10; i++)
            defaultListModel.addElement((double) (i * 10));
        JList<Double> xCoordinatesList = new JList<>(defaultListModel);
        JScrollPane eastPanelScrollPane = new JScrollPane(xCoordinatesList);

        JCheckBox addSomePointYFirstCoordinateBox = new JCheckBox("75.0");
        JCheckBox addSomePointYSecondCoordinateBox = new JCheckBox("0.0");
        JCheckBox addSomePointYThirdCoordinateBox = new JCheckBox("-50.0");

        JPanel eastPanelAddSomePointsYCheckBoxesPanel = new JPanel();
        eastPanelAddSomePointsYCheckBoxesPanel.setLayout(new BoxLayout(
                eastPanelAddSomePointsYCheckBoxesPanel, BoxLayout.X_AXIS));
        eastPanelAddSomePointsYCheckBoxesPanel.add(addSomePointYFirstCoordinateBox);
        eastPanelAddSomePointsYCheckBoxesPanel.add(addSomePointYSecondCoordinateBox);
        eastPanelAddSomePointsYCheckBoxesPanel.add(addSomePointYThirdCoordinateBox);

        JButton eastPanelAddSomePointsButton = new JButton(mLocalizationBundle.getString("add"));
        eastPanelAddSomePointsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.add(Box.createRigidArea(new Dimension(0, 80)));
        eastPanel.add(lastSetPointCoordinatesFirstPartLabel);
        eastPanel.add(lastSetPointCoordinatesSecondPartLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        eastPanel.add(packLastSetPointPanel());
        eastPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        eastPanel.add(addSomePointLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        eastPanel.add(addSomePointXCoordinateLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        eastPanel.add(eastPanelScrollPane);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        eastPanel.add(addSomePointYCoordinateLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        eastPanel.add(eastPanelAddSomePointsYCheckBoxesPanel);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        eastPanel.add(eastPanelAddSomePointsButton);
        eastPanel.add(Box.createRigidArea(new Dimension(0, 45)));

        eastPanelAddSomePointsButton.addActionListener(e -> {
            if (addSomePointYFirstCoordinateBox.isSelected())
                mCanvasController.addPoint(new Vertex(xCoordinatesList.getSelectedValue(), 75.0, -1));
            if (addSomePointYSecondCoordinateBox.isSelected())
                mCanvasController.addPoint(new Vertex(xCoordinatesList.getSelectedValue(), 0.0, -1));
            if (addSomePointYThirdCoordinateBox.isSelected())
                mCanvasController.addPoint(new Vertex(xCoordinatesList.getSelectedValue(), -50.0, -1));
            updateTextFields(mCanvasController.getVectorVertexLastItem().getCoordinateX(),
                    mCanvasController.getVectorVertexLastItem().getCoordinateY());
        });

        return eastPanel;
    }

    private void updateTextFields(double pX, double pY) {
        mLastSetPointXCoordinateTextField.setText("" + pX);
        mLastSetPointYCoordinateTextField.setText("" + pY);
    }

    public static void main (String[] args) {
        mLocalizationBundle = args.length > 0
        ? ResourceBundle.getBundle("Localization", new Locale(
                    args[args.length - 1].substring(0, 2).toLowerCase(),
                    args[args.length - 1].substring(0, 2).toUpperCase()))
        : ResourceBundle.getBundle("Localization", Locale.getDefault());

        Client lab = new Client();
        lab.setVisible(true);
    }
}

class Vertex implements Comparable {
    private double mCoordinateX;
    private double mCoordinateY;
    private int mColor;

    double getCoordinateY() {
        return mCoordinateY;
    }
    double getCoordinateX() {
        return mCoordinateX;
    }
    int getColor() {
        return mColor;
    }
    void setColor(int pColor) {
        mColor = pColor;
    }

    Vertex(double pCoordinateX, double pCoordinateY, int pColor) {
        mCoordinateX = pCoordinateX;
        mCoordinateY = pCoordinateY;
        mColor = pColor;
    }

    @Override
    public int compareTo(Object o) {
        Vertex lVertex = (Vertex) o;
        if (this.getCoordinateX() == lVertex.getCoordinateX()) {
            if (this.getCoordinateY() == lVertex.getCoordinateY())
                return 0;
            else if (this.getCoordinateY() > lVertex.getCoordinateY())
                return 1;
            else
                return -1;
        }
        else if (this.getCoordinateX() > lVertex.getCoordinateX())
            return 1;
        else
            return -1;
    }
}

class CanvasController extends JPanel implements Runnable {
    private int mRadius, mPort;
    private volatile Vector<Vertex> mVertexVector;
    private final static int SERVER_ANSWER_PACKET_SIZE = 9;
    private final static int POINT_DATA_PACKET_SIZE = 40;

    Vector<Vertex> getVertexVector() {
        return mVertexVector;
    }
    Vertex getVectorVertexLastItem() {
        return mVertexVector.lastElement();
    }
    void setRadius(int pRadius) {
        mRadius = pRadius;
    }

    CanvasController(int pRadius) {
        mRadius = pRadius;
        mVertexVector = new Vector<>();

        // looking for available port
        for (int i = 8900; i > 0; i++) {
            if (isPortAvailable(i)) {
                mPort = i;
                break;
            }
        }

        new Thread(this).start();
    }

    private static boolean isPortAvailable (int port) {
        System.out.print("port " + port + ": ");

        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            System.out.println("available");
            return true;
        } catch (Exception ignored) { }
        finally {
            if (ds != null)
                ds.close();
        }

        System.out.println("unavailable");
        return false;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(mPort, InetAddress.getLocalHost());
            while (true) {
                byte[] receivedBuffer = new byte[SERVER_ANSWER_PACKET_SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(receivedBuffer, SERVER_ANSWER_PACKET_SIZE);
                socket.receive(receivedPacket);
                int color = ByteBuffer.wrap(receivedPacket.getData(), 0, 1).get();
                int position = ByteBuffer.wrap(receivedPacket.getData(), 1, 8).getInt();
                mVertexVector.get(position).setColor(color);
                repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double[] getXAxisTicks() {
        double increment = (getSize().getWidth() / 2) / 5;
        double currentTick = -1 * ((getSize().getWidth() / 2));
        double[] tick = new double[9];
        for (int i = 0; i < 9; i++) {
            currentTick += increment;
            tick[i] = Math.round(currentTick * 100.0) / 100.0;
        }
        return tick;
    }

    private double[] getYAxisTicks() {
        double increment = (getSize().getHeight() / 2) / 5;
        double currentTick = -1 * ((getSize().getHeight() / 2));
        double[] tick = new double[9];
        for (int i = 0; i < 9; i++) {
            currentTick += increment;
            tick[i] = Math.round(currentTick * 100.0) / 100.0;
        }
        return tick;
    }

    private void drawArea(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setBackground(Color.WHITE);
        g2d.setPaint(new Color(0, 0, 0));
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);
        g2d.fillRect(getWidth() / 2 - mRadius, getHeight() / 2, mRadius, mRadius);
        g2d.fillArc(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, mRadius * 2, mRadius * 2, 270, 90);
        g2d.fill(new Polygon( /* Triangle */
                        new int[] /* X-coordinates of points*/
                                {
                                        getWidth() / 2 - mRadius,
                                        getWidth() / 2,
                                        getWidth() / 2
                                },
                        new int[] /* Y-coordinates of points*/
                                {
                                        getHeight() / 2,
                                        getHeight() / 2 - (mRadius / 2),
                                        getHeight() / 2
                                },
                        3 /* number of points*/
                )
        );

        g2d.setPaint(Color.BLUE);
        g2d.fill(new Polygon( /* Y-axis direction triangle */
                        new int[] /* X-coordinates of points*/
                                {
                                        getWidth() / 2 - 5,
                                        getWidth() / 2,
                                        getWidth() / 2 + 5
                                },
                        new int[] /* Y-coordinates of points*/
                                {
                                        10,
                                        0,
                                        10
                                },
                        3 /* number of points*/
                )
        );

        g2d.fill(new Polygon( /* X-axis direction triangle */
                        new int[] /* X-coordinates of points*/
                                {
                                        getWidth(),
                                        getWidth() - 10,
                                        getWidth() - 10
                                },
                        new int[] /* Y-coordinates of points*/
                                {
                                        getHeight() / 2,
                                        getHeight() / 2 - 5,
                                        getHeight() / 2 + 5
                                },
                        3 /* number of points*/
                )
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawArea(g);

        double[] xAxisTicks = getXAxisTicks();
        int x_x = getWidth() / 10;
        int y_x = getHeight() / 2;

        double[] yAxisTicks = getYAxisTicks();
        int x_y = getWidth() / 2;
        int y_y = getHeight() / 10;

        int vA = 8;

        g.setColor(new Color(10, 10, 200));
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        for (int i = 0; i < 9; i++) {
            g.drawLine(x_x, y_x + 5, x_x, y_x - 5); // drawing tick lines for X axis
            g.drawLine(x_y - 5, y_y, x_y + 5, y_y); // drawing tick lines for Y axis
            if (i != 4) {
                /* Painting values for X axis ticks */
                g.drawString(xAxisTicks[i] + "", x_x - getWidth() / 60, y_x + getHeight() / 30);
                /* Painting values for Y axis ticks */
                g.drawString(yAxisTicks[vA] + "", x_y + getWidth() / 60, y_y + getHeight() / 60);
            }
            x_x += getWidth() / 10;
            y_y += getHeight() / 10;
            vA--;
        }

        for (Vertex vertex : mVertexVector) {
            switch (vertex.getColor()) {
                case  1:    g.setColor(new Color(10, 200, 10));
                            break;
                case  0:    g.setColor(new Color(100, 10, 10));
                            break;
                case -1:    g.setColor(new Color(100, 100, 100));
                            try {
                                DatagramSocket socket = new DatagramSocket() ;
                                byte[] pointDataBytes = new byte[POINT_DATA_PACKET_SIZE];
                                ByteBuffer.wrap(pointDataBytes).putDouble(vertex.getCoordinateX());
                                ByteBuffer.wrap(pointDataBytes, 8, 8).putDouble(vertex.getCoordinateY());
                                ByteBuffer.wrap(pointDataBytes, 16, 8).putDouble(mRadius);
                                ByteBuffer.wrap(pointDataBytes, 24, 8).putInt(mVertexVector.indexOf(vertex));
                                ByteBuffer.wrap(pointDataBytes, 32, 8).putInt(mPort);
                                DatagramPacket packet = new DatagramPacket(pointDataBytes, POINT_DATA_PACKET_SIZE, InetAddress.getByName("192.168.56.1"), 8099);
                                socket.send(packet);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                default:    break;
            }
            g.fillOval((int) (vertex.getCoordinateX() + (this.getWidth()/2) - 3), (int) (this.getHeight()/2 - vertex.getCoordinateY() - 3), 6, 6);
        }
    }

    void addPoint(Vertex pVertex) {
        if (!isAlreadyExist(pVertex)) {
            mVertexVector.add(pVertex);
            repaint();
        }
    }

    private boolean isAlreadyExist(Vertex pVertex) {
        for (Vertex vertex : mVertexVector) {
            if (vertex.compareTo(pVertex) == 0)
                return true;
        }
        return false;
    }
}