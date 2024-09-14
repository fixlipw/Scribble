package com.ufc.molic.editor;

import com.mxgraph.swing.util.mxGraphActions;
import com.ufc.molic.editor.EditorActions.HistoryAction;

import javax.swing.*;
import java.io.Serial;

public class EditorPopupMenu extends JPopupMenu {

    @Serial
    private static final long serialVersionUID = -3132749140550242191L;

    public EditorPopupMenu(BasicGraphEditor editor) {
        boolean selected = !editor.getGraphComponent().getGraph().isSelectionEmpty();

        add(editor.bind("Desfazer", new HistoryAction(true), "images/undo.gif"));

        addSeparator();

        add(editor.bind("Cortar", TransferHandler.getCutAction(), "images/cut.gif")).setEnabled(selected);
        add(editor.bind("Copiar", TransferHandler.getCopyAction(), "images/copy.gif")).setEnabled(selected);
        add(editor.bind("Colar", TransferHandler.getPasteAction(), "images/paste.gif"));

        addSeparator();

        add(editor.bind("Deletar", mxGraphActions.getDeleteAction(), "images/delete.gif")).setEnabled(selected);

        addSeparator();

        add(editor.bind("Criar Objetivo", new EditorActions.SaveGoalsAction())).setEnabled(selected);

        addSeparator();

        JMenu menu = (JMenu) add(new JMenu("Formato"));

        EditorMenuBar.populateFormatMenu(menu, editor);

        menu = (JMenu) add(new JMenu("Forma"));

        EditorMenuBar.populateShapeMenu(menu, editor);

        addSeparator();

        add(editor.bind("Editar", mxGraphActions.getEditAction())).setEnabled(selected);

        addSeparator();

        add(editor.bind("Selecionar Verticies", mxGraphActions.getSelectVerticesAction()));
        add(editor.bind("Selecionar Arestas", mxGraphActions.getSelectEdgesAction()));

        addSeparator();

        add(editor.bind("Selecionar Tudo", mxGraphActions.getSelectAllAction()));
    }

}
