/*
 * Copyright (c) 2001-2012, JGraph Ltd
 */
package com.ufc.molic.editor;

import com.mxgraph.analysis.mxDistanceCostFunction;
import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.util.*;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class EditorActions {
    public static BasicGraphEditor getEditor(ActionEvent e) {
        if (e.getSource() instanceof Component component) {

            while (component != null && !(component instanceof BasicGraphEditor)) {
                component = component.getParent();
            }

            return (BasicGraphEditor) component;
        }

        return null;
    }

    public static class ToggleRulersItem extends JCheckBoxMenuItem {

        public ToggleRulersItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(editor.getGraphComponent().getColumnHeader() != null);

            addActionListener(e -> {
                mxGraphComponent graphComponent = editor.getGraphComponent();

                if (graphComponent.getColumnHeader() != null) {
                    graphComponent.setColumnHeader(null);
                    graphComponent.setRowHeader(null);
                } else {
                    graphComponent.setColumnHeaderView(new EditorRuler(graphComponent, EditorRuler.ORIENTATION_HORIZONTAL));
                    graphComponent.setRowHeaderView(new EditorRuler(graphComponent, EditorRuler.ORIENTATION_VERTICAL));
                }
            });
        }
    }


    public static class ToggleGridItem extends JCheckBoxMenuItem {

        public ToggleGridItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(true);

            addActionListener(e -> {
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                boolean enabled = !graph.isGridEnabled();

                graph.setGridEnabled(enabled);
                graphComponent.setGridVisible(enabled);
                graphComponent.repaint();
                setSelected(enabled);
            });
        }
    }

    public static class ToggleOutlineItem extends JCheckBoxMenuItem {

        public ToggleOutlineItem(final BasicGraphEditor editor, String name) {
            super(name);
            setSelected(true);

            addActionListener(e -> {
                final mxGraphOutline outline = editor.getGraphOutline();
                outline.setVisible(!outline.isVisible());
                outline.revalidate();

                SwingUtilities.invokeLater(() -> {
                    if (outline.getParent() instanceof JSplitPane) {
                        if (outline.isVisible()) {
                            ((JSplitPane) outline.getParent()).setDividerLocation(editor.getHeight() - 300);
                            ((JSplitPane) outline.getParent()).setDividerSize(6);
                        } else {
                            ((JSplitPane) outline.getParent()).setDividerSize(0);
                        }
                    }
                });
            });
        }
    }

    public static class ExitAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                editor.exit();
            }
        }
    }

    public static class StylesheetAction extends AbstractAction {

        protected String stylesheet;


        public StylesheetAction(String stylesheet) {
            this.stylesheet = stylesheet;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                mxGraph graph = graphComponent.getGraph();
                mxCodec codec = new mxCodec();
                Document doc = mxUtils.loadDocument(Objects.requireNonNull(EditorActions.class.getClassLoader().getResource(stylesheet)).toString());

                if (doc != null) {
                    codec.decode(doc.getDocumentElement(), graph.getStylesheet());
                    graph.refresh();
                }
            }
        }
    }


    public static class ZoomPolicyAction extends AbstractAction {

        protected int zoomPolicy;

        public ZoomPolicyAction(int zoomPolicy) {
            this.zoomPolicy = zoomPolicy;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                graphComponent.setPageVisible(true);
                graphComponent.setZoomPolicy(zoomPolicy);
            }
        }
    }

    public static class GridStyleAction extends AbstractAction {

        protected int style;

        public GridStyleAction(int style) {
            this.style = style;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                graphComponent.setGridStyle(style);
                graphComponent.repaint();
            }
        }
    }

    public static class GridColorAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                Color newColor = JColorChooser.showDialog(graphComponent, "Cor da Grade", graphComponent.getGridColor());

                if (newColor != null) {
                    graphComponent.setGridColor(newColor);
                    graphComponent.repaint();
                }
            }
        }
    }

    public static class ScaleAction extends AbstractAction {
        /**
         *
         */
        protected double scale;

        /**
         *
         */
        public ScaleAction(double scale) {
            this.scale = scale;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                double scale = this.scale;

                if (scale == 0) {
                    String value = (String) JOptionPane.showInputDialog(graphComponent, "Valor", "Escala" + " (%)", JOptionPane.PLAIN_MESSAGE, null, null, "");

                    if (value != null) {
                        scale = Double.parseDouble(value.replace("%", "")) / 100;
                    }
                }

                if (scale > 0) {
                    graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
                }
            }
        }
    }

    public static class PageSetupAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                PrinterJob pj = PrinterJob.getPrinterJob();
                PageFormat format = pj.pageDialog(graphComponent.getPageFormat());

                if (format != null) {
                    graphComponent.setPageFormat(format);
                    graphComponent.zoomAndCenter();
                }
            }
        }
    }

    public static class PrintAction extends AbstractAction {
        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                PrinterJob pj = PrinterJob.getPrinterJob();

                if (pj.printDialog()) {
                    PageFormat pf = graphComponent.getPageFormat();
                    Paper paper = new Paper();
                    double margin = 36;
                    paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
                    pf.setPaper(paper);
                    pj.setPrintable(graphComponent, pf);

                    try {
                        pj.print();
                    } catch (PrinterException e2) {
                        System.out.println(e2.getMessage());
                    }
                }
            }
        }
    }

    public static class SaveGoalsAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            BasicGraphEditor editor = getEditor(e);
            GoalsDialogFrame goals = new GoalsDialogFrame(e);
            assert editor != null;
            JFrame frame = (JFrame) SwingUtilities.windowForComponent(editor.graphComponent);

            if (frame != null) {

                int x = frame.getX() + (frame.getWidth() - goals.getWidth()) / 2;
                int y = frame.getY() + (frame.getHeight() - goals.getHeight()) / 2;
                goals.setLocation(x, y);

                goals.setVisible(true);

            }

        }

    }

    public static class SaveAction extends AbstractAction {
        /**
         *
         */
        protected boolean showDialog;

        /**
         *
         */
        protected String lastDir = null;

        /**
         *
         */
        public SaveAction(boolean showDialog) {
            this.showDialog = showDialog;
        }

        /**
         * Saves XML+PNG format.
         */
        protected void saveXmlPng(BasicGraphEditor editor, String filename, Color bg) throws IOException {
            mxGraphComponent graphComponent = editor.getGraphComponent();
            mxGraph graph = graphComponent.getGraph();

            // Creates the image for the PNG file
            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, bg, graphComponent.isAntiAlias(), null, graphComponent.getCanvas());

            // Creates the URL-encoded XML data
            mxCodec codec = new mxCodec();
            String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(graph.getModel())), StandardCharsets.UTF_8);
            mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
            param.setCompressedText(new String[]{"mxGraphModel", xml});

            // Saves as a PNG file
            try (FileOutputStream outputStream = new FileOutputStream(filename)) {
                mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream, param);

                encoder.encode(image);

                editor.setModified(false);
                editor.setCurrentFile(new File(filename));
            }
        }

        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                FileFilter selectedFilter = null;
                DefaultFileFilter mxeFilter = new DefaultFileFilter(".mxe", "arquivo " + "MoLIC Editor" + " (.mxe)");
                String filename;
                boolean dialogShown = false;

                if (showDialog || editor.getCurrentFile() == null) {
                    String wd;

                    if (lastDir != null) {
                        wd = lastDir;
                    } else if (editor.getCurrentFile() != null) {
                        wd = editor.getCurrentFile().getParent();
                    } else {
                        wd = System.getProperty("user.dir");
                    }

                    JFileChooser fc = new JFileChooser(wd);

                    // Adds the default file format
                    fc.addChoosableFileFilter(mxeFilter);

                    fc.addChoosableFileFilter(new DefaultFileFilter(".svg",  "arquivo "  + "SVG"+ " (.svg)"));

                    // Adds a filter for each supported image format
                    Object[] imageFormats = ImageIO.getReaderFormatNames();

                    // Finds all distinct extensions
                    HashSet<String> formats = new HashSet<>();

                    for (Object imageFormat : imageFormats) {
                        String ext = imageFormat.toString().toLowerCase();
                        formats.add(ext);
                    }

                    // Adds filter that accepts all supported image formats
                    fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter("Todas as Imagens"));
                    fc.setFileFilter(mxeFilter);
                    int rc = fc.showDialog(null, "Salvar");
                    dialogShown = true;

                    if (rc != JFileChooser.APPROVE_OPTION) {
                        return;
                    } else {
                        lastDir = fc.getSelectedFile().getParent();
                    }

                    filename = fc.getSelectedFile().getAbsolutePath();
                    selectedFilter = fc.getFileFilter();

                    if (selectedFilter instanceof DefaultFileFilter) {
                        String ext = ((DefaultFileFilter) selectedFilter).getExtension();

                        if (!filename.toLowerCase().endsWith(ext)) {
                            filename += ext;
                        }
                    }

                    if (new File(filename).exists() && JOptionPane.showConfirmDialog(graphComponent, "Sobrescrever Arquivo Existente?") != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    filename = editor.getCurrentFile().getAbsolutePath();
                }

                try {
                    String ext = filename.substring(filename.lastIndexOf('.') + 1);

                    if (ext.equalsIgnoreCase("svg")) {
                        mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, new CanvasFactory() {
                            public mxICanvas createCanvas(int width, int height) {
                                mxSvgCanvas canvas = new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height));
                                canvas.setEmbedded(true);

                                return canvas;
                            }

                        });

                        mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
                    } else if (ext.equalsIgnoreCase("html")) {
                        mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer.createHtmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
                    } else if (ext.equalsIgnoreCase("mxe") || ext.equalsIgnoreCase("xml")) {
                        mxCodec codec = new mxCodec();
                        String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

                        mxUtils.writeFile(xml, filename);

                        editor.setModified(false);
                        editor.setCurrentFile(new File(filename));
                    } else if (ext.equalsIgnoreCase("txt")) {
                        String content = mxGdCodec.encode(graph);

                        mxUtils.writeFile(content, filename);
                    } else {
                        Color bg = null;

                        if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png")) || JOptionPane.showConfirmDialog(graphComponent, "Deseja Fundo Transparente?") != JOptionPane.YES_OPTION) {
                            bg = graphComponent.getBackground();
                        }

                        if (selectedFilter == mxeFilter || (editor.getCurrentFile() != null && ext.equalsIgnoreCase("png") && !dialogShown)) {
                            saveXmlPng(editor, filename, bg);
                        } else {
                            BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, bg, graphComponent.isAntiAlias(), null, graphComponent.getCanvas());

                            if (image != null) {
                                ImageIO.write(image, ext, new File(filename));
                            } else {
                                JOptionPane.showMessageDialog(graphComponent, "Sem Dados de Imagem");
                            }
                        }
                    }
                } catch (Throwable ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(graphComponent, ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static class SelectShortestPathAction extends AbstractAction {
        /**
         *
         */
        protected boolean directed;

        /**
         *
         */
        public SelectShortestPathAction(boolean directed) {
            this.directed = directed;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                mxGraph graph = graphComponent.getGraph();
                mxIGraphModel model = graph.getModel();

                Object source = null;
                Object target = null;

                Object[] cells = graph.getSelectionCells();

                for (Object cell : cells) {
                    if (model.isVertex(cell)) {
                        if (source == null) {
                            source = cell;
                        } else {
                            target = cell;
                        }
                    }

                    if (source != null && target != null) {
                        break;
                    }
                }

                if (source != null && target != null) {
                    int steps = graph.getChildEdges(graph.getDefaultParent()).length;
                    Object[] path = mxGraphAnalysis.getInstance().getShortestPath(graph, source, target, new mxDistanceCostFunction(), steps, directed);
                    graph.setSelectionCells(path);
                } else {
                    JOptionPane.showMessageDialog(graphComponent, "Nenhuma origem e destino selecionadas");
                }
            }
        }
    }

    public static class SelectSpanningTreeAction extends AbstractAction {
        /**
         *
         */
        protected boolean directed;

        /**
         *
         */
        public SelectSpanningTreeAction(boolean directed) {
            this.directed = directed;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                mxGraph graph = graphComponent.getGraph();
                mxIGraphModel model = graph.getModel();

                Object parent = graph.getDefaultParent();
                Object[] cells = graph.getSelectionCells();

                for (Object cell : cells) {
                    if (model.getChildCount(cell) > 0) {
                        parent = cell;
                        break;
                    }
                }

                Object[] v = graph.getChildVertices(parent);
                Object[] mst = mxGraphAnalysis.getInstance().getMinimumSpanningTree(graph, v, new mxDistanceCostFunction(), directed);
                graph.setSelectionCells(mst);
            }
        }
    }

    public static class ToggleDirtyAction extends AbstractAction {
        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                graphComponent.showDirtyRectangle = !graphComponent.showDirtyRectangle;
            }
        }

    }

    public static class ToggleConnectModeAction extends AbstractAction {

        private final mxGraphComponent graphComponent;

        public ToggleConnectModeAction(final BasicGraphEditor editor) {
            super("Toggle Connect Mode");
            this.graphComponent = editor.graphComponent;

            mxConnectionHandler handler = graphComponent.getConnectionHandler();
            handler.setHandleEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (graphComponent != null) {
                mxConnectionHandler handler = graphComponent.getConnectionHandler();
                handler.setHandleEnabled(!handler.isHandleEnabled());
            }
        }
    }


    public static class ToggleCreateTargetItem extends JCheckBoxMenuItem {

        public ToggleCreateTargetItem(final BasicGraphEditor editor, String name) {
            super(name);
            mxGraphComponent graphComponent = editor.getGraphComponent();
            boolean createTarget = false;

            if (graphComponent != null) {
                mxConnectionHandler handler = graphComponent.getConnectionHandler();
                createTarget = handler.isCreateTarget();
            }

            setSelected(createTarget);

            addActionListener(e -> {
                mxGraphComponent graphComponent1 = editor.getGraphComponent();

                if (graphComponent1 != null) {
                    mxConnectionHandler handler = graphComponent1.getConnectionHandler();
                    handler.setCreateTarget(!handler.isCreateTarget());
                    setSelected(handler.isCreateTarget());
                }
            });

            if (createTarget) {
                doClick();
            }
        }
    }


    public static class PromptPropertyAction extends AbstractAction {
        /**
         *
         */
        protected Object target;

        /**
         *
         */
        protected String fieldname, message;

        /**
         *
         */
        public PromptPropertyAction(Object target, String message) {
            this(target, message, message);
        }

        /**
         *
         */
        public PromptPropertyAction(Object target, String message, String fieldname) {
            this.target = target;
            this.message = message;
            this.fieldname = fieldname;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component) {
                try {
                    Method getter = target.getClass().getMethod("get" + fieldname);
                    Object current = getter.invoke(target);

                    if (current instanceof Integer) {
                        Method setter = target.getClass().getMethod("set" + fieldname, int.class);

                        String value = (String) JOptionPane.showInputDialog((Component) e.getSource(), "Valor", message, JOptionPane.PLAIN_MESSAGE, null, null, current);

                        if (value != null) {
                            setter.invoke(target, Integer.parseInt(value));
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            // Repaints the graph component
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                graphComponent.repaint();
            }
        }
    }

    public static class TogglePropertyItem extends JCheckBoxMenuItem {

        public TogglePropertyItem(Object target, String name, String fieldname) {
            this(target, name, fieldname, false);
        }

        public TogglePropertyItem(Object target, String name, String fieldname, boolean refresh) {
            this(target, name, fieldname, refresh, null);
        }

        public TogglePropertyItem(final Object target, String name, final String fieldname, final boolean refresh, ActionListener listener) {
            super(name);

            // Since action listeners are processed last to first we add the given
            // listener here which means it will be processed after the one below
            if (listener != null) {
                addActionListener(listener);
            }

            addActionListener(new ActionListener() {
                /**
                 *
                 */
                public void actionPerformed(ActionEvent e) {
                    execute(target, fieldname, refresh);
                }
            });

            PropertyChangeListener propertyChangeListener = evt -> {
                if (evt.getPropertyName().equalsIgnoreCase(fieldname)) {
                    update(target, fieldname);
                }
            };

            if (target instanceof mxGraphComponent) {
                ((mxGraphComponent) target).addPropertyChangeListener(propertyChangeListener);
            } else if (target instanceof mxGraph) {
                ((mxGraph) target).addPropertyChangeListener(propertyChangeListener);
            }

            update(target, fieldname);
        }

        public void update(Object target, String fieldname) {
            if (target != null && fieldname != null) {
                try {
                    Method getter = target.getClass().getMethod("is" + fieldname);

                    Object current = getter.invoke(target);

                    if (current instanceof Boolean) {
                        setSelected((Boolean) current);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        
        public void execute(Object target, String fieldname, boolean refresh) {
            if (target != null && fieldname != null) {
                try {
                    Method getter = target.getClass().getMethod("is" + fieldname);
                    Method setter = target.getClass().getMethod("set" + fieldname, boolean.class);

                    Object current = getter.invoke(target);

                    if (current instanceof Boolean) {
                        boolean value = !(Boolean) current;
                        setter.invoke(target, value);
                        setSelected(value);
                    }

                    if (refresh) {
                        mxGraph graph = null;

                        if (target instanceof mxGraph) {
                            graph = (mxGraph) target;
                        } else if (target instanceof mxGraphComponent) {
                            graph = ((mxGraphComponent) target).getGraph();
                        }

                        assert graph != null;
                        graph.refresh();
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        public static void disablePropertyByDefault(Object target, String fieldname) {
            if (target != null && fieldname != null) {
                try {
                    Method setter = target.getClass().getMethod("set" + fieldname, boolean.class);
                    setter.invoke(target, false);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    public static class HistoryAction extends AbstractAction {
        /**
         *
         */
        protected boolean undo;

        /**
         *
         */
        public HistoryAction(boolean undo) {
            this.undo = undo;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                if (undo) {
                    editor.getUndoManager().undo();
                } else {
                    editor.getUndoManager().redo();
                }
            }
        }
    }

    public static class FontStyleAction extends AbstractAction {
        /**
         *
         */
        protected boolean bold;

        /**
         *
         */
        public FontStyleAction(boolean bold) {
            this.bold = bold;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                Component editorComponent = null;

                if (graphComponent.getCellEditor() instanceof mxCellEditor) {
                    editorComponent = ((mxCellEditor) graphComponent.getCellEditor()).getEditor();
                }

                if (editorComponent instanceof JEditorPane editorPane) {
                    int start = editorPane.getSelectionStart();
                    int ende = editorPane.getSelectionEnd();
                    String text = editorPane.getSelectedText();

                    if (text == null) {
                        text = "";
                    }

                    try {
                        HTMLEditorKit editorKit = new HTMLEditorKit();
                        HTMLDocument document = (HTMLDocument) editorPane.getDocument();
                        document.remove(start, (ende - start));
                        editorKit.insertHTML(document, start, ((bold) ? "<b>" : "<i>") + text + ((bold) ? "</b>" : "</i>"), 0, 0, (bold) ? HTML.Tag.B : HTML.Tag.I);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                    editorPane.requestFocus();
                    editorPane.select(start, ende);
                } else {
                    mxIGraphModel model = graphComponent.getGraph().getModel();
                    model.beginUpdate();
                    try {
                        graphComponent.stopEditing(false);
                        graphComponent.getGraph().toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE, (bold) ? mxConstants.FONT_BOLD : mxConstants.FONT_ITALIC);
                    } finally {
                        model.endUpdate();
                    }
                }
            }
        }
    }

    public static class WarningAction extends AbstractAction {
        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                Object[] cells = graphComponent.getGraph().getSelectionCells();

                if (cells != null && cells.length > 0) {
                    String warning = JOptionPane.showInputDialog("Mensagem de Erro");

                    for (Object cell : cells) {
                        graphComponent.setCellWarning(cell, warning);
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent, "Nenhuma célula selecionada");
                }
            }
        }
    }

    public static class NewAction extends AbstractAction {
        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                if (editor.isModified() || JOptionPane.showConfirmDialog(editor, "Perder Mudanças?") == JOptionPane.YES_OPTION) {
                    mxGraph graph = editor.getGraphComponent().getGraph();

                    // Check modified flag and display save dialog
                    mxCell root = new mxCell();
                    root.insert(new mxCell());
                    graph.getModel().setRoot(root);

                    editor.setModified(false);
                    editor.setCurrentFile(null);
                    editor.getGraphComponent().zoomAndCenter();
                }
            }
        }
    }

    public static class ImportAction extends AbstractAction {
        /**
         *
         */
        protected String lastDir;

        /**
         * Loads and registers the shape as a new shape in mxGraphics2DCanvas and
         * adds a new entry to use that shape in the specified palette
         *
         * @param palette The palette to add the shape to.
         * @param nodeXml The raw XML of the shape
         * @param path    The path to the directory the shape exists in
         * @return the string name of the shape
         */
        public static String addStencilShape(EditorPalette palette, String nodeXml, String path) {

            // Some editors place a 3 byte BOM at the start of files
            // Ensure the first char is a "<"
            int lessthanIndex = nodeXml.indexOf("<");
            nodeXml = nodeXml.substring(lessthanIndex);
            mxStencilShape newShape = new mxStencilShape(nodeXml);
            String name = newShape.getName();
            ImageIcon icon = null;

            if (path != null) {
                String iconPath = path + newShape.getIconPath();
                icon = new ImageIcon(iconPath);
            }

            // Registers the shape in the canvas shape registry
            mxGraphics2DCanvas.putShape(name, newShape);

            if (palette != null && icon != null) {
                palette.addTemplate(name, icon, "shape=" + name, 80, 80, "");
            }

            return name;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");

                JFileChooser fc = new JFileChooser(wd);

                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                // Adds file filter for Dia shape import
                fc.addChoosableFileFilter(new DefaultFileFilter(".shape", "Dia Shape " + "Arquivo" + " (.shape)"));

                int rc = fc.showDialog(null, "Importar Istencil");

                if (rc == JFileChooser.APPROVE_OPTION) {
                    lastDir = fc.getSelectedFile().getParent();

                    try {
                        if (fc.getSelectedFile().isDirectory()) {
                            EditorPalette palette = editor.insertPalette(fc.getSelectedFile().getName());

                            for (File f : Objects.requireNonNull(fc.getSelectedFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".shape")))) {
                                String nodeXml = mxUtils.readFile(f.getAbsolutePath());
                                addStencilShape(palette, nodeXml, f.getParent() + File.separator);
                            }

                            JComponent scrollPane = (JComponent) palette.getParent().getParent();
                            editor.getLibraryPane().setSelectedComponent(scrollPane);

                        } else {
                            String nodeXml = mxUtils.readFile(fc.getSelectedFile().getAbsolutePath());
                            String name = addStencilShape(null, nodeXml, null);

                            JOptionPane.showMessageDialog(editor, "Istencil impotado:" + name);
                        }
                    } catch (IOException e1) {
                        System.out.println(e1.getMessage());
                    }
                }
            }
        }
    }


    public static class OpenAction extends AbstractAction {
        /**
         *
         */
        protected String lastDir;

        /**
         *
         */
        protected void resetEditor(BasicGraphEditor editor) {
            editor.setModified(false);
            editor.getUndoManager().clear();
            editor.getGraphComponent().zoomAndCenter();
        }

        /**
         * Reads XML+PNG format.
         */
        protected void openXmlPng(BasicGraphEditor editor, File file) throws IOException {
            Map<String, String> text = mxPngTextDecoder.decodeCompressedText(new FileInputStream(file));

            if (text != null) {
                String value = text.get("mxGraphModel");

                if (value != null) {
                    Document document = mxXmlUtils.parseXml(URLDecoder.decode(value, StandardCharsets.UTF_8));
                    mxCodec codec = new mxCodec(document);
                    codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());
                    editor.setCurrentFile(file);
                    resetEditor(editor);

                    return;
                }
            }

            JOptionPane.showMessageDialog(editor, "Imagem não contém nenhum diagrama");
        }

        protected void openGD(BasicGraphEditor editor, File file, String gdText) {
            mxGraph graph = editor.getGraphComponent().getGraph();

            // Replaces file extension with .mxe
            String filename = file.getName();
            filename = filename.substring(0, filename.length() - 4) + ".mxe";

            if (new File(filename).exists() && JOptionPane.showConfirmDialog(editor, "Sobrescrever Arquivo Existente") != JOptionPane.YES_OPTION) {
                return;
            }

            ((mxGraphModel) graph.getModel()).clear();
            mxGdCodec.decode(gdText, graph);
            editor.getGraphComponent().zoomAndCenter();
            editor.setCurrentFile(new File(lastDir + "/" + filename));
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            BasicGraphEditor editor = getEditor(e);

            if (editor != null) {
                if (editor.isModified() || JOptionPane.showConfirmDialog(editor, "Perder mudanças?") == JOptionPane.YES_OPTION) {
                    mxGraph graph = editor.getGraphComponent().getGraph();

                    if (graph != null) {
                        String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");

                        JFileChooser fc = new JFileChooser(wd);

                        // Adds file filter for supported file format
                        DefaultFileFilter defaultFilter = new DefaultFileFilter(".mxe", "Todos os formatos suportados" + " (.mxe, .png, .vdx)") {

                            public boolean accept(File file) {
                                String lcase = file.getName().toLowerCase();

                                return super.accept(file) || lcase.endsWith(".png") || lcase.endsWith(".vdx");
                            }
                        };
                        fc.addChoosableFileFilter(defaultFilter);

                        fc.addChoosableFileFilter(new DefaultFileFilter(".mxe", "mxGraph Editor " + "Arquivo" + " (.mxe)"));
                        fc.addChoosableFileFilter(new DefaultFileFilter(".png", "PNG+XML  " + "Arquivo" + " (.png)"));

                        // Adds file filter for VDX import
                        fc.addChoosableFileFilter(new DefaultFileFilter(".vdx", "XML Drawing  " + "Arquivo" + " (.vdx)"));

                        // Adds file filter for GD import
                        fc.addChoosableFileFilter(new DefaultFileFilter(".txt", "Graph Drawing  " + "Arquivo" + " (.txt)"));

                        fc.setFileFilter(defaultFilter);

                        int rc = fc.showDialog(null, "Abrir Arquivo");

                        if (rc == JFileChooser.APPROVE_OPTION) {
                            lastDir = fc.getSelectedFile().getParent();

                            try {
                                if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".png")) {
                                    openXmlPng(editor, fc.getSelectedFile());
                                } else if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".txt")) {
                                    openGD(editor, fc.getSelectedFile(), mxUtils.readFile(fc.getSelectedFile().getAbsolutePath()));
                                } else {
                                    Document document = mxXmlUtils.parseXml(mxUtils.readFile(fc.getSelectedFile().getAbsolutePath()));

                                    mxCodec codec = new mxCodec(document);
                                    codec.decode(document.getDocumentElement(), graph.getModel());
                                    editor.setCurrentFile(fc.getSelectedFile());

                                    resetEditor(editor);
                                }
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                                JOptionPane.showMessageDialog(editor.getGraphComponent(), ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class ToggleAction extends AbstractAction {
        /**
         *
         */
        protected String key;

        /**
         *
         */
        protected boolean defaultValue;

        public ToggleAction(String key) {
            this(key, false);
        }

        public ToggleAction(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null) {
                graph.toggleCellStyles(key, defaultValue);
            }
        }
    }

    public static class SetLabelPositionAction extends AbstractAction {
        /**
         *
         */
        protected String labelPosition, alignment;

        public SetLabelPositionAction(String labelPosition, String alignment) {
            this.labelPosition = labelPosition;
            this.alignment = alignment;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.getModel().beginUpdate();
                try {
                    // Checks the orientation of the alignment to use the correct constants
                    if (labelPosition.equals(mxConstants.ALIGN_LEFT) || labelPosition.equals(mxConstants.ALIGN_CENTER) || labelPosition.equals(mxConstants.ALIGN_RIGHT)) {
                        graph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, labelPosition);
                        graph.setCellStyles(mxConstants.STYLE_ALIGN, alignment);
                    } else {
                        graph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, labelPosition);
                        graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, alignment);
                    }
                } finally {
                    graph.getModel().endUpdate();
                }
            }
        }
    }

    public static class SetStyleAction extends AbstractAction {
        /**
         *
         */
        protected String value;

        public SetStyleAction(String value) {
            this.value = value;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.setCellStyle(value);
            }
        }
    }

    public static class KeyValueAction extends AbstractAction {
        /**
         *
         */
        protected String key, value;

        @SuppressWarnings("unused")
        public KeyValueAction(String key) {
            this(key, null);
        }

        public KeyValueAction(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.setCellStyles(key, value);
            }
        }
    }

    public static class PromptValueAction extends AbstractAction {
        /**
         *
         */
        protected String key, message;

        public PromptValueAction(String key, String message) {
            this.key = key;
            this.message = message;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component) {
                mxGraph graph = mxGraphActions.getGraph(e);

                if (graph != null && !graph.isSelectionEmpty()) {
                    String value = (String) JOptionPane.showInputDialog((Component) e.getSource(), "Valor", message, JOptionPane.PLAIN_MESSAGE, null, null, "");

                    if (value != null) {
                        if (value.equals(mxConstants.NONE)) {
                            value = null;
                        }

                        graph.setCellStyles(key, value);
                    }
                }
            }
        }
    }

    public static class AlignCellsAction extends AbstractAction {
        /**
         *
         */
        protected String align;

        public AlignCellsAction(String align) {
            this.align = align;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                graph.alignCells(align);
            }
        }
    }

    public static class AutosizeAction extends AbstractAction {
        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = mxGraphActions.getGraph(e);

            if (graph != null && !graph.isSelectionEmpty()) {
                Object[] cells = graph.getSelectionCells();
                mxIGraphModel model = graph.getModel();

                model.beginUpdate();
                try {
                    for (Object cell : cells) {
                        graph.updateCellSize(cell);
                    }
                } finally {
                    model.endUpdate();
                }
            }
        }
    }

    public static class ColorAction extends AbstractAction {
        /**
         *
         */
        protected String name, key;

        public ColorAction(String name, String key) {
            this.name = name;
            this.key = key;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                mxGraph graph = graphComponent.getGraph();

                if (!graph.isSelectionEmpty()) {
                    Color newColor = JColorChooser.showDialog(graphComponent, name, null);

                    if (newColor != null) {
                        graph.setCellStyles(key, mxUtils.hexString(newColor));
                    }
                }
            }
        }
    }

    public static class BackgroundImageAction extends AbstractAction {
        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                String value = (String) JOptionPane.showInputDialog(graphComponent, "Imagem de fundo", "URL", JOptionPane.PLAIN_MESSAGE, null, null, "https://www.callatecs.com/images/background2.JPG");

                if (value != null) {
                    if (value.isEmpty()) {
                        graphComponent.setBackgroundImage(null);
                    } else {
                        Image background = mxUtils.loadImage(value);

                        if (background != null) {
                            graphComponent.setBackgroundImage(new ImageIcon(background));
                        }
                    }

                    graphComponent.getGraph().repaint();
                }
            }
        }
    }

    public static class BackgroundAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                Color newColor = JColorChooser.showDialog(graphComponent, "Fundo", null);

                if (newColor != null) {
                    graphComponent.getViewport().setOpaque(true);
                    graphComponent.getViewport().setBackground(newColor);
                }

                // Forces a repaint of the outline
                graphComponent.getGraph().repaint();
            }
        }
    }

    public static class PageBackgroundAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                Color newColor = JColorChooser.showDialog(graphComponent, "Fundo da Página", null);

                if (newColor != null) {
                    graphComponent.setPageBackgroundColor(newColor);
                }

                // Forces a repaint of the component
                graphComponent.repaint();
            }
        }
    }

    public static class StyleAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent graphComponent) {
                mxGraph graph = graphComponent.getGraph();
                String initial = graph.getModel().getStyle(graph.getSelectionCell());
                String value = (String) JOptionPane.showInputDialog(graphComponent, "Estilo", "Estilo", JOptionPane.PLAIN_MESSAGE, null, null, initial);

                if (value != null) {
                    graph.setCellStyle(value);
                }
            }
        }
    }
}
