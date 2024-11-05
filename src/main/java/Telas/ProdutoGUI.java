package Telas;

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
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author daniel
 */
public class ProdutoGUI extends javax.swing.JFrame {
    
    private static ProdutoGUI instance;
    private static OrcamentoGUI orcamentoGUI;
    
    /**
     * Creates new form TelaNovoProduto
     */
    public ProdutoGUI() {
        initComponents();
        atalhos();
        padrao();
        addListeners();
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE); // Fecha apenas a tela atual        
        setResizable(false); // Não redimensionável
        setLocationRelativeTo(null); // Centraliza a janela na tela
        
    }
    
    // MÉTODOS ESPECÍFICOS PARA ESTA TELA:
    
    private long salvarProduto() {
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {

            ItemOrcamentoDAO produto = new ItemOrcamentoDAO();
            long produtoId = produto.findNextId(session);

            String descricaoProduto = txtDescricao.getText();

            String comandoSqlProduto = "insert into produtos(id_produto, descricao, tipo_produto) values (" + produtoId + ",'" + descricaoProduto + "', " + "'Mercadoria')";
            session.createNativeQuery(comandoSqlProduto).executeUpdate();

            transaction.commit();
            return produtoId;
        } catch (Exception ex) {
            //se der erro, dá um rollback na transação
            transaction.rollback();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + ex.getMessage());
            return 0;
        }

    }

    private void salvarOrcamentoItem() {
        long produtoId = salvarProduto();
        long idOrcamento = orcamentoGUI.idOrcamentoGlobal;
        String valorUnitario = txtValorUn.getText().replace(",", ".");
        String quantidade = txtQuantidade.getText();

        ItemOrcamentoDAO itemOrcamentoDAO = new ItemOrcamentoDAO();
        itemOrcamentoDAO.inserirItemOrcamento(produtoId, valorUnitario, quantidade, idOrcamento);
    }
    
    // Método para setar o padrão 
    private void padrao() {
        txtDescricao.requestFocusInWindow(); 
    }
    
    // Método para obter a instância única da tela
    public static ProdutoGUI getInstance() {
        if (instance == null) {
            instance = new ProdutoGUI();
        }
        return instance;
    }

    private long idOrcamentoGlobal;
    
    
    // Método para abrir a tela de novo Produto
    public static void abrirNovaInstancia(OrcamentoGUI instanciaOrcamento) {
        orcamentoGUI = instanciaOrcamento;
        if (instance == null) {
            instance = new ProdutoGUI();
            //Define a janela como sempre no topo
            //instance.setAlwaysOnTop(true);
        }
        instance.setVisible(true);
    }
    
    // Método para limpar os campos de texto
    private void limparCampos() {
        txtDescricao.setText("");
        txtValorUn.setText("0,00");
        txtQuantidade.setText("");
        lblSubtotal.setText("0,00");
    }
    
    private void atualizarSubtotal() {
        try {
            // Obtenha o texto dos campos
            String valorUnitarioStr = txtValorUn.getText().replaceAll("[^\\d,]", ""); // Remove tudo que não é número ou vírgula
            String quantidadeStr = txtQuantidade.getText().replaceAll("[^\\d]", ""); // Remove tudo que não é número

            // Substitui a vírgula por ponto para a conversão para double
            double valorUnitario = Double.parseDouble(valorUnitarioStr.replace(",", "."));
            int quantidade = Integer.parseInt(quantidadeStr.isEmpty() ? "0" : quantidadeStr); // Se vazio, assume 0
            double subtotal = valorUnitario * quantidade;
            lblSubtotal.setText(String.format("%.2f", subtotal));
        } catch (NumberFormatException e) {
            lblSubtotal.setText("0,00");
        }
    }
    
    private void addListeners() {
        // Listener para atualizar subtotal ao soltar teclas em txtValorUn
        txtValorUn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                atualizarSubtotal();
            }
        });

        // Listener para atualizar subtotal ao soltar teclas em txtQuantidade
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                atualizarSubtotal();
            }
        });

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

        // Listener para focar e sempre selecionar tudo no campo txtQuantidade
        txtQuantidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        txtQuantidade.selectAll(); // Seleciona todo o texto da quantidade
                    }
                });
            }
        });
        
        // Listener para Nulificar a instancia ao clicar no X de fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ProdutoGUI.instance = null; // Nulifica a instância única
                dispose(); // Fecha a janela
            }
        });
    }
    
    // Método personalizado para configurar os atalhos de teclado
    private void atalhos() {
        JRootPane rootPane = this.getRootPane();

        // Mapeamento global da tecla Enter para Inserir 
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "inserir");
        rootPane.getActionMap().put("inserir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnInserirProduto.doClick(); // Simula o clique no botão Inserir
            }
        });

        // Mapeamento global da tecla esc para fechar a tela
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "dispose");
        rootPane.getActionMap().put("dispose", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ProdutoGUI.instance = null; // Nulifica a instância única
            }
        });
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
        lblValorTotal = new javax.swing.JLabel();
        lblQuantidade = new javax.swing.JLabel();
        lblValorUn = new javax.swing.JLabel();
        btnInserirProduto = new javax.swing.JButton();
        lblCancelar = new javax.swing.JButton();
        txtDescricao = new javax.swing.JTextField();
        lblSubtotal = new javax.swing.JLabel();
        txtValorUn = new javax.swing.JFormattedTextField();
        txtQuantidade = new javax.swing.JFormattedTextField();
        lblRs = new javax.swing.JLabel();
        lblRs1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Incluir Mercadoria");

        lblDescricao.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblDescricao.setText("Descrição da Peça / Mercadoria");

        lblValorTotal.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblValorTotal.setText("Total");

        lblQuantidade.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblQuantidade.setText("Qtd.");

        lblValorUn.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblValorUn.setText("Valor Unitário");

        btnInserirProduto.setText("Inserir Produto");
        btnInserirProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirProdutoActionPerformed(evt);
            }
        });

        lblCancelar.setText("Cancelar");
        lblCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblCancelarActionPerformed(evt);
            }
        });

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

        lblSubtotal.setText("0,00");

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

        txtQuantidade.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtQuantidade.setText("1");
        txtQuantidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQuantidadeFocusLost(evt);
            }
        });
        txtQuantidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtQuantidadeKeyTyped(evt);
            }
        });

        lblRs.setText("R$");

        lblRs1.setText("R$");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDescricao)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblRs1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtValorUn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblValorUn, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblValorTotal)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblRs)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(39, 39, 39))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnInserirProduto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblDescricao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorUn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRs1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblValorUn)
                        .addGap(30, 30, 30))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(24, 24, 24)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                        .addComponent(lblSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                                        .addComponent(lblRs)))
                                .addComponent(lblValorTotal)))
                        .addComponent(lblQuantidade)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCancelar)
                    .addComponent(btnInserirProduto))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblCancelarActionPerformed
        dispose(); // Fecha a janela
        ProdutoGUI.instance = null; // Nulifica a instância única
    }//GEN-LAST:event_lblCancelarActionPerformed
       
    private void btnInserirProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirProdutoActionPerformed
        // Verifica se todos os campos obrigatórios estão preenchidos
        if (txtDescricao.getText().trim().isEmpty() || txtValorUn.getText().trim().isEmpty() || txtQuantidade.getText().trim().isEmpty()) {
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

        // Validação da quantidade
        try {
            int quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validações concluídas, prosseguir com o salvamento
        if (orcamentoGUI.idOrcamentoGlobal > 0) {
            salvarOrcamentoItem();
        }

        orcamentoGUI.atualizarGridItens();
        dispose();
        limparCampos();
        txtQuantidade.setValue(1);
    }//GEN-LAST:event_btnInserirProdutoActionPerformed

    private void txtDescricaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescricaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescricaoActionPerformed

    private void txtValorUnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorUnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorUnActionPerformed

    private void txtDescricaoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescricaoKeyTyped
        if (txtDescricao.getText().length() >= 50) {
            evt.consume(); // Limita o tamanho a 50 caracteres
        }
    }//GEN-LAST:event_txtDescricaoKeyTyped
    // Método para impedir a digitação de caracteres inválidos no campo de Valor Unitário
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
    
    // Método para impedir a digitação de caracteres inválidos no campo de Quantidade
    private void txtQuantidadeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyTyped
        char c = evt.getKeyChar();

        // Permite apenas números
        if (!Character.isDigit(c)) {
            evt.consume(); // Impede a digitação de qualquer caractere que não seja número
        }

        // Limita o tamanho do texto a 5 caracteres
        if (txtQuantidade.getText().length() >= 3) {
            evt.consume(); // Limita o tamanho a 5 caracteres
        }
    }//GEN-LAST:event_txtQuantidadeKeyTyped

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

    private void txtQuantidadeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyReleased
        
    }//GEN-LAST:event_txtQuantidadeKeyReleased
    
    // Quando o campo perder o foco, define o valor padrão como "0,00" se estiver vazio
    private void txtValorUnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorUnFocusLost
        if (txtValorUn.getText().trim().isEmpty()) {
            txtValorUn.setText("0,00"); // Define o valor padrão se estiver vazio
        } else {
            atualizarSubtotal(); // Atualiza o subtotal quando o campo perde o foco
        }
    }//GEN-LAST:event_txtValorUnFocusLost
    
    // Quando o campo perder o foco, define o valor padrão como "1" se estiver vazio
    private void txtQuantidadeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQuantidadeFocusLost
        if (txtQuantidade.getText().trim().isEmpty()) {
            txtQuantidade.setText("1"); // Define o valor padrão se estiver vazio
        } // Certificando-se de que a quantidade é válida
        else if (Integer.parseInt(txtQuantidade.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtQuantidade.setText("1"); // Reconfigura para 1 em caso de erro
        } else {
            atualizarSubtotal(); // Atualiza o subtotal quando o campo perde o foco
        }
    }//GEN-LAST:event_txtQuantidadeFocusLost

    private void txtValorUnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorUnKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnInserirProduto.doClick(); // Simula o clique no botão Inserir ao pressionar Enter
        }
    }//GEN-LAST:event_txtValorUnKeyPressed

    private void txtDescricaoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescricaoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnInserirProduto.doClick(); // Simula o clique no botão Inserir ao pressionar Enter
        }
    }//GEN-LAST:event_txtDescricaoKeyPressed

    private void txtQuantidadeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantidadeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnInserirProduto.doClick(); // Simula o clique no botão Inserir ao pressionar Enter
        }
    }//GEN-LAST:event_txtQuantidadeKeyPressed

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
            java.util.logging.Logger.getLogger(ProdutoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProdutoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProdutoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProdutoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProdutoGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInserirProduto;
    private javax.swing.JButton lblCancelar;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblRs;
    private javax.swing.JLabel lblRs1;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JLabel lblValorUn;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JFormattedTextField txtQuantidade;
    private javax.swing.JFormattedTextField txtValorUn;
    // End of variables declaration//GEN-END:variables
}
