package com.ufc.molic.editor;

import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.Serial;
import java.util.List;
import java.util.Objects;

public class BasicGraphEditor extends JPanel {

    @Serial
    private static final long serialVersionUID = -6561623072112577140L;

    static {
        try {
            mxResources.add("com/ufc/molic/resources");
        } catch (Exception ignored) { }
    }

    protected mxGraphComponent graphComponent;

    protected mxGraphOutline graphOutline;

    protected JTabbedPane libraryPane;

    protected JTabbedPane goalsPane;

    protected JPanel notesPane;

    protected GoalsTabbedPanel goalsPanel;

    protected EditorPalette palette;

    protected mxUndoManager undoManager;

    protected String appTitle;

    protected JLabel statusBar;

    protected File currentFile;

    protected boolean modified = false;

    protected mxRubberband rubberband;

    protected mxKeyboardHandler keyboardHandler;

    protected mxIEventListener undoHandler = new mxIEventListener() {
        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };

    protected mxIEventListener changeTracker = (source, evt) -> setModified(true);

    public BasicGraphEditor(String appTitle, mxGraphComponent component) {
        this.appTitle = appTitle;

        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(BasicGraphEditor.class.getClassLoader().getResource("images/app-icon.jpg"))).getImage());
        frame.setSize(870, 640);

        graphComponent = component;
        final mxGraph graph = graphComponent.getGraph();
        undoManager = createUndoManager();

        graph.setResetViewOnRootChange(false);

        graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxIEventListener undoHandler = (source, evt) -> {
            List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
            graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        graphOutline = new mxGraphOutline(graphComponent);

        libraryPane = new JTabbedPane();
        goalsPane = new JTabbedPane();
        notesPane = new JPanel();

        JSplitPane innerPallete = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPane, graphOutline);
        innerPallete.setDividerLocation(350);
        innerPallete.setResizeWeight(1);
        innerPallete.setDividerSize(6);
        innerPallete.setBorder(null);

        JSplitPane outerPallete = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, innerPallete, graphComponent);
        outerPallete.setOneTouchExpandable(true);
        outerPallete.setDividerLocation(250);
        outerPallete.setDividerSize(6);
        outerPallete.setBorder(null);

        Rectangle appBounds = SwingUtilities.getRoot(this).getBounds();

        JSplitPane outterMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outerPallete, goalsPane);
        outterMain.setOneTouchExpandable(true);
        outterMain.setDividerLocation(appBounds.width - 270);
        outterMain.setDividerSize(6);
        outterMain.setBorder(null);

        outterMain.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                outterMain.setDividerLocation(outterMain.getWidth() - 270);
            }
        });

        statusBar = createStatusBar();

        installRepaintListener();

        setLayout(new BorderLayout());
        add(outterMain, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        installToolBar();

        installHandlers();
        installListeners();
        updateTitle();
    }

    protected mxUndoManager createUndoManager() {
        return new mxUndoManager();
    }

    protected void installHandlers() {
        rubberband = new mxRubberband(graphComponent);
        keyboardHandler = new EditorKeyboardHandler(graphComponent);
    }

    protected void installToolBar() {
        add(new EditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
    }

    protected JLabel createStatusBar() {
        JLabel statusBar = new JLabel("Pronto");
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        return statusBar;
    }

    protected void installRepaintListener() {
        graphComponent.getGraph().addListener(mxEvent.REPAINT, (source, evt) -> {
            String buffer = (graphComponent.getTripleBuffer() != null) ? "" : " (unbuffered)";
            mxRectangle dirty = (mxRectangle) evt.getProperty("region");

            if (dirty == null) {
                status("Repaint all" + buffer);
            } else {
                status("Repaint: x=" + (int) (dirty.getX()) + " y=" + (int) (dirty.getY()) + " w=" + (int) (dirty.getWidth()) + " h=" + (int) (dirty.getHeight()) + buffer);
            }
        });
    }

    public EditorPalette insertPalette(String title) {
        palette = new EditorPalette();
        final JScrollPane scrollPane = new JScrollPane(palette);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        libraryPane.add(title, scrollPane);

        libraryPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = scrollPane.getWidth() - scrollPane.getVerticalScrollBar().getWidth();
                palette.setPreferredWidth(w);
            }

        });

        return palette;
    }

    public GoalsTabbedPanel insertGoals(String title) {
        goalsPanel = new GoalsTabbedPanel();
        final JScrollPane scrollPane = new JScrollPane(goalsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        goalsPane.add(title, scrollPane);

        return goalsPanel;
    }

    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphComponent.zoomIn();
        } else {
            graphComponent.zoomOut();
        }

        status("Escala" + ": " + (int) (100 * graphComponent.getGraph().getView().getScale()) + "%");
    }

    protected void showOutlinePopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);

        JCheckBoxMenuItem item = new JCheckBoxMenuItem("Ampliar Página");
        item.setSelected(graphOutline.isFitPage());

        item.addActionListener(e3 -> {
            graphOutline.setFitPage(!graphOutline.isFitPage());
            graphOutline.repaint();
        });

        JCheckBoxMenuItem item2 = new JCheckBoxMenuItem("Mostrar Rótulos");
        item2.setSelected(graphOutline.isDrawLabels());

        item2.addActionListener(e2 -> {
            graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
            graphOutline.repaint();
        });

        JCheckBoxMenuItem item3 = new JCheckBoxMenuItem("Bufferizar");
        item3.setSelected(graphOutline.isTripleBuffered());

        item3.addActionListener(e1 -> {
            graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
            graphOutline.repaint();
        });

        JPopupMenu menu = new JPopupMenu();
        menu.add(item);
        menu.add(item2);
        menu.add(item3);
        menu.show(graphComponent, pt.x, pt.y);

        e.consume();
    }

    protected void showGraphPopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
        EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
        menu.show(graphComponent, pt.x, pt.y);

        e.consume();
    }

    protected void mouseLocationChanged(MouseEvent e) {
        status(e.getX() + ", " + e.getY());
    }

    protected void installListeners() {

        MouseWheelListener wheelTracker = e -> {
            if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
                BasicGraphEditor.this.mouseWheelMoved(e);
            }
        };

        graphOutline.addMouseWheelListener(wheelTracker);
        graphComponent.addMouseWheelListener(wheelTracker);

        graphOutline.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showOutlinePopupMenu(e);
                }
            }

        });

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showGraphPopupMenu(e);
                }
            }

        });

        graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                mouseLocationChanged(e);
            }

            public void mouseMoved(MouseEvent e) {
                mouseDragged(e);
            }

        });
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File file) {
        File oldValue = currentFile;
        currentFile = file;

        firePropertyChange("currentFile", oldValue, file);

        if (oldValue != file) {
            updateTitle();
        }
    }

    public boolean isModified() {
        return !modified;
    }

    public void setModified(boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;

        firePropertyChange("modified", oldValue, modified);

        if (oldValue != modified) {
            updateTitle();
        }
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    public JTabbedPane getLibraryPane() {
        return libraryPane;
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public Action bind(String name, final Action action) {
        return bind(name, action, null);
    }


    public Action bind(String name, final Action action, String iconUrl) {
        AbstractAction newAction = new AbstractAction(name, iconUrl == null ? null : new ImageIcon(Objects.requireNonNull(BasicGraphEditor.class.getClassLoader().getResource(iconUrl)))) {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
            }
        };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

    public void status(String msg) {
        statusBar.setText(msg);
    }

    public void updateTitle() {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            String title = (currentFile != null) ? currentFile.getAbsolutePath() : "novoModelo";

            if (modified) {
                title += "*";
            }

            frame.setTitle(title + " - " + appTitle);
        }
    }

    public void molic() {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {

            MolicAboutFrame molic = new MolicAboutFrame(frame);
            int x = frame.getX() + (frame.getWidth() - molic.getWidth()) / 2;
            int y = frame.getY() + (frame.getHeight() - molic.getHeight()) / 2;
            molic.setLocation(x, y);

            molic.setVisible(true);
        }
    }

    public void exit() {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            frame.dispose();
        }
    }

    public void setLookAndFeel(String clazz) {
        JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

        if (frame != null) {
            try {
                UIManager.setLookAndFeel(clazz);
                SwingUtilities.updateComponentTreeUI(frame);

                keyboardHandler = new EditorKeyboardHandler(graphComponent);
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        }
    }

    public JFrame createFrame(JMenuBar menuBar) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(BasicGraphEditor.class.getClassLoader().getResource("images/app-icon.jpg"))).getImage());
        frame.setJMenuBar(menuBar);
        frame.setSize(870, 640);

        updateTitle();

        return frame;
    }


}
