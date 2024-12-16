package Telas;

import Classes.Mercadoria;
import Classes.Servico;
import Classes.SessionManager;
import DAO.ItemOrcamentoDAO;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Edu
 */
public class CadServicosGUI extends javax.swing.JFrame {
    
    private static CadServicosGUI instance;

    /**
     * Creates new form CadServicosGUI
     */
    public CadServicosGUI() {
        initComponents();
        atalhos();
        addListeners();
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE); // Fecha apenas a tela atual        
        setResizable(false); // Não redimensionável
        setLocationRelativeTo(null); // Centraliza a janela na tela
    }
    
    // MÉTODOS ESPECÍFICOS PARA ESTA TELA:
    
    // Método personalizado para configurar os atalhos de teclado
    private void atalhos() {
        JRootPane rootPane = this.getRootPane();
        
        // Mapeamento global da tecla ENTER para Salvar
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "salvarAction");
        rootPane.getActionMap().put("salvarAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSalvar.doClick(); // Simula o clique no botão Salvar
            }
        });
        
        // Mapeamento global da tecla esc para fechar a tela
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "dispose");
        rootPane.getActionMap().put("dispose", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CadServicosGUI.instance = null; // Nulifica a instância única
            }
        });
        
        // Mapeamento global da tecla F5 para atualizar a tela (reset)
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "reset");
        rootPane.getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new CadServicosGUI().setVisible(true);
            }
        });
    }
    
    private void addListeners() {
       
        // Listener para focar e sempre selecionar todos os números no campo txtValorUn
        txtValorUn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        txtValorUn.selectAll(); // Seleciona todo o texto no campo
                    }
                });
            }
        });

        // Listener para Nulificar a instancia ao clicar no X de fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CadServicosGUI.instance = null; // Nulifica a instância única
                dispose(); // Fecha a janela
            }
        });
    }
    
    private long cadastrarServico() {
        String descricaoProduto = txtDescricao.getText();
        String precoTexto = txtValorUn.getText().replace(",", ".");
        double precoProduto;

        // Validação do preço
        try {
            precoProduto = Double.parseDouble(precoTexto);
            if (precoProduto <= 0) {
                throw new NumberFormatException("O preço deve ser maior que zero.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido. Insira um valor numérico positivo.");
            return -1;
        }

        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        long produtoId = -1;

        try {
            produtoId = new ItemOrcamentoDAO().findNextId(session);
            Object tipoProduto = "Servico";

            if ("Mercadoria".equals(tipoProduto)) {
                Mercadoria mercadoria = new Mercadoria(descricaoProduto, precoProduto);
                mercadoria.setIdProduto(produtoId);
                session.save(mercadoria);
            } else if ("Servico".equals(tipoProduto)) {
                Servico servico = new Servico(descricaoProduto, precoProduto);
                servico.setIdProduto(produtoId);
                session.save(servico);
            } else {
                throw new IllegalArgumentException("Tipo de produto inválido!");
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + e.getMessage());
            return -1;
        } finally {
            session.close();
        }

        return produtoId;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblDescricao = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        lblValorUn = new javax.swing.JLabel();
        lblRs1 = new javax.swing.JLabel();
        txtValorUn = new javax.swing.JFormattedTextField();
        lblHead = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        PaneTabela = new javax.swing.JScrollPane();
        TabelaListagem = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblDescricao.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblDescricao.setText("Descrição do Serviço");

        txtDescricao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescricaoActionPerformed(evt);
            }
        });
        txtDescricao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDescricaoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescricaoKeyTyped(evt);
            }
        });

        lblValorUn.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblValorUn.setText("Valor Unitário");

        lblRs1.setText("R$");

        txtValorUn.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtValorUn.setText("0,00");
        txtValorUn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtValorUnFocusLost(evt);
            }
        });
        txtValorUn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorUnActionPerformed(evt);
            }
        });
        txtValorUn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValorUnKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorUnKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtValorUnKeyTyped(evt);
            }
        });

        lblHead.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHead.setText("Cadastro de Serviços");
        lblHead.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSalvar.setForeground(new java.awt.Color(76, 175, 80));
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnEditar.setForeground(new java.awt.Color(90, 155, 213));
        btnEditar.setText("Editar (F2)");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnExcluir.setForeground(new java.awt.Color(244, 67, 54));
        btnExcluir.setText("Excluir (Del)");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        TabelaListagem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "cod_servico", "descricao", "preco_produto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        PaneTabela.setViewportView(TabelaListagem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PaneTabela, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDescricao)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtDescricao))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblValorUn)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblRs1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtValorUn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluir)
                        .addGap(0, 48, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHead)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblDescricao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblValorUn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRs1)
                            .addComponent(txtValorUn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnEditar)
                    .addComponent(btnExcluir))
                .addGap(18, 18, 18)
                .addComponent(PaneTabela, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDescricaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescricaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescricaoActionPerformed

    private void txtDescricaoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescricaoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSalvar.doClick(); // Simula o clique no botão Inserir ao pressionar Enter
        }
    }//GEN-LAST:event_txtDescricaoKeyPressed

    private void txtDescricaoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescricaoKeyTyped
        if (txtDescricao.getText().length() >= 50) {
            evt.consume(); // Limita o tamanho a 50 caracteres
            return; // Sai do método para evitar processamento adicional
        }

        // Aguarda a digitação concluir antes de processar a formatação
        SwingUtilities.invokeLater(() -> {
            String texto = txtDescricao.getText();
            String[] palavras = texto.split("\\s+");
            StringBuilder descricaoFormatada = new StringBuilder();

            for (String palavra : palavras) {
                if (!palavra.isEmpty()) {
                    descricaoFormatada.append(Character.toUpperCase(palavra.charAt(0))) // Primeira letra maiúscula
                            .append(palavra.substring(1)) // Mantém as demais letras como estão
                            .append(" "); // Adiciona o espaço após a palavra
                }
            }

            // Define o texto formatado, preservando o espaço ao final durante a digitação
            if (texto.endsWith(" ")) {
                txtDescricao.setText(descricaoFormatada.toString()); // Mantém o espaço final
            } else {
                txtDescricao.setText(descricaoFormatada.toString().trim()); // Remove espaços extras no final
            }
        });
    }//GEN-LAST:event_txtDescricaoKeyTyped

    private void txtValorUnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorUnFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorUnFocusLost

    private void txtValorUnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorUnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorUnActionPerformed

    private void txtValorUnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSalvar.doClick(); // Simula o clique no botão Salvar ao pressionar Enter
        }
    }//GEN-LAST:event_txtValorUnKeyPressed

    private void txtValorUnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnKeyReleased
        // Verifica se a tecla pressionada é um número ou uma tecla de controle
        if (Character.isDigit(evt.getKeyChar()) || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            // Obtém o texto atual do campo
            String texto = txtValorUn.getText();

            // Remove caracteres não numéricos
            String numeros = texto.replaceAll("[^0-9]", "");

            // Se o texto não estiver vazio
            if (!numeros.isEmpty()) {
                // Atualiza o campo com a formatação correta
                if (!texto.endsWith(",00") && !texto.contains(",")) {
                    // Adiciona a formatação ",00" se não houver vírgula
                    txtValorUn.setText(numeros + ",00");
                    txtValorUn.setCaretPosition(txtValorUn.getText().length() - 3); // Move o cursor para antes de ",00"
                }
            } else {
                // Se o campo estiver vazio, define como "0,00" e seleciona todo o texto
                txtValorUn.setText("0,00");
                txtValorUn.selectAll(); // Seleciona todo o texto
            }
        } else if (evt.getKeyChar() == ',') {
            String texto = txtValorUn.getText();

            // Remove a última vírgula ao digitar uma nova vírgula
            int lastIndexVirgula = texto.lastIndexOf(',');
            String novoTexto = texto.substring(0, lastIndexVirgula) + texto.substring(lastIndexVirgula + 1);

            txtValorUn.setText(novoTexto);

            // Seleciona as casas decimais
            txtValorUn.setSelectionStart(lastIndexVirgula); // Seleciona logo após a vírgula
            txtValorUn.setSelectionEnd(txtValorUn.getText().length()); // Seleciona até o final do texto
        }
    }//GEN-LAST:event_txtValorUnKeyReleased

    private void txtValorUnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnKeyTyped
        char c = evt.getKeyChar();

        // Permite apenas números e vírgula (mudar para apenas uma vírgula)
        if (!Character.isDigit(c) && c != ',') {
            evt.consume();
        }

        // Limita o tamanho do texto
        if (txtValorUn.getText().length() >= 10) {
            evt.consume();
        }
    }//GEN-LAST:event_txtValorUnKeyTyped

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // Verifica se todos os campos obrigatórios estão preenchidos
        if (txtDescricao.getText().trim().isEmpty() || txtValorUn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validação do valor unitário
        double valorUnitario;
        try {
            valorUnitario = Double.parseDouble(txtValorUn.getText().replace(",", "."));
            if (valorUnitario <= 0) {
                JOptionPane.showMessageDialog(this, "O valor unitário deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O valor unitário deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cadastrarServico();
        //atualizarGridItens();
        //limparCampos();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExcluirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CadServicosGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadServicosGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadServicosGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadServicosGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadServicosGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane PaneTabela;
    private javax.swing.JTable TabelaListagem;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblHead;
    private javax.swing.JLabel lblRs1;
    private javax.swing.JLabel lblValorUn;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JFormattedTextField txtValorUn;
    // End of variables declaration//GEN-END:variables
}
