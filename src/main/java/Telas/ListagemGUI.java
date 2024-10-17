package Telas;

import Classes.Orcamento;
import Classes.SessionManager;
import DAO.OrcamentoDAO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.hibernate.Session;

/**
 *
 * @author danielp6r
 */
public class ListagemGUI extends javax.swing.JFrame {

    /**
     * Creates new form ClienteGUI
     */
    
    public ListagemGUI() {
        
        initComponents();
        
        // Carregar orçamentos na tabela
        loadOrcamentosIntoTable();
        filtrarPorData(); // Garante a filtragem depois de atualizar
        
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicializa Maximizado
        
        // Fecha todas as janelas quando clicar no x
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE); 
        
        // Configurar datas padrão
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -7); // uma semana atrás
        jDateChooser1.setDate(calendar.getTime()); // data uma semana atrás
        jDateChooser2.setDate(currentDate); // data atual
        
        // Listeners para os JDateChooser
        jDateChooser1.addPropertyChangeListener(evt -> filtrarPorData());
        jDateChooser2.addPropertyChangeListener(evt -> filtrarPorData());
        
        // Adicionar listener ao campo de busca
        addSearchListener();
        
        // Adiciona o MouseListener à lupa
        addLupaClickListener();
        
        txtBusca.setText("");
        filtrarOrcamentos();
        
        btnRelatorios.setVisible(false);
        
        // Coluna num com tamanho pequeno definido
        TableColumnModel columnModel = tblListagem.getColumnModel();
        TableColumn column = columnModel.getColumn(0); // 0 é o índice da coluna num
        column.setMinWidth(50);
        column.setMaxWidth(50);
        
        // Adiciona um listener para quando a janela ganhar foco
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                loadOrcamentosIntoTable(); // Chama o método ao ativar a janela
                filtrarPorData(); // Garante a filtragem depois de atualizar
            }
        });
        
        //debugCalendar();
 
    }
    
    //MÉTODOS ESPECÍFICOS PARA ESTA TELA:
    
    
    
     
    // Método para listar os Orçamentos na tabela
    private void loadOrcamentosIntoTable() {
        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        model.setRowCount(0); // Limpar tabela antes de adicionar dados
        OrcamentoDAO orcamentoDAO = new OrcamentoDAO();

        List<Orcamento> orcamentos = orcamentoDAO.getAllOrcamentos();

        if (orcamentos != null) {
            Collections.reverse(orcamentos); // Inverte a lista para mostrar os mais novos no topo
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")); // Formato de moeda brasileira

            for (Orcamento orcamento : orcamentos) {
                String nome;
                if (orcamento.getCliente() == null) {
                    nome = "";
                } else {
                    nome = orcamento.getCliente().getNomeCliente();
                }

                // Formata o ID do orçamento com zeros à esquerda
                String idFormatado = String.format("%04d", orcamento.getIdOrcamento());

                // Formata a data para AAAA-MM-DD para ordenação
                String dataFormatada = orcamento.getDataHora() != null
                        ? String.format("%d-%02d-%02d",
                                orcamento.getDataHora().getYear(),
                                orcamento.getDataHora().getMonthValue(),
                                orcamento.getDataHora().getDayOfMonth())
                        : "";

                // Verifica se o valor total não é nulo antes de formatar
                String valorFormatado = orcamento.getValTotal() != null
                        ? currencyFormat.format(orcamento.getValTotal())
                        : "R$ 0,00"; // Ou algum valor padrão

                Object[] row = {
                    idFormatado, // ID formatado
                    nome, // Nome do cliente
                    orcamento.getCarro(),
                    orcamento.getPlaca(),
                    dataFormatada, // Data formatada para ordenação
                    valorFormatado // Valor formatado como moeda
                };
                model.addRow(row);
            }
        } else {
            System.out.println("Nenhum orçamento encontrado ou erro ao carregar dados.");
        }
    }

    // Método para adicionar um DocumentListener ao campo de busca
    private void addSearchListener() {
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarOrcamentos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarOrcamentos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarOrcamentos();
            }
        });
    }

    // Método para filtrar os orçamentos de acordo com o texto digitado no campo de busca
    private void filtrarOrcamentos() {
        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblListagem.setRowSorter(sorter);

        String text = txtBusca.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // Ignora maiúsculas/minúsculas
        }
    }
    
    // Método para adicionar o MouseListener à lupa
    private void addLupaClickListener() {
        lblImgLupa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtBusca.requestFocus();
            }
        });
    }
    
    // Método para filtrar orçamentos por data pelos JCalendars
    private void filtrarPorData() {
        Date dataInicial = jDateChooser2.getDate();
        Date dataFinal = jDateChooser1.getDate();

        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblListagem.setRowSorter(sorter);

        // Se ambas as datas forem selecionadas
        if (dataInicial != null && dataFinal != null) {
            // Definindo o filtro
            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    // A data na tabela deve ser obtida como String
                    String dataOrcamentoStr = (String) entry.getValue(4); // Índice 4 para a coluna de data
                    try {
                        // Definindo o formato da data
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dataOrcamento = sdf.parse(dataOrcamentoStr);
                        // Verifica se a dataOrcamento está no intervalo
                        return !dataOrcamento.before(dataFinal) && !dataOrcamento.after(dataInicial);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
        } else {
            // Se as datas não forem válidas, mostra todos os orçamentos
            sorter.setRowFilter(null); // Mostra todos os orçamentos
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneAll = new javax.swing.JPanel();
        lblHead = new javax.swing.JLabel();
        panebotoes = new javax.swing.JPanel();
        lbla = new javax.swing.JLabel();
        lblPeriodo = new javax.swing.JLabel();
        lblImgLupa = new javax.swing.JLabel();
        txtBusca = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        btnExcluir = new javax.swing.JButton();
        paneListagem = new javax.swing.JScrollPane();
        tblListagem = new javax.swing.JTable();
        btnNovoOrcamento = new javax.swing.JButton();
        btnCadastro = new javax.swing.JButton();
        btnRelatorios = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Listagem de Orçamentos");
        setExtendedState(6);
        setFocusCycleRoot(false);
        setMinimumSize(new java.awt.Dimension(1366, 768));

        paneAll.setBackground(new java.awt.Color(255, 255, 255));

        lblHead.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblHead.setForeground(new java.awt.Color(0, 0, 0));
        lblHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHead.setText("LISTAGEM DE ORÇAMENTOS");
        lblHead.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        panebotoes.setBackground(new java.awt.Color(255, 255, 255));
        panebotoes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbla.setText("a");

        lblPeriodo.setText("Período");

        lblImgLupa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/pesquisar.png"))); // NOI18N
        lblImgLupa.setText(" ");

        txtBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscaActionPerformed(evt);
            }
        });

        btnExcluir.setText("Excluir (Del)");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panebotoesLayout = new javax.swing.GroupLayout(panebotoes);
        panebotoes.setLayout(panebotoesLayout);
        panebotoesLayout.setHorizontalGroup(
            panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panebotoesLayout.createSequentialGroup()
                .addGroup(panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panebotoesLayout.createSequentialGroup()
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbla)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnExcluir))
                    .addComponent(lblPeriodo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 630, Short.MAX_VALUE)
                .addComponent(lblImgLupa, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        panebotoesLayout.setVerticalGroup(
            panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panebotoesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panebotoesLayout.createSequentialGroup()
                        .addGroup(panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblImgLupa))
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panebotoesLayout.createSequentialGroup()
                        .addGroup(panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnExcluir)
                            .addGroup(panebotoesLayout.createSequentialGroup()
                                .addComponent(lblPeriodo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panebotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbla)
                                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(7, 7, 7))))
        );

        paneListagem.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblListagem.setFont(new java.awt.Font("Liberation Sans", 0, 15)); // NOI18N
        tblListagem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Num", "Cliente", "Veículo", "Placa", "Data", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        paneListagem.setViewportView(tblListagem);

        btnNovoOrcamento.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        btnNovoOrcamento.setText("Novo Orçamento (F1)");
        btnNovoOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoOrcamentoActionPerformed(evt);
            }
        });

        btnCadastro.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        btnCadastro.setText("Cadastros (F2)");
        btnCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastroActionPerformed(evt);
            }
        });

        btnRelatorios.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        btnRelatorios.setText("Relatórios (F3)");

        javax.swing.GroupLayout paneAllLayout = new javax.swing.GroupLayout(paneAll);
        paneAll.setLayout(paneAllLayout);
        paneAllLayout.setHorizontalGroup(
            paneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneAllLayout.createSequentialGroup()
                .addGroup(paneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneAllLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(paneAllLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(paneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panebotoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 1354, Short.MAX_VALUE)))
                    .addGroup(paneAllLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnNovoOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(btnRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paneAllLayout.setVerticalGroup(
            paneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneAllLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHead, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(paneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovoOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addComponent(panebotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNovoOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoOrcamentoActionPerformed
        // Cria a instância da tela ClienteGUI
        //OrcamentoGUI orcamentoGUI = new OrcamentoGUI();
        // Torna a tela visível
        //orcamentoGUI.setVisible(true);
        OrcamentoGUI.abrirNovaInstancia();
        
    }//GEN-LAST:event_btnNovoOrcamentoActionPerformed

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        // Cria a instância da tela ClienteGUI
        //ClienteGUI clienteGUI = new ClienteGUI();
        // Torna a tela visível
        //clienteGUI.setVisible(true);
        ClienteGUI.abrirNovaInstancia();

        
    }//GEN-LAST:event_btnCadastroActionPerformed

    private void txtBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscaActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        int selectedRow = tblListagem.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um Orçamento para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtém o ID formatado como String e remove os zeros à esquerda
        String idFormatado = (String) tblListagem.getValueAt(selectedRow, 0); // 0 é o índice da coluna ID na tabela
        long idOrcamento = Long.parseLong(idFormatado); // Converte o ID de String para Long

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este Orçamento?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            Session session = SessionManager.getInstance().getSession();
            OrcamentoDAO orcamentoDAO = new OrcamentoDAO();
            try {
                orcamentoDAO.excluirOrcamentoPorId(idOrcamento, session);
                JOptionPane.showMessageDialog(this, "Orçamento excluído com sucesso!");
                loadOrcamentosIntoTable(); // Atualiza a lista de orçamento na tabela após a exclusão
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir Orçamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                session.close(); // Fecha a sessão
            }
        }
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
            java.util.logging.Logger.getLogger(ListagemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListagemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListagemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListagemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListagemGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastro;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnNovoOrcamento;
    private javax.swing.JButton btnRelatorios;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel lblHead;
    private javax.swing.JLabel lblImgLupa;
    private javax.swing.JLabel lblPeriodo;
    private javax.swing.JLabel lbla;
    private javax.swing.JPanel paneAll;
    private javax.swing.JScrollPane paneListagem;
    private javax.swing.JPanel panebotoes;
    private javax.swing.JTable tblListagem;
    private javax.swing.JTextField txtBusca;
    // End of variables declaration//GEN-END:variables
}
