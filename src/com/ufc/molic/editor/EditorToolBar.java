package com.ufc.molic.editor;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.ufc.molic.editor.EditorActions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EditorToolBar extends JToolBar {

    @Serial
    private static final long serialVersionUID = -8015443128436394471L;

    private boolean ignoreZoomChange = false;

    /**
     *
     */
    public EditorToolBar(final BasicGraphEditor editor, int orientation) {
        super(orientation);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);

        add(editor.bind("Novo", new NewAction(), "new.gif")).setToolTipText("Novo");
        add(editor.bind("Abrir", new OpenAction(), "open.gif")).setToolTipText("Abrir");
        add(editor.bind("Salvar", new SaveAction(false), "save.gif")).setToolTipText("Salvar");

        addSeparator();

        add(editor.bind("Imprimir", new PrintAction(), "print.gif")).setToolTipText("Imprimir");

        addSeparator();

        add(editor.bind("Cortar", TransferHandler.getCutAction(), "cut.gif")).setToolTipText("Cortar");
        add(editor.bind("Copiar", TransferHandler.getCopyAction(), "copy.gif")).setToolTipText("Copiar");
        add(editor.bind("Colar", TransferHandler.getPasteAction(), "paste.gif")).setToolTipText("Colar");

        addSeparator();

        add(editor.bind("Deletar", mxGraphActions.getDeleteAction(), "delete.gif")).setToolTipText("Deletar");

        addSeparator();

        add(editor.bind("Desfazer", new HistoryAction(true), "undo.gif")).setToolTipText("Desfazer");
        add(editor.bind("Refazer", new HistoryAction(false), "redo.gif")).setToolTipText("Refazer");

        addSeparator();

        // Gets the list of available fonts from the local graphics environment
        // and adds some frequently used fonts at the beginning of the list
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<String> fonts = new ArrayList<>();
        fonts.addAll(Arrays.asList("Helvetica", "Verdana", "Times New Roman", "Garamond", "Courier New", "-"));
        fonts.addAll(Arrays.asList(env.getAvailableFontFamilyNames()));

        final JComboBox<Object> fontCombo = new JComboBox<>(fonts.toArray());
        fontCombo.setEditable(true);
        fontCombo.setMinimumSize(new Dimension(120, 0));
        fontCombo.setPreferredSize(new Dimension(120, 0));
        fontCombo.setMaximumSize(new Dimension(120, 100));
        add(fontCombo);

        fontCombo.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                String font = Objects.requireNonNull(fontCombo.getSelectedItem()).toString();

                if (font != null && !font.equals("-")) {
                    mxGraph graph = editor.getGraphComponent().getGraph();
                    graph.setCellStyles(mxConstants.STYLE_FONTFAMILY, font);
                }
            }
        });

        final JComboBox<Object> sizeCombo = new JComboBox<>(new Object[]{"6pt", "8pt", "9pt", "10pt", "12pt", "14pt", "18pt", "24pt", "30pt", "36pt", "48pt", "60pt"});
        sizeCombo.setEditable(true);
        sizeCombo.setMinimumSize(new Dimension(65, 0));
        sizeCombo.setPreferredSize(new Dimension(65, 0));
        sizeCombo.setMaximumSize(new Dimension(65, 100));
        add(sizeCombo);

        sizeCombo.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                mxGraph graph = editor.getGraphComponent().getGraph();
                graph.setCellStyles(mxConstants.STYLE_FONTSIZE, Objects.requireNonNull(sizeCombo.getSelectedItem()).toString().replace("pt", ""));
            }
        });

        addSeparator();

        add(editor.bind("Negrito", new FontStyleAction(true), "bold.gif")).setToolTipText("Negrito");
        add(editor.bind("Itálico", new FontStyleAction(false), "italic.gif")).setToolTipText("Itálico");

        addSeparator();

        add(editor.bind("Esquerda", new KeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT), "left.gif")).setToolTipText("Alinhar à Esquerda");
        add(editor.bind("Centro", new KeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER), "center.gif")).setToolTipText("Centralizar");
        add(editor.bind("Direita", new KeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT), "right.gif")).setToolTipText("Alinhar à Direita");

        addSeparator();

        add(editor.bind("Fonte", new ColorAction("Font", mxConstants.STYLE_FONTCOLOR), "fontcolor.gif")).setToolTipText("Fonte");
        add(editor.bind("Linha", new ColorAction("Stroke", mxConstants.STYLE_STROKECOLOR), "linecolor.gif")).setToolTipText("Linha");
        add(editor.bind("Peenchimento", new ColorAction("Fill", mxConstants.STYLE_FILLCOLOR), "fillcolor.gif")).setToolTipText("Peenchimento");

        addSeparator();

        final mxGraphView view = editor.getGraphComponent().getGraph().getView();
        final JComboBox<Object> zoomCombo = new JComboBox<>(new Object[]{"400%", "200%", "150%", "100%", "75%", "50%", "Página", "Largura", "Tamanho Atual"});
        zoomCombo.setEditable(true);
        zoomCombo.setMinimumSize(new Dimension(75, 0));
        zoomCombo.setPreferredSize(new Dimension(75, 0));
        zoomCombo.setMaximumSize(new Dimension(75, 100));
        zoomCombo.setMaximumRowCount(9);
        add(zoomCombo);

        // Sets the zoom in the zoom combo the current value
        mxIEventListener scaleTracker = new mxIEventListener() {
            /**
             *
             */
            public void invoke(Object sender, mxEventObject evt) {
                ignoreZoomChange = true;

                try {
                    zoomCombo.setSelectedItem((int) Math.round(100 * view.getScale()) + "%");
                } finally {
                    ignoreZoomChange = false;
                }
            }
        };

        // Installs the scale tracker to update the value in the combo box
        // if the zoom is changed from outside the combo box
        view.getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
        view.getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE, scaleTracker);

        // Invokes once to sync with the actual zoom value
        scaleTracker.invoke(null, null);

        zoomCombo.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                mxGraphComponent graphComponent = editor.getGraphComponent();

                // Zoomcombo is changed when the scale is changed in the diagram
                // but the change is ignored here
                if (!ignoreZoomChange) {
                    String zoom = Objects.requireNonNull(zoomCombo.getSelectedItem()).toString();

                    switch (zoom) {
                        case "Página" -> {
                            graphComponent.setPageVisible(true);
                            graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
                        }
                        case "Largura" -> {
                            graphComponent.setPageVisible(true);
                            graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
                        }
                        case "Tamanho Atual" -> graphComponent.zoomActual();
                        default -> {
                            try {
                                zoom = zoom.replace("%", "");
                                double scale = Math.min(16, Math.max(0.01, Double.parseDouble(zoom) / 100));
                                graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(editor, ex.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }
}
