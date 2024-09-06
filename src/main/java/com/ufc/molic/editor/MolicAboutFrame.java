package com.ufc.molic.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.Objects;

public class MolicAboutFrame extends JDialog {

    public MolicAboutFrame(Frame owner) {
        super(owner);
        setTitle("Sobre o MoLIC");
        setLayout(new BorderLayout());
        setModal(false);

        JPanel titlePanel = getPanel();

        JLabel titleLabel = new JLabel("Sobre MoLIC");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(4, 18, 0, 0));
        titleLabel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("<html>MoLIC (Linguagem de Modelagem de Interação como Conversa) foi desenvolvida para representar a interação humano-computador como um conjunto de conversas que os usuários podem ter com o sistema (chamado de \"Proposto do Designer\") para alcançar objetivos</html>");
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(4, 18, 0, 0));
        subtitleLabel.setOpaque(false);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        getContentPane().add(titlePanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        JPanel contentPanel = getPanel();

        JLabel modelsLabel = new JLabel("Elementos da MoLIC");
        modelsLabel.setFont(modelsLabel.getFont().deriveFont(Font.BOLD));
        modelsLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        modelsLabel.setOpaque(false);
        modelsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(modelsLabel, BorderLayout.NORTH);

        // Ponto de Início
        String title1 = "Ponto de Início";
        String imageDescription1 = "<html><div style='width: 150px'>Representa um ponto de entrada para a aplicação. É possível ter mais de um ponto de entrada se a aplicação puder ser iniciada em cenas diferentes.<div></html>";
        JPanel imagePanel1 = createImagePanel("images/molic/pontoInicio_sized.png", title1, imageDescription1);
        contentPanel.add(imagePanel1);

        // Ponto de Saída
        String title2 = "Ponto de Saída";
        String imageDescription2 = "<html><div style='width: 150px'>Representa um ponto de saída para a aplicação, indicando onde a conversa termina.<div></html>";
        JPanel imagePanel2 = createImagePanel("images/molic/pontoFim_sized.png", title2, imageDescription2);
        contentPanel.add(imagePanel2);

        // Acesso Ubíquo
        String title3 = "Acesso Ubíquo";
        String imageDescription3 = "<html><div style='width: 150px'>Representa a troca de tópicos da conversa a qualquer momento.<div></html>";
        JPanel imagePanel3 = createImagePanel("images/molic/acessoUbicuo_sized.png", title3, imageDescription3);
        contentPanel.add(imagePanel3);

        // Cena
        String title4 = "Cena";
        String imageDescription4 = "<html><div style='width: 150px'>Representa uma conversa por tópicos que o usuário deve ter com o proposto do designer para atingir um objetivo. A cena se estrutura em dois containers: acima temos o Tópico da Conversa (uma frase do ponto de vista do designer sobre o que o usuário é capaz alcançar naquele momento), e abaixo os Diálogos da Conversa (descrevem os diálogos entre o usuário e o sistema devem manter parar atingir o objetivo descrito do tópico). Em Cena, o usuário tem sua vez de decidir como as conversas devem prossegir.<div></html>";
        JPanel imagePanel4 = createImagePanel("images/molic/cena_sized.png", title4, imageDescription4);
        contentPanel.add(imagePanel4);

        // Cena de Alerta
        String title5 = "Cena de Alerta";
        String imageDescription5 = "<html><div style='width: 150px'>Representa uma situação em que o designer prevê uma causa potencial de quebra de comunicação.<div></html>";
        JPanel imagePanel5 = createImagePanel("images/molic/cenaAlerta_sized.png", title5, imageDescription5);
        contentPanel.add(imagePanel5);

        // Processo
        String title6 = "Processo";
        String imageDescription6 = "<html><div style='width: 150px'>Representa o momento em que o sistema processa uma requisição recebida pelo usuário<div></html>";
        JPanel imagePanel6 = createImagePanel("images/molic/processo_sized.png", title6, imageDescription6);
        contentPanel.add(imagePanel6);

        // Processo com Progresso
        String title7 = "Processo com Progresso";
        String imageDescription7 = "<html><div style='width: 150px'>Representa um processo do sistema, e também permite que o designer defina uma interrupção ao atingir uma marca condicional<div></html>";
        JPanel imagePanel7 = createImagePanel("images/molic/processoComProgresso_sized.png", title7, imageDescription7);
        contentPanel.add(imagePanel7);



        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        scrollPane.setViewportView(contentPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton buttonOK = new JButton("OK");
        getRootPane().setDefaultButton(buttonOK);

        setSize(400, 400);
        setResizable(false);

        buttonOK.addActionListener(e -> setVisible(false));
    }

    private JPanel createImagePanel(String imagePath, String title, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(MolicAboutFrame.class.getClassLoader().getResource(imagePath)));
        JLabel imageLabel;

        if (originalIcon.getIconWidth() > 85) {
            Image scaledImage = originalIcon.getImage().getScaledInstance(85, -1, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaledImage));
        } else {
            imageLabel = new JLabel(originalIcon);
        }

        JPanel imageContainer = new JPanel(new GridBagLayout());
        imageContainer.setPreferredSize(new Dimension(85, originalIcon.getIconHeight()));
        imageContainer.add(imageLabel, new GridBagConstraints());

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel imageAndDescriptionPanel = new JPanel();
        imageAndDescriptionPanel.setLayout(new BoxLayout(imageAndDescriptionPanel, BoxLayout.X_AXIS));
        imageAndDescriptionPanel.add(imageContainer);
        imageAndDescriptionPanel.add(descriptionLabel);

        panel.add(imageAndDescriptionPanel);

        return panel;
    }

    private JPanel getPanel() {

        return new JPanel(new BorderLayout()) {

            @Serial
            private static final long serialVersionUID = -5062895855016210947L;

            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, getWidth(), 0, getBackground()));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

        };
    }

    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(actionEvent -> setVisible(false), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
}
