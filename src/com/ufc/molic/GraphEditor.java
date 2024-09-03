package com.ufc.molic;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import com.ufc.molic.editor.BasicGraphEditor;
import com.ufc.molic.editor.EditorMenuBar;
import com.ufc.molic.editor.EditorPalette;
import com.ufc.molic.editor.GoalsTabbedPanel;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class GraphEditor extends BasicGraphEditor implements Serializable {
    @Serial
    private static final long serialVersionUID = -4601740824088314699L;

    public GraphEditor() {
        this("Scribble", new CustomGraphComponent(new CustomGraph()));
    }

    public GraphEditor(String appTitle, mxGraphComponent component) {
        super(appTitle, component);
        final mxGraph graph = graphComponent.getGraph();

        EditorPalette molicPalette = insertPalette("MoLIC");
        GoalsTabbedPanel goalsTabbedPanel = insertGoals("Objetivos");

        molicPalette.addListener(mxEvent.SELECT, (sender, evt) -> {
            Object tmp = evt.getProperty("transferable");

            if (tmp instanceof mxGraphTransferable t) {
                Object cell = t.getCells()[0];

                if (graph.getModel().isEdge(cell)) {
                    ((CustomGraph) graph).setEdgeTemplate(cell);
                }
            }
        });

        goalsTabbedPanel.addListener(mxEvent.SELECT, (sender, evt) -> {
            Object tmp = evt.getProperty("transferable");

            if (tmp instanceof mxGraphTransferable t) {
                Object cell = t.getCells()[0];

                if (graph.getModel().isEdge(cell)) {
                    ((CustomGraph) graph).setEdgeTemplate(cell);
                }
            }
        });

        // Criação do elemento ACESSO UBÍCUO
        {
            mxCell acessoUbicuo = new mxCell(
                    "",
                    new mxGeometry(0, -1.1368683772161603e-13, 100, 30),
                    "rounded=1;whiteSpace=wrap;html=1;glass=0;strokeColor=#000000;fillColor=#D1D1D1;fontFamily=Verdana;fontSize=12;arcSize=40;"
            );
            acessoUbicuo.setVertex(true);
            molicPalette.addTemplate("Acesso Ubíquo", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("acessoUbicuo.png"))), acessoUbicuo);
        }

        // Criação do elemento CENA
        {
            mxGeometry cenaGeometry = new mxGeometry(0, 0, 160.0, 90.0);
            cenaGeometry.setAlternateBounds(new mxRectangle(230.0, 280.0, 120.0, 26.0));

            mxCell tituloCena = new mxCell("Cena", cenaGeometry, "swimlane;html=1;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=26;fillColor=none;horizontalStack=0;resizeParent=1;resizeLast=1;collapsible=1;marginBottom=0;swimlaneFillColor=#ffffff;rounded=1;glass=0;");

            mxCell itensAcoes = new mxCell("Item", new mxGeometry(0, 26.0, 160.0, 64.0), "text;html=1;strokeColor=none;fillColor=none;movable=0;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;whiteSpace=wrap;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;deletable=0;cloneable=0;autosize=1");

            tituloCena.setVertex(true);
            itensAcoes.setVertex(true);
            itensAcoes.setConnectable(false);

            tituloCena.insert(itensAcoes);

            molicPalette.addTemplate("Cena", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("cena.png"))), tituloCena);
        }

        // Criação do elemento CENA DE ALERTA
        {
            mxGeometry cenaAlertaGeometry = new mxGeometry(0, 0, 203.0, 84.0);
            cenaAlertaGeometry.setAlternateBounds(new mxRectangle(230.0, 280.0, 120.0, 26.0));

            mxCell cenaAlerta = new mxCell(
                    "Confirmação",
                    cenaAlertaGeometry,
                    "swimlane;html=1;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=26;fillColor=none;horizontalStack=0;resizeParent=1;resizeLast=0;collapsible=1;marginBottom=0;swimlaneFillColor=#ffffff;rounded=1;glass=0;dashed=1;"
            );

            mxCell alertaTexto = new mxCell(
                    "d+u: confirmação.",
                    new mxGeometry(0, 26.0, 203.0, 34.0),
                    "text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;whiteSpace=wrap;overflow=hidden;movable=0;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;dashed=1;deletable=0;cloneable=0"
            );

            cenaAlerta.setVertex(true);
            alertaTexto.setVertex(true);
            alertaTexto.setConnectable(false);

            cenaAlerta.insert(alertaTexto);

            molicPalette.addTemplate("Cena de Alerta", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("cenaAlerta.png"))), cenaAlerta);
        }

        // Criação do elemento PONTO DE ABERTURA
        {
            mxCell pontoAbertura = new mxCell(
                    "",
                    new mxGeometry(0, 5.684341886080802e-14, 30, 30),
                    "ellipse;html=1;fillColor=#D1D1D1;strokeColor=#000000;"
            );
            pontoAbertura.setVertex(true);
            molicPalette.addTemplate("Ponto de Abertura", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("pontoFim.png"))), pontoAbertura);
        }

        // Criação do elemento PONTO DE ENCERRAMENTO
        {
            mxCell pontoEncerramento = new mxCell(
                    "",
                    new mxGeometry(0, 0, 23, 23),
                    "ellipse;html=1;fillColor=#000000;strokeColor=#000000;rounded=1;glass=0;fontFamily=Verdana;fontSize=12;"
            );
            pontoEncerramento.setVertex(true);
            molicPalette.addTemplate("Ponto de Encerramento", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("pontoInicio.png"))), pontoEncerramento);
        }

        // Criação do elemento PROCESSO DE SISTEMA
        {
            mxCell processoSistema = new mxCell(
                    "",
                    new mxGeometry(0, 0, 30, 29),
                    "whiteSpace=wrap;html=1;rounded=0;glass=0;strokeColor=#000000;fontFamily=Verdana;fontSize=12;fillColor=#000000;"
            );
            processoSistema.setVertex(true);
            molicPalette.addTemplate("Processo de Sistema", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("processo.png"))), processoSistema);
        }

        // Criação do elemento PROCESSO COM PROGRESSO
        {
            mxCell pontoDentroGrupo = new mxCell(
                    "",
                    new mxGeometry(0, 0, 30.0, 30.0),
                    "whiteSpace=wrap;html=1;rounded=0;glass=0;strokeColor=#000000;fontFamily=Verdana;fontSize=12;fillColor=#000000;movable=0;"
            );
            pontoDentroGrupo.setVertex(true);
            pontoDentroGrupo.setConnectable(false);

            mxGeometry processoComProgressoGeometry = new mxGeometry(0, 0, 150.0, 30.0);
            mxCell processoComProgresso = new mxCell(
                    "",
                    processoComProgressoGeometry,
                    "text;html=1;strokeColor=none;childLayout=stackLayout;fillColor=#D1D1D1;strokeColor=#000000;align=left;verticalAlign=top;spacingLeft=30;spacingRight=4;whiteSpace=wrap;overflow=hidden;movable=1;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;dashed=1;deletable=1;cloneable=0;foldable=0;"
            );
            processoComProgresso.setVertex(true);
            processoComProgresso.setConnectable(true);

            processoComProgresso.insert(pontoDentroGrupo);

            molicPalette.addTemplate("Processo com Progresso", new ImageIcon(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("processoComProgresso.png"))), processoComProgresso);

        }

    }

    /**
     *
     */
    public static class CustomGraphComponent extends mxGraphComponent {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -6833603133512882012L;

        public CustomGraphComponent(mxGraph graph) {
            super(graph);

            // Sets switches typically used in an editor
            setPageVisible(true);
            setGridVisible(true);
            setToolTips(true);
            getConnectionHandler().setCreateTarget(true);

            // Loads the default stylesheet from an external file
            mxCodec codec = new mxCodec();
            Document doc = mxUtils.loadDocument(Objects.requireNonNull(GraphEditor.class.getClassLoader().getResource("default-style.xml")).toString());
            assert doc != null;
            codec.decode(doc.getDocumentElement(), graph.getStylesheet());

            // Sets the background to white
            getViewport().setOpaque(true);
            getViewport().setBackground(Color.WHITE);
        }

        /**
         * Overrides drop behaviour to set the cell style if the target
         * is not a valid drop target and the cells are of the same
         * type (e.g. both vertices or both edges).
         */
        public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
            if (target == null && cells.length == 1 && location != null) {
                target = getCellAt(location.x, location.y);

                if (target instanceof mxICell targetCell && cells[0] instanceof mxICell dropCell) {

                    if (targetCell.isVertex() == dropCell.isVertex() || targetCell.isEdge() == dropCell.isEdge()) {
                        mxIGraphModel model = graph.getModel();
                        model.setStyle(target, model.getStyle(cells[0]));
                        graph.setSelectionCell(target);

                        return null;
                    }
                }
            }

            return super.importCells(cells, dx, dy, target, location);
        }

    }

    /**
     * A graph that creates new edges from a given template edge.
     */
    public static class CustomGraph extends mxGraph {
        /**
         * Holds the edge to be used as a template for inserting new edges.
         */
        protected Object edgeTemplate;

        /**
         * Custom graph that defines the alternate edge style to be used when
         * the middle control point of edges is double-clicked (flipped).
         */
        public CustomGraph() {
            setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
        }

        /**
         * Sets the edge template to be used to inserting edges.
         */
        public void setEdgeTemplate(Object template) {
            edgeTemplate = template;
        }

        public Object createEdge(Object parent, String id, Object value, Object source, Object target, String style) {
            if (edgeTemplate != null) {
                mxCell edge = (mxCell) cloneCells(new Object[]{edgeTemplate})[0];
                edge.setId(id);

                return edge;
            }

            return super.createEdge(parent, id, value, source, target, style);
        }

    }

    public static void main(String[] args) {
        try {
            FlatMacLightLaf.installLafInfo();
            FlatMacDarkLaf.installLafInfo();
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e1) {
            System.out.println(e1.getMessage());
        }

        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

        GraphEditor editor = new GraphEditor();
        editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
    }
}
