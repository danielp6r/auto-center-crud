package Telas;

import Classes.ItemOrcamento;
import javax.swing.JOptionPane;
import Classes.SessionManager;
import DAO.ClienteDAO;
import DAO.ItemOrcamentoDAO;
import DAO.OrcamentoDAO;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import org.hibernate.Transaction;
import org.hibernate.Session;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author daniel
 */
public class OrcamentoGUI extends javax.swing.JFrame {
    // Adiciona um campo estático para armazenar a instância única
    private static OrcamentoGUI instance;
    
    // Campos para os componentes dinâmicos
    private JTextField txtPeca;
    private JTextField txtValorUnitario;
    private JTextField txtQuantidade;

    /**
     * Creates new form ClienteGUI
     */
    public OrcamentoGUI() {
        initComponents();
        atalhos();
        
        // Inicializa os campos dinâmicos
        txtPeca = new JTextField();
        txtPeca.setText("");
        txtValorUnitario = new JTextField();
        txtValorUnitario.setText("");
        txtQuantidade = new JTextField();
        txtQuantidade.setText("");
        
        // Ajusta para fechar apenas a janela atual
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        lblHead.setText("ORÇAMENTO");
        //ABRE O A TELA MAXIMIZADA.
        //setExtendedState(MAXIMIZED_BOTH);
        
        lblDataHora.setText(LocalDateTime.now().toString());
        lblDataHora.setVisible(false);
        
        // Ocultando a coluna número
        TableColumnModel columnModel = tblListagem.getColumnModel();
        TableColumn column = columnModel.getColumn(0); // 0 é o índice da coluna
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setWidth(0);
    }
    
    /*
    MÉTODOS ESPECÍFICOS PARA ESTA TELA
     */
    
