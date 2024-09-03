package com.ufc.molic.editor;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.ufc.molic.editor.EditorActions.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.Arrays;

public class EditorMenuBar extends JMenuBar {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4060203894740766714L;

    public EditorMenuBar(final BasicGraphEditor editor) {
        final mxGraphComponent graphComponent = editor.getGraphComponent();
        final mxGraph graph = graphComponent.getGraph();

        JMenu menu;
        JMenu submenu;

        menu = add(new JMenu("Arquivo"));

        menu.add(editor.bind("Novo", new NewAction(), "new.gif"));
        menu.add(editor.bind("Abrir Arquivo", new OpenAction(), "open.gif"));
        menu.add(editor.bind("Importar Stencil", new ImportAction(), "open.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Salvar", new SaveAction(false), "save.gif"));
        menu.add(editor.bind("Salvar Como", new SaveAction(true), "saveas.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Configuração de Página", new PageSetupAction(), "pagesetup.gif"));
        menu.add(editor.bind("Imprimir", new PrintAction(), "print.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Sair", new ExitAction()));

        menu = add(new JMenu("Editar"));

        menu.add(editor.bind("Desfazer", new HistoryAction(true), "undo.gif"));
        menu.add(editor.bind("Refazer", new HistoryAction(false), "redo.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Cortar", TransferHandler.getCutAction(), "cut.gif"));
        menu.add(editor.bind("Copiar", TransferHandler.getCopyAction(), "copy.gif"));
        menu.add(editor.bind("Colar", TransferHandler.getPasteAction(), "paste.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Deletar", mxGraphActions.getDeleteAction(), "delete.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Selecionar Tudo", mxGraphActions.getSelectAllAction()));
        menu.add(editor.bind("Desfazer Seleção", mxGraphActions.getSelectNoneAction()));

        menu.addSeparator();

        menu.add(editor.bind("Aviso", new WarningAction()));
        menu.add(editor.bind("Editar", mxGraphActions.getEditAction()));

        menu = add(new JMenu("Visualizar"));

        JMenuItem item = menu.add(new TogglePropertyItem(graphComponent, "Layout da Página", "Visibilidade da Página", true, new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                if (graphComponent.isPageVisible() && graphComponent.isCenterPage()) {
                    graphComponent.zoomAndCenter();
                } else {
                    graphComponent.getGraphControl().updatePreferredSize();
                }
            }
        }));

        item.addActionListener(e -> {
            if (e.getSource() instanceof TogglePropertyItem toggleItem) {
                final mxGraphComponent graphComponent1 = editor.getGraphComponent();

                if (toggleItem.isSelected()) {
                    // Scrolls the view to the center
                    SwingUtilities.invokeLater(() -> {
                        graphComponent1.scrollToCenter(true);
                        graphComponent1.scrollToCenter(false);
                    });
                } else {
                    // Resets the translation of the view
                    mxPoint tr = graphComponent1.getGraph().getView().getTranslate();

                    if (tr.getX() != 0 || tr.getY() != 0) {
                        graphComponent1.getGraph().getView().setTranslate(new mxPoint());
                    }
                }
            }
        });

        menu.add(new TogglePropertyItem(graphComponent, "Anti-Aliasing", "Anti-Aliasing", true));

        menu.addSeparator();

        menu.add(new ToggleGridItem(editor, "Grade"));
        menu.add(new ToggleRulersItem(editor, "Réguas"));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Zoom"));

        submenu.add(editor.bind("400%", new ScaleAction(4)));
        submenu.add(editor.bind("200%", new ScaleAction(2)));
        submenu.add(editor.bind("150%", new ScaleAction(1.5)));
        submenu.add(editor.bind("100%", new ScaleAction(1)));
        submenu.add(editor.bind("75%", new ScaleAction(0.75)));
        submenu.add(editor.bind("50%", new ScaleAction(0.5)));

        submenu.addSeparator();

        submenu.add(editor.bind("Customizado", new ScaleAction(0)));

        menu.addSeparator();

        menu.add(editor.bind("Mais Zoom", mxGraphActions.getZoomInAction()));
        menu.add(editor.bind("Menos Zoom", mxGraphActions.getZoomOutAction()));

        menu.addSeparator();

        menu.add(editor.bind("Página", new ZoomPolicyAction(mxGraphComponent.ZOOM_POLICY_PAGE)));
        menu.add(editor.bind("Largura", new ZoomPolicyAction(mxGraphComponent.ZOOM_POLICY_WIDTH)));

        menu.addSeparator();

        menu.add(editor.bind("Tamanho Atual", mxGraphActions.getZoomActualAction()));

        menu = add(new JMenu("Formatar"));

        populateFormatMenu(menu, editor);

        menu = add(new JMenu("Forma"));

        populateShapeMenu(menu, editor);

        menu = add(new JMenu("Diagrama"));

        menu.add(new ToggleOutlineItem(editor, "Contorno"));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Fundo"));

        submenu.add(editor.bind("Cor de Fundo", new BackgroundAction()));
        submenu.add(editor.bind("Imagem de Fundo", new BackgroundImageAction()));

        submenu.addSeparator();

        submenu.add(editor.bind("Fundo da Página", new PageBackgroundAction()));

        submenu = (JMenu) menu.add(new JMenu("Grade"));

        submenu.add(editor.bind("Tamanho da Grade", new PromptPropertyAction(graph, "Tamanho da Grade", "GridSize")));
        submenu.add(editor.bind("Cor da Grade", new GridColorAction()));

        submenu.addSeparator();

        submenu.add(editor.bind("Tracejado", new GridStyleAction(mxGraphComponent.GRID_STYLE_DASHED)));
        submenu.add(editor.bind("Pontilhado", new GridStyleAction(mxGraphComponent.GRID_STYLE_DOT)));
        submenu.add(editor.bind("Linha", new GridStyleAction(mxGraphComponent.GRID_STYLE_LINE)));
        submenu.add(editor.bind("Cruz", new GridStyleAction(mxGraphComponent.GRID_STYLE_CROSS)));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Seleção"));

        submenu.add(editor.bind("Selecionar Caminho", new SelectShortestPathAction(false)));
        submenu.add(editor.bind("Selecionar Caminho Direcionado", new SelectShortestPathAction(true)));

        submenu.addSeparator();

        submenu.add(editor.bind("Selecionar Árvore", new SelectSpanningTreeAction(false)));
        submenu.add(editor.bind("Selecionar Árvore Direcionada", new SelectSpanningTreeAction(true)));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Folha de Estilos"));

        submenu.add(editor.bind("Estilo Básico", new StylesheetAction("basic-style.xml")));
        submenu.add(editor.bind("Estilo Default", new StylesheetAction("default-style.xml")));

        menu = add(new JMenu("Opções"));

        submenu = (JMenu) menu.add(new JMenu("Display"));
        submenu.add(new TogglePropertyItem(graphComponent, "Bufferizar", "TripleBuffered", true));

        submenu.add(new TogglePropertyItem(graphComponent, "Tamanho de Página Padrão", "PreferPageSize", true, e -> graphComponent.zoomAndCenter()));

        submenu.addSeparator();

        submenu.add(editor.bind("Tolerância", new PromptPropertyAction(graphComponent, "Tolerance")));

        submenu.add(editor.bind("Sujo", new ToggleDirtyAction()));

        submenu = (JMenu) menu.add(new JMenu("Zoom"));

        submenu.add(new TogglePropertyItem(graphComponent, "Centralizar Zoom", "CenterZoom", true));
        submenu.add(new TogglePropertyItem(graphComponent, "Zoom na Seleção", "KeepSelectionVisibleOnZoom", true));

        submenu.addSeparator();

        submenu.add(new TogglePropertyItem(graphComponent, "Centralizar Página", "CenterPage", true, new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                if (graphComponent.isPageVisible() && graphComponent.isCenterPage()) {
                    graphComponent.zoomAndCenter();
                }
            }
        }));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Arrastar e Soltar"));

        submenu.add(new TogglePropertyItem(graphComponent, "Arratar Ativado", "DragEnabled"));
        submenu.add(new TogglePropertyItem(graph, "Soltar Ativado", "DropEnabled"));

        submenu.addSeparator();

        submenu.add(new TogglePropertyItem(graphComponent.getGraphHandler(), "Pre-visualização de Imagem", "ImagePreview"));

        submenu = (JMenu) menu.add(new JMenu("Rótulos"));

        submenu.add(new TogglePropertyItem(graph, "Rótulos HTML", "HtmlLabels", true));
        submenu.add(new TogglePropertyItem(graph, "Mostrar Rótulos", "LabelsVisible", true));

        submenu.addSeparator();

        submenu.add(new TogglePropertyItem(graph, "Mover Rótulos das Arestas", "EdgeLabelsMovable"));
        submenu.add(new TogglePropertyItem(graph, "Mover Rótulos dos Vértices", "VertexLabelsMovable"));

        submenu.addSeparator();

        submenu.add(new TogglePropertyItem(graphComponent, "ENTER para concluir edição", "EnterStopsCellEditing"));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Conexões"));

        submenu.add(new TogglePropertyItem(graphComponent, "Conectar", "Connectable"));
        submenu.add(new TogglePropertyItem(graph, "Conectar Arestas", "ConnectableEdges"));

        submenu.addSeparator();

        submenu.add(new ToggleCreateTargetItem(editor, "Criar Alvos"));
        submenu.add(new TogglePropertyItem(graph, "Desconectar ao Mover", "DisconnectOnMove"));
        TogglePropertyItem.disablePropertyByDefault(graph, "DisconnectOnMove");

        submenu.addSeparator();

        submenu.add(editor.bind("Modo de Conexão", new ToggleConnectModeAction(editor)));

        submenu = (JMenu) menu.add(new JMenu("Validação"));

        submenu.add(new TogglePropertyItem(graph, "Permitir Arestas Pendentes", "AllowDanglingEdges"));
        TogglePropertyItem.disablePropertyByDefault(graph, "AllowDanglingEdges");

        submenu.add(new TogglePropertyItem(graph, "Clonar Arestas Inválidas", "CloneInvalidEdges"));

        submenu.addSeparator();

        submenu.add(new TogglePropertyItem(graph, "Permitir Loops", "AllowLoops"));

        menu = add(new JMenu("Janela"));

        UIManager.LookAndFeelInfo[] lafs =
                Arrays.stream(UIManager.getInstalledLookAndFeels())
                        .filter(laf -> laf.getClassName().startsWith("com.formdev.flatlaf"))
                        .toArray(UIManager.LookAndFeelInfo[]::new);


        for (UIManager.LookAndFeelInfo laf : lafs) {
            final String clazz = laf.getClassName();

            menu.add(new AbstractAction(laf.getName()) {
                /**
                 *
                 */
                @Serial
                private static final long serialVersionUID = 7588919504149148501L;

                public void actionPerformed(ActionEvent e) {
                    editor.setLookAndFeel(clazz);
                }
            });
        }

        // Creates the help menu
        menu = add(new JMenu("Ajuda"));

        item = menu.add(new JMenuItem("Sobre MoLIC"));
        item.addActionListener(e -> editor.molic());
    }

    /**
     * Adds menu items to the given shape menu. This is factored out because
     * the shape menu appears in the menubar and also in the popupmenu.
     */
    public static void populateShapeMenu(JMenu menu, BasicGraphEditor editor) {
        menu.add(editor.bind("Origem", mxGraphActions.getHomeAction(), "house.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Sair do Grupo", mxGraphActions.getExitGroupAction(), "up.gif"));
        menu.add(editor.bind("Entrar no Grupo", mxGraphActions.getEnterGroupAction(), "down.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Agrupar", mxGraphActions.getGroupAction(), "group.gif"));
        menu.add(editor.bind("Desagrupar", mxGraphActions.getUngroupAction(), "ungroup.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Remover do Grupo", mxGraphActions.getRemoveFromParentAction()));

        menu.add(editor.bind("Atualizar Limites do Grupo", mxGraphActions.getUpdateGroupBoundsAction()));

        menu.addSeparator();

        menu.add(editor.bind("Esconder", mxGraphActions.getCollapseAction(), "collapse.gif"));
        menu.add(editor.bind("Mostrar", mxGraphActions.getExpandAction(), "expand.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Para Trás", mxGraphActions.getToBackAction(), "toback.gif"));
        menu.add(editor.bind("Para Frente", mxGraphActions.getToFrontAction(), "tofront.gif"));

        menu.addSeparator();

        JMenu submenu = (JMenu) menu.add(new JMenu("Alinhar"));

        submenu.add(editor.bind("Esquerda", new AlignCellsAction(mxConstants.ALIGN_LEFT), "alignleft.gif"));
        submenu.add(editor.bind("Centro", new AlignCellsAction(mxConstants.ALIGN_CENTER), "aligncenter.gif"));
        submenu.add(editor.bind("Direita", new AlignCellsAction(mxConstants.ALIGN_RIGHT), "alignright.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Topo", new AlignCellsAction(mxConstants.ALIGN_TOP), "aligntop.gif"));
        submenu.add(editor.bind("Meio", new AlignCellsAction(mxConstants.ALIGN_MIDDLE), "alignmiddle.gif"));
        submenu.add(editor.bind("Margem", new AlignCellsAction(mxConstants.ALIGN_BOTTOM), "alignbottom.gif"));

        menu.addSeparator();

        menu.add(editor.bind("Tamanho Automático", new AutosizeAction()));

    }

    /**
     * Adds menu items to the given format menu. This is factored out because
     * the format menu appears in the menubar and also in the popupmenu.
     */
    public static void populateFormatMenu(JMenu menu, BasicGraphEditor editor) {
        JMenu submenu = (JMenu) menu.add(new JMenu("Fundo"));

        submenu.add(editor.bind("Cor de Preenchimento", new ColorAction("Fillcolor", mxConstants.STYLE_FILLCOLOR), "fillcolor.gif"));
        submenu.add(editor.bind("Gradiente", new ColorAction("Gradient", mxConstants.STYLE_GRADIENTCOLOR)));

        submenu.addSeparator();

        submenu.add(editor.bind("Imagem", new PromptValueAction(mxConstants.STYLE_IMAGE, "Image")));
        submenu.add(editor.bind("Sombra", new ToggleAction(mxConstants.STYLE_SHADOW)));

        submenu.addSeparator();

        submenu.add(editor.bind("Opacidade", new PromptValueAction(mxConstants.STYLE_OPACITY, "Opacity (0-100)")));

        submenu = (JMenu) menu.add(new JMenu("Rótulo"));

        submenu.add(editor.bind("Cor da Fonte", new ColorAction("Fontcolor", mxConstants.STYLE_FONTCOLOR), "fontcolor.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Preenchimento do Rótulo", new ColorAction("Label Fill", mxConstants.STYLE_LABEL_BACKGROUNDCOLOR)));
        submenu.add(editor.bind("Borda do Rótulo", new ColorAction("Label Border", mxConstants.STYLE_LABEL_BORDERCOLOR)));

        submenu.addSeparator();

        submenu.add(editor.bind("Rotacionar Rótulo", new ToggleAction(mxConstants.STYLE_HORIZONTAL, true)));

        submenu.add(editor.bind("Opacidade do Texto", new PromptValueAction(mxConstants.STYLE_TEXT_OPACITY, "Opacity (0-100)")));

        submenu.addSeparator();

        JMenu subsubmenu = (JMenu) submenu.add(new JMenu("Posição"));

        subsubmenu.add(editor.bind("Topo",   new SetLabelPositionAction(mxConstants.ALIGN_TOP, mxConstants.ALIGN_BOTTOM)));
        subsubmenu.add(editor.bind("Meio",   new SetLabelPositionAction(mxConstants.ALIGN_MIDDLE, mxConstants.ALIGN_MIDDLE)));
        subsubmenu.add(editor.bind("Margem", new SetLabelPositionAction(mxConstants.ALIGN_BOTTOM, mxConstants.ALIGN_TOP)));

        subsubmenu.addSeparator();

        subsubmenu.add(editor.bind("Esquerda", new SetLabelPositionAction(mxConstants.ALIGN_LEFT, mxConstants.ALIGN_RIGHT)));
        subsubmenu.add(editor.bind("Centro", new SetLabelPositionAction(mxConstants.ALIGN_CENTER, mxConstants.ALIGN_CENTER)));
        subsubmenu.add(editor.bind("Direita", new SetLabelPositionAction(mxConstants.ALIGN_RIGHT, mxConstants.ALIGN_LEFT)));

        submenu.addSeparator();

        submenu.add(editor.bind("Quebra de Linha", new KeyValueAction(mxConstants.STYLE_WHITE_SPACE, "wrap")));
        submenu.add(editor.bind("Sem Quebra de Linha", new KeyValueAction(mxConstants.STYLE_WHITE_SPACE, null)));

        submenu.addSeparator();

        submenu.add(editor.bind("Esconder Rótulo", new ToggleAction(mxConstants.STYLE_NOLABEL)));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Linha"));

        submenu.add(editor.bind("Cor da Linha", new ColorAction("Cor da Linha", mxConstants.STYLE_STROKECOLOR), "linecolor.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Ortogonal", new ToggleAction(mxConstants.STYLE_ORTHOGONAL)));
        submenu.add(editor.bind("Tracejada", new ToggleAction(mxConstants.STYLE_DASHED)));

        submenu.addSeparator();

        submenu.add(editor.bind("Espessura da Linha", new PromptValueAction(mxConstants.STYLE_STROKEWIDTH, "Linewidth")));

        submenu = (JMenu) menu.add(new JMenu("Conector"));

        submenu.add(editor.bind("Reto", new SetStyleAction("straight"), "straight.gif"));

        submenu.add(editor.bind("Horizontal", new SetStyleAction(""), "connect.gif"));
        submenu.add(editor.bind("Vertical", new SetStyleAction("vertical"), "vertical.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Relação de Entidades", new SetStyleAction("edgeStyle=mxEdgeStyle.EntityRelation"), "entity.gif"));
        submenu.add(editor.bind("Seta", new SetStyleAction("arrow"), "arrow.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Plano", new ToggleAction(mxConstants.STYLE_NOEDGESTYLE)));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Início de Linha"));

        submenu.add(editor.bind("Aberto", new KeyValueAction(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_OPEN), "open_start.gif"));
        submenu.add(editor.bind("Clássico", new KeyValueAction(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_CLASSIC), "classic_start.gif"));
        submenu.add(editor.bind("Bloco", new KeyValueAction(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_BLOCK), "block_start.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Diamante", new KeyValueAction(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_DIAMOND), "diamond_start.gif"));
        submenu.add(editor.bind("Oval", new KeyValueAction(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_OVAL), "oval_start.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Nenhum", new KeyValueAction(mxConstants.STYLE_STARTARROW, mxConstants.NONE)));
        submenu.add(editor.bind("Tamanho", new PromptValueAction(mxConstants.STYLE_STARTSIZE, "Linestart Size")));

        submenu = (JMenu) menu.add(new JMenu("Fim de Linha"));

        submenu.add(editor.bind("Aberto", new KeyValueAction(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN), "open_end.gif"));
        submenu.add(editor.bind("Clássico", new KeyValueAction(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC), "classic_end.gif"));
        submenu.add(editor.bind("Bloco", new KeyValueAction(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_BLOCK), "block_end.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Diamante", new KeyValueAction(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_DIAMOND), "diamond_end.gif"));
        submenu.add(editor.bind("Oval", new KeyValueAction(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OVAL), "oval_end.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Nenhum", new KeyValueAction(mxConstants.STYLE_ENDARROW, mxConstants.NONE)));
        submenu.add(editor.bind("Tamanho", new PromptValueAction(mxConstants.STYLE_ENDSIZE, "Lineend Size")));

        menu.addSeparator();

        submenu = (JMenu) menu.add(new JMenu("Alinhamento"));

        submenu.add(editor.bind("Esquerda", new KeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT), "left.gif"));
        submenu.add(editor.bind("Centro", new KeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER), "center.gif"));
        submenu.add(editor.bind("Direita", new KeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT), "right.gif"));

        submenu.addSeparator();

        submenu.add(editor.bind("Topo", new KeyValueAction(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP), "top.gif"));
        submenu.add(editor.bind("Meio", new KeyValueAction(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE), "middle.gif"));
        submenu.add(editor.bind("Margem", new KeyValueAction(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM), "bottom.gif"));

        submenu = (JMenu) menu.add(new JMenu("Espaçamento"));

        submenu.add(editor.bind("Direita", new PromptValueAction(mxConstants.STYLE_SPACING_RIGHT, "Right Spacing")));
        submenu.add(editor.bind("Margem", new PromptValueAction(mxConstants.STYLE_SPACING_BOTTOM, "Bottom Spacing")));
        submenu.add(editor.bind("Esquerda", new PromptValueAction(mxConstants.STYLE_SPACING_LEFT, "Left Spacing")));

        submenu.addSeparator();

        submenu.add(editor.bind("Global", new PromptValueAction(mxConstants.STYLE_SPACING, "Spacing")));

        submenu.addSeparator();

        submenu.add(editor.bind("Espaçamento da Origem", new PromptValueAction(mxConstants.STYLE_SOURCE_PERIMETER_SPACING, "sourceSpacing")));
        submenu.add(editor.bind("Espaçamento do Alvo", new PromptValueAction(mxConstants.STYLE_TARGET_PERIMETER_SPACING, "targetSpacing")));

        submenu.addSeparator();

        submenu.add(editor.bind("Perímetro", new PromptValueAction(mxConstants.STYLE_PERIMETER_SPACING, "Perimeter Spacing")));

        submenu = (JMenu) menu.add(new JMenu("Direção"));

        submenu.add(editor.bind("Norte", new KeyValueAction(mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_NORTH)));
        submenu.add(editor.bind("Leste", new KeyValueAction(mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST)));
        submenu.add(editor.bind("Sul", new KeyValueAction(mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_SOUTH)));
        submenu.add(editor.bind("Oeste", new KeyValueAction(mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_WEST)));

        submenu.addSeparator();

        submenu.add(editor.bind("Rotação", new PromptValueAction(mxConstants.STYLE_ROTATION, "Rotation (0-360)")));

        menu.addSeparator();

        menu.add(editor.bind("Arredondado", new ToggleAction(mxConstants.STYLE_ROUNDED)));

        menu.add(editor.bind("Estilo", new StyleAction()));
    }

}