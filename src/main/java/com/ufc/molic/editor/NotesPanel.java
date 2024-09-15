package com.ufc.molic.editor;

import com.ufc.molic.dao.AnotacaoDAOImpl;
import com.ufc.molic.entity.Anotacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NotesPanel extends JPanel {

    private final JTable notesTable;
    private final DefaultTableModel notesModel;

    AnotacaoDAOImpl anotacaoDAO = new AnotacaoDAOImpl();

    public NotesPanel() {
        setLayout(new BorderLayout());

        notesModel = new DefaultTableModel(new Object[]{"ID", "Anotação"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        notesTable = new JTable(notesModel);

        notesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && notesTable.getSelectedRow() != -1) {

                    String annotation = (String) notesModel.getValueAt(notesTable.getSelectedRow(), 1);

                    showNoteDialog("Anotação", annotation);
                }
            }
        });

        notesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(notesTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Adicionar");
        JButton editButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addNote());
        editButton.addActionListener(e -> editNote());
        deleteButton.addActionListener(e -> deleteNote());
    }

    private void showNoteDialog(String title, String annotation) {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow != -1) {

            JDialog noteDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
            noteDialog.setLayout(new BorderLayout());
            noteDialog.setSize(300, 200);
            noteDialog.setLocationRelativeTo(this);

            JTextArea annotationTextArea = new JTextArea(annotation);
            annotationTextArea.setEditable(false);
            annotationTextArea.setLineWrap(true);
            annotationTextArea.setWrapStyleWord(true);

            JScrollPane textScrollPane = new JScrollPane(annotationTextArea);
            noteDialog.add(textScrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Fechar");
            closeButton.addActionListener(e -> noteDialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);

            noteDialog.add(buttonPanel, BorderLayout.SOUTH);

            noteDialog.setVisible(true);
        }
    }

    private void addNote() {
        String annotation = JOptionPane.showInputDialog(null, "Digite a nova anotação:");
        if (annotation != null && !annotation.trim().isEmpty()) {
            int nextId = getNextId();
            notesModel.addRow(new Object[]{nextId, annotation.trim()});

            anotacaoDAO.save(new Anotacao(annotation));

            showNoteDialog("Anotação Salva!", annotation);
        }
    }

    private void editNote() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow != -1) {
            String currentAnnotation = (String) notesModel.getValueAt(selectedRow, 1);
            String newAnnotation = JOptionPane.showInputDialog(null, "Edite a anotação:", currentAnnotation);
            if (newAnnotation != null && !newAnnotation.trim().isEmpty()) {
                notesModel.setValueAt(newAnnotation.trim(), selectedRow, 1);

                Anotacao anotacao = anotacaoDAO.find(currentAnnotation.trim());
                if (anotacao != null) {
                    anotacao.setNote(newAnnotation.trim());
                    anotacaoDAO.update(anotacao);
                }

            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma anotação para editar.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteNote() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir a anotação selecionada?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Anotacao anotacao = anotacaoDAO.find(notesModel.getValueAt(selectedRow, 1).toString());
                anotacaoDAO.delete(anotacao.getId());

                notesModel.removeRow(selectedRow);


                renumberIds();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma anotação para excluir.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int getNextId() {
        int rowCount = notesModel.getRowCount();
        return rowCount > 0 ? (int) notesModel.getValueAt(rowCount - 1, 0) + 1 : 1;
    }

    private void renumberIds() {
        for (int i = 0; i < notesModel.getRowCount(); i++) {
            notesModel.setValueAt(i + 1, i, 0);
        }
    }

    public void loadNotes() {

        List<Anotacao> anotacoes = anotacaoDAO.find();

        notesModel.setRowCount(0);
        AtomicInteger id = new AtomicInteger(1);

        anotacoes.forEach(note -> {
            notesModel.addRow(new Object[]{id.getAndIncrement(), note.getNote()});
        });
    }
}