    // Método personalizado para configurar os atalhos de teclado
    private void atalhos() {
        JRootPane rootPane = this.getRootPane();

        // Mapeamento global da tecla F1 para Salvar
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "salvarAction");
        rootPane.getActionMap().put("salvarAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSalvar.doClick(); // Simula o clique no botão Salvar
            }
        });

        // Mapeamento da tecla F2 para inserir peça (dentro da JTable)
        tblListagem.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("F2"), "inserirPeça");
        tblListagem.getActionMap().put("inserirPeça", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("F2 pressionado - Inserir Peça (dentro da JTable)"); // Para depuração
                btnProduto.doClick(); // Simula o clique no botão inserir Peça
            }
        });

        // Mapeamento da tecla F2 para inserir peça (fora da JTable)
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "inserirPeça");
        rootPane.getActionMap().put("inserirPeça", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("F2 pressionado - Inserir Peça (fora da JTable)"); // Para depuração
                btnProduto.doClick(); // Simula o clique no botão Inserir Peça
            }
        });
        
        // Mapeamento global da tecla F3 para Inserir Serviço
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F3"), "InserirServiço");
        rootPane.getActionMap().put("InserirServiço", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnServico.doClick(); // Simula o clique no botão Inserir Serviço
            }
        });


        // Mapeamento global da tecla Delete para Excluir
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "excluirAction");
        rootPane.getActionMap().put("excluirAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExcluir.doClick(); // Simula o clique no botão Excluir
            }
        });

        // Mapeamento global da tecla F5 para atualizar a tela (reset)
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "reset");
        rootPane.getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                OrcamentoGUI.abrirNovaInstancia();
            }
        });

        // Mapeamento global da tecla esc para fechar a tela
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "dispose");
        rootPane.getActionMap().put("dispose", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    // Método para obter a instância única da tela
    public static OrcamentoGUI getInstance() {
        if (instance == null) {
            instance = new OrcamentoGUI();
        }
        return instance;
    }
    
    // Método para abrir a tela de novo orçamento
    public static void abrirNovaInstancia() {
        if (instance == null) {
            instance = new OrcamentoGUI();
            // Define a janela como sempre no topo
            //instance.setAlwaysOnTop(true);
        }
        instance.setVisible(true);
        instance.requestFocus(); // Garante que a janela receba o foco
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PaneAll = new javax.swing.JPanel();
        lblHead = new javax.swing.JLabel();
        paneBotoes = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnProduto = new javax.swing.JButton();
        btnServico = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        paneListagem = new javax.swing.JScrollPane();
        tblListagem = new javax.swing.JTable();
        btnCadastro = new javax.swing.JButton();
        txtVeiculo = new javax.swing.JTextField();
        txtPlaca = new javax.swing.JTextField();
        lblVeiculo = new javax.swing.JLabel();
        lblPlaca = new javax.swing.JLabel();
        lblDataHora = new javax.swing.JLabel();
        paneBot = new javax.swing.JPanel();
        txtCliente = new javax.swing.JTextField();
        lblCliente = new javax.swing.JLabel();
        paneValores = new javax.swing.JPanel();
        lblTotalPecas = new javax.swing.JLabel();
        lblTotalServicos = new javax.swing.JLabel();
        lblValorFinal = new javax.swing.JLabel();
        txtTotalServicos = new javax.swing.JTextField();
        txtTotalPecas = new javax.swing.JTextField();
        txtValorFinal = new javax.swing.JTextField();
        paneObs = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        lblObs = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Orçamentos");
        setResizable(false);

        PaneAll.setBackground(new java.awt.Color(255, 255, 255));

        lblHead.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblHead.setForeground(new java.awt.Color(0, 0, 0));
        lblHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHead.setText("ORÇAMENTO Nº:");
        lblHead.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        paneBotoes.setBackground(new java.awt.Color(255, 255, 255));
        paneBotoes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl1.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lbl1.setForeground(new java.awt.Color(0, 0, 0));
        lbl1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl1.setText("LISTAGEM DE PEÇAS E SERVIÇOS");
        lbl1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnProduto.setText("Inserir Peça");
        btnProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProdutoActionPerformed(evt);
            }
        });

        btnServico.setText("Inserir Serviço");
        btnServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnServicoActionPerformed(evt);
            }
        });

        btnExcluir.setText("Excluir Item");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneBotoesLayout = new javax.swing.GroupLayout(paneBotoes);
        paneBotoes.setLayout(paneBotoesLayout);
        paneBotoesLayout.setHorizontalGroup(
            paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnServico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExcluir)
                .addGap(18, 18, 18)
                .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap())
        );
        paneBotoesLayout.setVerticalGroup(
            paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBotoesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancelar)
                            .addComponent(btnSalvar)
                            .addComponent(lbl1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(paneBotoesLayout.createSequentialGroup()
                        .addComponent(btnProduto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnServico)
                            .addComponent(btnExcluir))))
                .addContainerGap())
        );

        paneListagem.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblListagem.setFont(new java.awt.Font("Liberation Sans", 0, 15)); // NOI18N
        tblListagem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Número Item", "Descrição", "Preço Un.", "Quantidade", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        paneListagem.setViewportView(tblListagem);

        btnCadastro.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        btnCadastro.setText("Cadastro");
        btnCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastroActionPerformed(evt);
            }
        });

        txtVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVeiculoActionPerformed(evt);
            }
        });

        lblVeiculo.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblVeiculo.setText("Veículo");

        lblPlaca.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblPlaca.setText("Placa");

        lblDataHora.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblDataHora.setText("Data - Hora");

        javax.swing.GroupLayout paneBotLayout = new javax.swing.GroupLayout(paneBot);
        paneBot.setLayout(paneBotLayout);
        paneBotLayout.setHorizontalGroup(
            paneBotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        paneBotLayout.setVerticalGroup(
            paneBotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 44, Short.MAX_VALUE)
        );

        txtCliente.setName("txtCliente"); // NOI18N
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        lblCliente.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblCliente.setText("Cliente");

        lblTotalPecas.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblTotalPecas.setText("Total de Peças");

        lblTotalServicos.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblTotalServicos.setText("Total de Serviços");

        lblValorFinal.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblValorFinal.setText("Valor Final");

        txtTotalServicos.setEditable(false);
        txtTotalServicos.setBackground(new java.awt.Color(0, 0, 0));
        txtTotalServicos.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        txtTotalServicos.setForeground(new java.awt.Color(51, 204, 0));
        txtTotalServicos.setText("R$0,00");

        txtTotalPecas.setEditable(false);
        txtTotalPecas.setBackground(new java.awt.Color(0, 0, 0));
        txtTotalPecas.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        txtTotalPecas.setForeground(new java.awt.Color(51, 204, 0));
        txtTotalPecas.setText("R$0,00");
        txtTotalPecas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalPecasActionPerformed(evt);
            }
        });

        txtValorFinal.setEditable(false);
        txtValorFinal.setBackground(new java.awt.Color(0, 0, 0));
        txtValorFinal.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        txtValorFinal.setForeground(new java.awt.Color(51, 204, 0));
        txtValorFinal.setText("R$0,00");

        javax.swing.GroupLayout paneValoresLayout = new javax.swing.GroupLayout(paneValores);
        paneValores.setLayout(paneValoresLayout);
        paneValoresLayout.setHorizontalGroup(
            paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneValoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalPecas)
                    .addComponent(txtTotalPecas, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalServicos)
                    .addComponent(txtTotalServicos, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblValorFinal)
                    .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(192, 192, 192))
        );
        paneValoresLayout.setVerticalGroup(
            paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneValoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalPecas)
                    .addComponent(lblTotalServicos)
                    .addComponent(lblValorFinal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalServicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalPecas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        paneObs.setViewportView(jTextArea2);

        lblObs.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblObs.setText("Observações:");

        javax.swing.GroupLayout PaneAllLayout = new javax.swing.GroupLayout(PaneAll);
        PaneAll.setLayout(PaneAllLayout);
        PaneAllLayout.setHorizontalGroup(
            PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE)
            .addGroup(PaneAllLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paneBotoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PaneAllLayout.createSequentialGroup()
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCliente))
                        .addGap(18, 18, 18)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblVeiculo)
                            .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PaneAllLayout.createSequentialGroup()
                                .addComponent(lblPlaca)
                                .addGap(68, 68, 68)
                                .addComponent(lblDataHora)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PaneAllLayout.createSequentialGroup()
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(paneValores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(paneBot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(paneObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObs)))
                    .addComponent(lblHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PaneAllLayout.setVerticalGroup(
            PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaneAllLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHead, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PaneAllLayout.createSequentialGroup()
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblVeiculo)
                            .addComponent(lblPlaca)
                            .addComponent(lblCliente)
                            .addComponent(lblDataHora))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(paneBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PaneAllLayout.createSequentialGroup()
                        .addComponent(paneValores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneBot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PaneAllLayout.createSequentialGroup()
                        .addComponent(lblObs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PaneAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PaneAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVeiculoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVeiculoActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose(); // Fecha a janela
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        // Cria a instância da tela ClienteGUI
        //ClienteGUI clienteGUI = new ClienteGUI();
        // Torna a tela visível
        //clienteGUI.setVisible(true);
        ClienteGUI.abrirNovaInstancia();
    }//GEN-LAST:event_btnCadastroActionPerformed

    private void txtTotalPecasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalPecasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalPecasActionPerformed

    
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        //Pega as strings digitadas pelo usuário na tela
        String nome = txtCliente.getText();
        String veiculo = txtVeiculo.getText();
        String placa = txtPlaca.getText();

        //Agora vai salvar o orçamento para poder salvar o item
        if (nome.equals("") || veiculo.equals("") || placa.equals("")) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
        } else {
            OrcamentoGUI instanciaOrcamento = getInstance();
            // consulta se o orçamento já existe na base de dados, se existir, 
            // faz o update, senão, faz o insert do orçamento antes 
            // de inserir o item
            if (idOrcamentoGlobal <= 0) {;
                SalvarOrcamento(false);
            }
            if (idOrcamentoGlobal > 0) {
                //
            }
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    // Variável global para guardar a id do Orçamento;
    public long idOrcamentoGlobal;

    private void SalvarOrcamento(Boolean exibirMensagem) {

        // Pega as strings digitadas pelo usuário na tela
        String nome = txtCliente.getText();
        String veiculo = txtVeiculo.getText();
        String placa = txtPlaca.getText();

        // Se os campos veículo, nome e placa estiverem vazios, dá um alerta ao usuário e impede de salvar
        if (nome.equals("") || veiculo.equals("") || placa.equals("")) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
            return; // Adicionando return para impedir a execução do restante do método
        }

        // Salva o cliente no banco de dados
        // starta a transaction
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            ClienteDAO cliente = new ClienteDAO();
            long idCliente = cliente.findNextId(session);

            String comandoSqlCliente = "insert into clientes(id_cliente, tipo_cliente, nome_cliente) values (" + idCliente + ", 'PessoaFisica', '" + nome + "')";
            session.createNativeQuery(comandoSqlCliente).executeUpdate();

            OrcamentoDAO orcamento = new OrcamentoDAO();
            long idOrcamento = orcamento.findNextId(session);
            idOrcamentoGlobal = idOrcamento;
            lblHead.setText("ORÇAMENTO Nº: " + idOrcamentoGlobal);

            String comandoSqlOrcamento = "insert into orcamentos(id_cliente, id_orcamento_, placa, carro, data_hora) values (" + idCliente + "," + idOrcamento + ", '" + placa + "', '" + veiculo + "', current_timestamp)";
            session.createNativeQuery(comandoSqlOrcamento).executeUpdate();

            transaction.commit();
            if (exibirMensagem) {
                JOptionPane.showMessageDialog(this, "Orçamento salvo com sucesso!");
            }
        } catch (Exception ex) {
            // Se der erro, dá um rollback na transação
            transaction.rollback();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar Orçamento: " + ex.getMessage());
        }
    }
            
    private void btnProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProdutoActionPerformed
       
        //Pega as strings digitadas pelo usuário na tela
        String nome = txtCliente.getText();
        String veiculo = txtVeiculo.getText();
        String placa = txtPlaca.getText();
        
        //Agora vai salvar o orçamento para poder salvar o item
        if (nome.equals("") || veiculo.equals("") || placa.equals("")) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
        } else {
            OrcamentoGUI instanciaOrcamento = getInstance();
            // consulta se o orçamento já existe na base de dados, se existir, 
            // faz o update, senão, faz o insert do orçamento antes 
            // de inserir o item
            if (idOrcamentoGlobal <= 0) {;
                SalvarOrcamento(false);
            }
            if (idOrcamentoGlobal > 0) {
                ProdutoGUI.abrirNovaInstancia(instanciaOrcamento);
            }
        }    
    }//GEN-LAST:event_btnProdutoActionPerformed
 
    private void UpdateValorTotalOrcamento(){
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            
            String comandoSqlUpdateOrcamento = "update orcamentos set val_total = " + Double.toString(valorTotalGlobal) + " where id_orcamento_ = " + idOrcamentoGlobal;
            session.createNativeQuery(comandoSqlUpdateOrcamento).executeUpdate();
            
            transaction.commit();
            
        } catch (Exception ex) {
            //se der erro, dá um rollback na transação
            transaction.rollback();
            ex.printStackTrace();
            
        }
}
        
        
    private double valorTotalGlobal;
    public void atualizarGridItens(){
        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        model.setRowCount(0); // Limpar tabela antes de adicionar dados
        ItemOrcamentoDAO itemOrcamentoDAO = new ItemOrcamentoDAO(); 
        double valorTotal = 0;
        double valorPecas = 0;
        double valorServicos = 0;
        List<ItemOrcamento> itens = itemOrcamentoDAO.getAllItens(idOrcamentoGlobal);
        
        if (itens != null) {
            for (ItemOrcamento item : itens) {
                String descricao;
                
                valorPecas = valorPecas + (item.getPrecoUn()*item.getQuantidade());
                
                
                Object[] row = {
                    item.getIdItemOrcamento(),
                    item.getProduto().getDescricao(),
                    item.getPrecoUn(),
                    item.getQuantidade(),
                    item.getPrecoUn()*item.getQuantidade()
                };
                model.addRow(row);
            }
        } else {
            System.out.println("Nenhum item encontrado ou erro ao carregar dados.");
        }
        UpdateValorTotalOrcamento();
        valorTotal = valorPecas + valorServicos;
        valorTotalGlobal = valorTotal; 
        txtValorFinal.setText(String.format("R$%.2f", valorTotal));
        txtTotalPecas.setText(String.format("R$%.2f", valorPecas));
    }
    
    
    private void btnServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnServicoActionPerformed
        //Pega as strings digitadas pelo usuário na tela
        String nome = txtCliente.getText();
        String veiculo = txtVeiculo.getText();
        String placa = txtPlaca.getText();

        //Agora vai salvar o orçamento para poder salvar o item
        if (nome.equals("") || veiculo.equals("") || placa.equals("")) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
        } else {
            OrcamentoGUI instanciaOrcamento = getInstance();
            // consulta se o orçamento já existe na base de dados, se existir, 
            // faz o update, senão, faz o insert do orçamento antes 
            // de inserir o item
            if (idOrcamentoGlobal <= 0) {;
                SalvarOrcamento(false);
            }
            if (idOrcamentoGlobal > 0) {
                ProdutoGUI.abrirNovaInstancia(instanciaOrcamento);
            }
        }
    }//GEN-LAST:event_btnServicoActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        // TODO add your handling code here:
        //pega a lnha selecionada
        int selectedRow = tblListagem.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long idOrcamentoItem = (long) tblListagem.getValueAt(selectedRow, 0); // 0 é o índice da coluna ID na tabela

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este item?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            Session session = SessionManager.getInstance().getSession();
            ItemOrcamentoDAO itemOrcamentoDAO = new ItemOrcamentoDAO();
            try {
                itemOrcamentoDAO.excluirItemPorId(idOrcamentoItem, session);
                JOptionPane.showMessageDialog(this, "Item excluído com sucesso!");
                atualizarGridItens();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao editar Item: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
            java.util.logging.Logger.getLogger(OrcamentoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrcamentoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrcamentoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrcamentoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrcamentoGUI().setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PaneAll;
    private javax.swing.JButton btnCadastro;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnProduto;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnServico;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblDataHora;
    private javax.swing.JLabel lblHead;
    private javax.swing.JLabel lblObs;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblTotalPecas;
    private javax.swing.JLabel lblTotalServicos;
    private javax.swing.JLabel lblValorFinal;
    private javax.swing.JLabel lblVeiculo;
    private javax.swing.JPanel paneBot;
    private javax.swing.JPanel paneBotoes;
    private javax.swing.JScrollPane paneListagem;
    private javax.swing.JScrollPane paneObs;
    private javax.swing.JPanel paneValores;
    private javax.swing.JTable tblListagem;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtPlaca;
    private javax.swing.JTextField txtTotalPecas;
    private javax.swing.JTextField txtTotalServicos;
    private javax.swing.JTextField txtValorFinal;
    private javax.swing.JTextField txtVeiculo;
    // End of variables declaration//GEN-END:variables
}