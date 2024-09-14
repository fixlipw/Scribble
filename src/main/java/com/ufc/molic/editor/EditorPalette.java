/**
 * Copyright (c) 2007-2012, JGraph Ltd
 */
package com.ufc.molic.editor;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;

public class EditorPalette extends JPanel {

    @Serial
    private static final long serialVersionUID = 7771113885935187066L;

    protected JLabel selectedEntry = null;

    protected mxEventSource eventSource = new mxEventSource(this);

    protected Color gradientColor = null;

    public EditorPalette() {
        setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        addMouseListener(new MouseListener() {

            public void mousePressed(MouseEvent e) {
                clearSelection();
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

        });

        setTransferHandler(new TransferHandler() {
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                return true;
            }
        });
    }

    public void paintComponent(Graphics g) {
        if (gradientColor == null) {
            super.paintComponent(g);
        } else {
            Rectangle rect = getVisibleRect();

            if (g.getClipBounds() != null) {
                rect = rect.intersection(g.getClipBounds());
            }

            Graphics2D g2 = (Graphics2D) g;

            g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), 0, gradientColor));
            g2.fill(rect);
        }
    }

    public void clearSelection() {
        setSelectionEntry(null, null);
    }

    public void setSelectionEntry(JLabel entry, mxGraphTransferable t) {
        JLabel previous = selectedEntry;
        selectedEntry = entry;

        if (previous != null) {
            previous.setBorder(null);
            previous.setOpaque(false);
        }

        if (selectedEntry != null) {
            selectedEntry.setBorder(ShadowBorder.getSharedInstance());
            selectedEntry.setOpaque(true);
        }

        eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry", selectedEntry, "transferable", t, "previous", previous));
    }

    public void setPreferredWidth(int width) {
        int cols = Math.max(1, width / 55);
        setPreferredSize(new Dimension(width, (getComponentCount() * 55 / cols) + 30));
        revalidate();
    }

    public void addEdgeTemplate(final String name, ImageIcon icon, String style, int width, int height, Object value) {
        mxGeometry geometry = new mxGeometry(0, 0, width, height);
        geometry.setTerminalPoint(new mxPoint(0, height), true);
        geometry.setTerminalPoint(new mxPoint(width, 0), false);
        geometry.setRelative(true);

        mxCell cell = new mxCell(value, geometry, style);
        cell.setEdge(true);

        addTemplate(name, icon, cell);
    }


    public void addTemplate(final String name, ImageIcon icon, String style, int width, int height, Object value) {
        mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height), style);
        cell.setVertex(true);

        addTemplate(name, icon, cell);
    }

    public void addTemplate(final String name, ImageIcon icon, mxCell cell) {
        mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
        final mxGraphTransferable t = new mxGraphTransferable(new Object[]{cell}, bounds);

        final JLabel entry = new JLabel(icon);

        entry.setPreferredSize(icon == null ? new Dimension(50, 50) : new Dimension(105, 60));

        entry.setBackground(EditorPalette.this.getBackground().brighter());
        entry.setFont(new Font(entry.getFont().getFamily(), Font.PLAIN, 10));

        entry.setVerticalTextPosition(JLabel.BOTTOM);
        entry.setHorizontalTextPosition(JLabel.CENTER);
        entry.setIconTextGap(0);

        entry.setToolTipText(name);
        entry.setText(name);

        entry.addMouseListener(new MouseListener() {

            public void mousePressed(MouseEvent e) {
                setSelectionEntry(entry, t);
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

        });

        DragGestureListener dragGestureListener = e -> e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);

        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_COPY, dragGestureListener);

        add(entry);
    }

    public void addListener(String eventName, mxIEventListener listener) {
        eventSource.addListener(eventName, listener);
    }

    public boolean isEventsEnabled() {
        return eventSource.isEventsEnabled();
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        eventSource.setEventsEnabled(eventsEnabled);
    }

    public void removeListener(mxIEventListener listener) {
        eventSource.removeListener(listener);
    }

    public void removeListener(mxIEventListener listener, String eventName) {
        eventSource.removeListener(listener, eventName);
    }

}
