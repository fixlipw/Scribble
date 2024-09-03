package com.ufc.molic.editor;

import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.Serial;

public class GoalsTabbedPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    protected JLabel selectedGoal = null;

    protected mxEventSource eventSource = new mxEventSource(this);

    public GoalsTabbedPanel() {
        setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));

        addMouseListener(new MouseListener() {

            public void mousePressed(MouseEvent e) {

            }

            public void mouseClicked(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void setSelectionGoal(JLabel goal, mxGraphTransferable t, mxGraph graph, Object[] cells, Color color) {
        JLabel previous = selectedGoal;
        selectedGoal = goal;

        if (previous != null) {
            previous.setBorder(null);
            previous.setOpaque(false);
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Color.black), cells);
        }

        if (selectedGoal != null) {
            selectedGoal.setBorder(ShadowBorder.getSharedInstance());
            selectedGoal.setOpaque(true);
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(color), cells);
        }

        eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry", selectedGoal, "transferable", t, "previous", previous));
    }

    public void addGoals(final String name, String description, Color color, mxGraph graph, Object[] cells, mxRectangle bounds) {
        final mxGraphTransferable t = new mxGraphTransferable(cells, bounds);

        final JLabel goal = new JLabel(createColorIcon(color));

        goal.setBackground(GoalsTabbedPanel.this.getBackground().brighter());
        goal.setFont(new Font(goal.getFont().getFamily(), Font.PLAIN, 10));

        goal.setVerticalTextPosition(JLabel.BOTTOM);
        goal.setHorizontalTextPosition(JLabel.CENTER);
        goal.setIconTextGap(0);

        goal.setToolTipText("<html><b>"+name+"</b><br />"+description+"</html>");
        goal.setText(name);

        final boolean[] clicked = {false};

        goal.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (!clicked[0]) {
                    setSelectionGoal(goal, t, graph, cells, color);
                    clicked[0] = true;
                } else {
                    setSelectionGoal(goal, t, graph, cells, Color.black);
                    clicked[0] = false;
                }
            }

            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                showGoalPopupMenu(e, goal, t, graph, cells, clicked);
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }


        });

        add(goal);
    }

    private void showGoalPopupMenu(MouseEvent e, JLabel goal, mxGraphTransferable t, mxGraph graph, Object[] cells, boolean[] clicked) {
        if (e.isPopupTrigger()) {
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), this);

            JMenuItem item = new JMenuItem("Deletar Objetivo");

            item.addActionListener(e1 -> {
                setSelectionGoal(goal, t, graph, cells, Color.black);
                clicked[0] = false;

                remove(goal);
                validate();
                repaint();
            });

            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add(item);
            popupMenu.show(goal.getParent(), pt.x, pt.y);
       }
    }

    public void addListener(String eventName, mxEventSource.mxIEventListener listener) {
        eventSource.addListener(eventName, listener);
    }

    private ImageIcon createColorIcon(Color color) {
        BufferedImage image = new BufferedImage(50, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, 50, 25);
        g2d.dispose();

        return new ImageIcon(image);
    }
}
