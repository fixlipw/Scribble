package com.ufc.molic.editor;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.*;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.util.mxMorphing;
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
        // Stores and updates the frame title
        this.appTitle = appTitle;

        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(BasicGraphEditor.class.getClassLoader().getResource("app-icon.jpg"))).getImage());
        frame.setSize(870, 640);

        // Stores a reference to the graph and creates the command history
        graphComponent = component;
        final mxGraph graph = graphComponent.getGraph();
        undoManager = createUndoManager();

        // Do not change the scale and translation after files have been loaded
        graph.setResetViewOnRootChange(false);

        // Updates the modified flag if the graph model changes
        graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

        // Adds the command history to the model and view
        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        // Keeps the selection in sync with the command history
        mxIEventListener undoHandler = (source, evt) -> {
            List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
            graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        // Creates the graph outline component
        graphOutline = new mxGraphOutline(graphComponent);

        // Creates the library pane that contains the tabs with the palettes
        libraryPane = new JTabbedPane();
        // Creates the goals pane that contains the tabs with the goals
        goalsPane = new JTabbedPane();

        // Creates the inner split pane that contains the library with the
        // palettes and the graph outline on the left side of the window
        JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPane, graphOutline);
        inner.setDividerLocation(350);
        inner.setResizeWeight(1);
        inner.setDividerSize(6);
        inner.setBorder(null);

        // Creates the outer split pane that contains the inner split pane and
        // the graph component on the right side of the window
        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner, graphComponent);
        outer.setOneTouchExpandable(true);
        outer.setDividerLocation(250);
        outer.setDividerSize(6);
        outer.setBorder(null);

        // Creates a split pane that contains the outer pane and the goalsPane
        JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outer, goalsPane);
        main.setOneTouchExpandable(true);
        int width = SwingUtilities.getRoot(this).getWidth();
        main.setDividerLocation(width - 270);
        main.setDividerSize(6);
        main.setBorder(null);

        main.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                main.setDividerLocation(main.getWidth() - 270);
            }
        });

        // Creates the status bar
        statusBar = createStatusBar();

        // Display some useful information about repaint events
        installRepaintListener();

        // Puts everything together
        setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        installToolBar();

        // Installs rubberband selection and handling for some special
        // keystrokes such as F2, Control-C, -V, X, A etc.
        installHandlers();
        installListeners();
        updateTitle();
    }

    /**
     *
     */
    protected mxUndoManager createUndoManager() {
        return new mxUndoManager();
    }

    /**
     *
     */
    protected void installHandlers() {
        rubberband = new mxRubberband(graphComponent);
        keyboardHandler = new EditorKeyboardHandler(graphComponent);
    }

    /**
     *
     */
    protected void installToolBar() {
        add(new EditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
    }

    /**
     *
     */
    protected JLabel createStatusBar() {
        JLabel statusBar = new JLabel("Pronto");
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        return statusBar;
    }

    /**
     *
     */
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

    /**
     *
     */
    public EditorPalette insertPalette(String title) {
        palette = new EditorPalette();
        final JScrollPane scrollPane = new JScrollPane(palette);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        libraryPane.add(title, scrollPane);

        // Updates the widths of the palettes if the container size changes
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

    /**
     *
     */
    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphComponent.zoomIn();
        } else {
            graphComponent.zoomOut();
        }

        status("Escala" + ": " + (int) (100 * graphComponent.getGraph().getView().getScale()) + "%");
    }

    /**
     *
     */
    protected void showOutlinePopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);

        JCheckBoxMenuItem item = new JCheckBoxMenuItem("Ampliar Página");
        item.setSelected(graphOutline.isFitPage());

        item.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                graphOutline.setFitPage(!graphOutline.isFitPage());
                graphOutline.repaint();
            }
        });

        JCheckBoxMenuItem item2 = new JCheckBoxMenuItem("Mostrar Rótulos");
        item2.setSelected(graphOutline.isDrawLabels());

        item2.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
                graphOutline.repaint();
            }
        });

        JCheckBoxMenuItem item3 = new JCheckBoxMenuItem("Bufferizar");
        item3.setSelected(graphOutline.isTripleBuffered());

        item3.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
                graphOutline.repaint();
            }
        });

        JPopupMenu menu = new JPopupMenu();
        menu.add(item);
        menu.add(item2);
        menu.add(item3);
        menu.show(graphComponent, pt.x, pt.y);

        e.consume();
    }

    /**
     *
     */
    protected void showGraphPopupMenu(MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
        EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
        menu.show(graphComponent, pt.x, pt.y);

        e.consume();
    }

    /**
     *
     */
    protected void mouseLocationChanged(MouseEvent e) {
        status(e.getX() + ", " + e.getY());
    }

    /**
     *
     */
    protected void installListeners() {
        // Installs mouse wheel listener for zooming
        MouseWheelListener wheelTracker = new MouseWheelListener() {
            /**
             *
             */
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
                    BasicGraphEditor.this.mouseWheelMoved(e);
                }
            }

        };

        // Handles mouse wheel events in the outline and graph component
        graphOutline.addMouseWheelListener(wheelTracker);
        graphComponent.addMouseWheelListener(wheelTracker);

        // Installs the popup menu in the outline
        graphOutline.addMouseListener(new MouseAdapter() {

            /**
             *
             */
            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            /**
             *
             */
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showOutlinePopupMenu(e);
                }
            }

        });

        // Installs the popup menu in the graph component
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            /**
             *
             */
            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            /**
             *
             */
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showGraphPopupMenu(e);
                }
            }

        });

        // Installs a mouse motion listener to display the mouse location
        graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {

            /*
             * (non-Javadoc)
             * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
             */
            public void mouseDragged(MouseEvent e) {
                mouseLocationChanged(e);
            }

            /*
             * (non-Javadoc)
             * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
             */
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

    /**
     *
     */
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
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(BasicGraphEditor.class.getClassLoader().getResource("app-icon.jpg"))).getImage());
        frame.setJMenuBar(menuBar);
        frame.setSize(870, 640);

        // Updates the frame title
        updateTitle();

        return frame;
    }


    public Action graphLayout(final String key, boolean animate) {
        final mxIGraphLayout layout = createLayout(key, animate);

        if (layout != null) {
            return new AbstractAction(key) {
                public void actionPerformed(ActionEvent e) {
                    final mxGraph graph = graphComponent.getGraph();
                    Object cell = graph.getSelectionCell();

                    if (cell == null || graph.getModel().getChildCount(cell) == 0) {
                        cell = graph.getDefaultParent();
                    }

                    graph.getModel().beginUpdate();
                    try {
                        long t0 = System.currentTimeMillis();
                        layout.execute(cell);
                        status("Layout: " + (System.currentTimeMillis() - t0) + " ms");
                    } finally {
                        mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

                        morph.addListener(mxEvent.DONE, (sender, evt) -> graph.getModel().endUpdate());

                        morph.startAnimation();
                    }

                }

            };
        } else {
            return new AbstractAction(key) {

                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(graphComponent, "noLayout");
                }

            };
        }
    }

    protected mxIGraphLayout createLayout(String ident, boolean ignoredAnimate) {
        mxIGraphLayout layout = null;

        if (ident != null) {
            mxGraph graph = graphComponent.getGraph();

            layout = switch (ident) {
                case "verticalHierarchical" -> new mxHierarchicalLayout(graph);
                case "horizontalHierarchical" -> new mxHierarchicalLayout(graph, JLabel.WEST);
                case "verticalTree" -> new mxCompactTreeLayout(graph, false);
                case "horizontalTree" -> new mxCompactTreeLayout(graph, true);
                case "parallelEdges" -> new mxParallelEdgeLayout(graph);
                case "placeEdgeLabels" -> new mxEdgeLabelLayout(graph);
                case "organicLayout" -> new mxOrganicLayout(graph);
                default -> null;
            };

            switch (ident) {
                case "verticalPartition" -> layout = new mxPartitionLayout(graph, false) {
                    /**
                     * Overrides the empty implementation to return the size of the
                     * graph control.
                     */
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
                case "horizontalPartition" -> layout = new mxPartitionLayout(graph, true) {
                    /**
                     * Overrides the empty implementation to return the size of the
                     * graph control.
                     */
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
                case "verticalStack" -> layout = new mxStackLayout(graph, false) {
                    /**
                     * Overrides the empty implementation to return the size of the
                     * graph control.
                     */
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
                case "horizontalStack" -> layout = new mxStackLayout(graph, true) {
                    /**
                     * Overrides the empty implementation to return the size of the
                     * graph control.
                     */
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
                case "circleLayout" -> layout = new mxCircleLayout(graph);
            }
        }

        return layout;
    }

}
