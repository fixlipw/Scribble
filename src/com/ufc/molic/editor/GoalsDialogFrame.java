package com.ufc.molic.editor;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static com.ufc.molic.editor.EditorActions.getEditor;

public class GoalsDialogFrame extends JDialog {

    protected JColorChooser goalColorChooser = new JColorChooser();
    protected JTextField goalNameField = new JTextField();
    protected JTextField goalDescriptionField = new JTextField();

    public GoalsDialogFrame(ActionEvent e) {

        JPanel contentPanel = new JPanel();
        JPanel colorChooserPanel = new JPanel();
        JLabel goalDescriptionLabel = new JLabel();
        JButton cancelButton = new JButton();
        JButton okButton = new JButton();
        JLabel goalNameLabel = new JLabel();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Novo Objetivo");

        colorChooserPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        colorChooserPanel.setName("Selecione uma Cor"); // NOI18N

        goalColorChooser.setBorder(BorderFactory.createTitledBorder("Selecione uma Cor"));

        goalDescriptionLabel.setText("Descrição do Objetivo");

        cancelButton.setText("Cancelar");
        cancelButton.addActionListener(evt -> dispose());

        okButton.setText("OK");
        okButton.addActionListener(evt -> {
            if (goalNameField.getText().isEmpty() || goalDescriptionField.getText().isEmpty() || goalColorChooser.getColor() == null) {
                JOptionPane.showMessageDialog(this, "Objetivos necessitam de nome, cor e descrição!");
            } else if (goalNameField.getText().length() < 5 || goalDescriptionField.getText().length() < 5) {
                JOptionPane.showMessageDialog(this, "Nome e descrição dos objetivos dever ter mais de 5 caracteres!");
            } else {

                BasicGraphEditor editor = getEditor(e);

                if (e.getSource() instanceof mxGraphComponent graphComponent) {
                    mxGraph graph = graphComponent.getGraph();

                    String goalName = goalNameField.getText();
                    String goalDescription = goalDescriptionField.getText();
                    Color goalColor = goalColorChooser.getColor();
                    assert editor != null;
                    editor.goalsPanel.addGoals(goalName, goalDescription, goalColor, graph, graph.getSelectionCells(), graph.getGraphBounds());
                    System.out.println(Arrays.toString(graph.getSelectionCells()));
                }
            }
            dispose();
        });

        goalNameLabel.setText("Nome do Objetivo");

        GroupLayout colorChooserPanelLayout = new GroupLayout(colorChooserPanel);
        colorChooserPanel.setLayout(colorChooserPanelLayout);
        colorChooserPanelLayout.setHorizontalGroup(
                colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(colorChooserPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(goalColorChooser, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, colorChooserPanelLayout.createSequentialGroup()
                                                .addGap(0, 2, Short.MAX_VALUE)
                                                .addGroup(colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(colorChooserPanelLayout.createSequentialGroup()
                                                                .addComponent(goalDescriptionLabel)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(goalDescriptionField, GroupLayout.PREFERRED_SIZE, 542, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(colorChooserPanelLayout.createSequentialGroup()
                                                                .addComponent(okButton)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cancelButton))))
                                        .addGroup(colorChooserPanelLayout.createSequentialGroup()
                                                .addComponent(goalNameLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(goalNameField, GroupLayout.PREFERRED_SIZE, 542, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        colorChooserPanelLayout.setVerticalGroup(
                colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, colorChooserPanelLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(goalNameLabel)
                                        .addComponent(goalNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(goalColorChooser, GroupLayout.PREFERRED_SIZE, 347, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(goalDescriptionLabel)
                                        .addComponent(goalDescriptionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(colorChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cancelButton)
                                        .addComponent(okButton))
                                .addContainerGap())
        );

        GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
                contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(colorChooserPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
                contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(colorChooserPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

        pack();
        
    }

    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(actionEvent -> setVisible(false), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
}
