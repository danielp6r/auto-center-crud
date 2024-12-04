package Telas;

import Classes.Cliente;
import Classes.ItemOrcamento;
import Classes.Mercadoria;
import Classes.Orcamento;
import Classes.Produto;
import Classes.Servico;
import javax.swing.JOptionPane;
import Classes.SessionManager;
import DAO.ClienteDAO;
import DAO.ItemOrcamentoDAO;
import DAO.OrcamentoDAO;
import java.awt.event.ActionEvent;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;



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
    private Long idClienteSelecionado;
    private boolean clienteSelecionado = false; // Indica se o cliente foi selecionado da tabela

    /**
     * Creates new form ClienteGUI
     */
    public OrcamentoGUI() {
        initComponents();
        ajustarAlinhamentoTabela();
        atalhos();
        atualizarDataHora();
        
        // Inicializa os campos dinâmicos
        txtPeca = new JTextField();
        txtPeca.setText("");
        txtValorUnitario = new JTextField();
        txtValorUnitario.setText("");
        txtQuantidade = new JTextField();
        txtQuantidade.setText("");
        
        // Ajusta para fechar apenas a janela atual
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        lblHead.setText("NOVO ORÇAMENTO");
        //ABRE O A TELA MAXIMIZADA.
        //setExtendedState(MAXIMIZED_BOTH);

        // Ocultando a coluna número
        TableColumnModel columnModel = tblListagem.getColumnModel();
        TableColumn column = columnModel.getColumn(0); // 0 é o índice da coluna
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setWidth(0);
        
        //WindowListener para redefinir a instância ao fechar:
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                instance = null; // Reseta a instância ao fechar
            }
        });   
    }
    
    //MÉTODOS ESPECÍFICOS PARA ESTA TELA

    //Método para carregar orçamento existente
    public void carregarOrcamento(Long idOrcamento) {
        try {
            OrcamentoDAO orcamentoDAO = OrcamentoDAO.getInstance();
            Orcamento orcamento = orcamentoDAO.findById(idOrcamento);

            if (orcamento != null) {
                this.idOrcamentoGlobal = idOrcamento; // Mantém o estado do orçamento carregado
                
                // Atualiza o número do orçamento no lblHead
                lblHead.setText("ORÇAMENTO Nº: " + idOrcamento);
                
                // Preencher os campos
                txtCliente.setText(orcamento.getCliente() != null ? orcamento.getCliente().getNomeCliente() : "");
                txtVeiculo.setText(orcamento.getCarro() != null ? orcamento.getCarro() : "");
                txtPlaca.setText(orcamento.getPlaca() != null ? orcamento.getPlaca() : "");
                
                // Carregar e formatar data e hora do orçamento
                if (orcamento.getDataHora() != null) {
                    String dataHoraFormatada = orcamento.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    lblDataHora.setText(dataHoraFormatada);
                } else {
                    lblDataHora.setText(""); // Limpa o campo caso não haja data
                }
                
                // Carregar itens do orçamento
                atualizarGridItens();

                // Atualizar os totais
                calcularTotais(orcamento.getItensOrcamento());
            } else {
                JOptionPane.showMessageDialog(this, "Orçamento não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar orçamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Método para calcular os totais ao abrir um orçamento já existente 
    private void calcularTotais(List<ItemOrcamento> itens) {
        double totalPecas = 0.0;
        double totalServicos = 0.0;

        for (ItemOrcamento item : itens) {
            Produto produto = item.getProduto();
            if (produto instanceof Mercadoria) {
                totalPecas += item.getSubtotal();
            } else if (produto instanceof Servico) {
                totalServicos += item.getSubtotal();
            }
        }

        // Atualizar os campos de totais
        txtTotalPecas.setText(String.format("R$%.2f", totalPecas));
        txtTotalServicos.setText(String.format("R$%.2f", totalServicos));
        txtValorFinal.setText(String.format("R$%.2f", totalPecas + totalServicos));
    }


    // Método para atualizar lblDataHora com a data e hora no formato correto
    public void atualizarDataHora() {
        try {
            // Captura a data e hora atuais
            LocalDateTime agora = LocalDateTime.now();

            // Define o formato correto
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Formata a data e hora
            String dataHoraFormatada = agora.format(formatter);

            // Atualiza o lblDataHora com o valor formatado
            lblDataHora.setText(dataHoraFormatada);
        } catch (Exception e) {
            // Tratamento de erros (se necessário)
            lblDataHora.setText("Erro ao carregar data/hora");
            e.printStackTrace();
        }
    }
    
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
                // Recupera o ID do orçamento atualmente carregado
                Long idAtual = getIdOrcamento();

                // Fecha a instância atual
                dispose();

                if (idAtual != null) {
                    // Reabre a mesma instância com o mesmo ID
                    OrcamentoGUI orcamentoGUI = new OrcamentoGUI();
                    orcamentoGUI.carregarOrcamento(idAtual);
                    orcamentoGUI.setVisible(true);
                } else {
                    // Caso não tenha ID (é uma nova instância), cria uma nova tela em branco
                    OrcamentoGUI.abrirNovaInstancia();
                }
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
    public static synchronized OrcamentoGUI getInstance() {
        if (instance == null) {
            instance = new OrcamentoGUI();
        }
        return instance;
    }

    
    // Método para abrir a tela de novo orçamento
    public static void abrirNovaInstancia() {
        if (instance == null) {
            instance = new OrcamentoGUI(); // Cria uma nova instância se não houver nenhuma aberta
        }
        instance.setVisible(true); // Traz a janela para frente
        instance.requestFocus(); // Garante que a janela recebe o foco
    }
    
    @Override
    public void dispose() {
        super.dispose(); // Fecha a janela atual
        instance = null; // Reseta a instância
    }
    
    //Método para resetar campos
    private void resetCampos() {
        // Limpa os campos de texto
        txtCliente.setText("");
        txtVeiculo.setText("");
        txtPlaca.setText("");
        txtTotalPecas.setText("R$0,00");
        txtTotalServicos.setText("R$0,00");
        txtValorFinal.setText("R$0,00");

        // Limpa a tabela de itens
        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        model.setRowCount(0); // Remove todas as linhas

        // Atualiza o cabeçalho para "NOVO ORÇAMENTO"
        lblHead.setText("NOVO ORÇAMENTO");

        // Reseta os identificadores globais
        idOrcamentoGlobal = 0;
        idClienteSelecionado = null;
        clienteSelecionado = false;
    }

    
    // Atualiza cliente no orçamento
    public void setClienteSelecionado(Long idCliente, String nomeCliente) {
        this.idClienteSelecionado = idCliente; // Define o ID do cliente selecionado
        txtCliente.setText(nomeCliente); // Atualiza o campo de texto com o nome do cliente
        clienteSelecionado = true; // Marca o cliente como selecionado
    }
    
    // Vincula cliente ao orçamento no banco
    private void vincularClienteAoOrcamento(Long idCliente) { 
        if (idOrcamentoGlobal > 0) { // Verifica se orçamento existe
            Session session = SessionManager.getInstance().getSession();
            Transaction transaction = session.beginTransaction();
            try {
                String comandoSql = "UPDATE orcamentos SET id_cliente = " + idCliente
                        + " WHERE id_orcamento_ = " + idOrcamentoGlobal;
                session.createNativeQuery(comandoSql).executeUpdate(); // Atualiza cliente no banco
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback(); // Reverte em caso de erro
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao vincular cliente: " + e.getMessage());
            } finally {
                session.close(); // Fecha sessão
            }
        } else {
            JOptionPane.showMessageDialog(this, "Orçamento ainda não foi criado."); // Alerta para criar orçamento
        }
    }
    
    public void resetarCliente() { // Reseta o cliente selecionado e limpa os campos
        clienteSelecionado = false; // Reseta o controle para criação de cliente
        idClienteSelecionado = null; // Remove o ID do cliente selecionado
        txtCliente.setText(""); // Limpa o campo de texto
    }
    
    // Getter para o ID do orçamento atual
    public Long getIdOrcamento() {
        return idOrcamentoGlobal; // Retorna o ID do orçamento carregado em OrcamentoGUI
    }
    
    private void ajustarAlinhamentoTabela() {
        // Configurar o alinhamento à esquerda para todas as células
        DefaultTableCellRenderer renderizadorCélula = new DefaultTableCellRenderer();
        renderizadorCélula.setHorizontalAlignment(SwingConstants.LEFT);

        // Aplica o renderizador para todas as colunas
        for (int i = 0; i < tblListagem.getColumnCount(); i++) {
            tblListagem.getColumnModel().getColumn(i).setCellRenderer(renderizadorCélula);
        }

        // Apenas ajusta o alinhamento do título, sem alterar o fundo ou o estilo visual
        DefaultTableCellRenderer renderizadorTitulo = (DefaultTableCellRenderer) tblListagem.getTableHeader().getDefaultRenderer();
        renderizadorTitulo.setHorizontalAlignment(SwingConstants.LEFT);
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
        btnImprimir = new javax.swing.JButton();

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

        btnCancelar.setText("Fechar");
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
                .addGap(18, 18, 18)
                .addComponent(btnExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 672, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(140, 140, 140)
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
                "Número Item", "Descrição", "Valor Un.", "Qtd.", "Subtotal"
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
        lblDataHora.setText("     Data - Hora");

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

        txtCliente.setEditable(false);
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

        btnImprimir.setText("Imprimir F12");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

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
                            .addComponent(lblCliente)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblVeiculo)
                            .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPlaca)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addComponent(lblDataHora, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimir)
                        .addGap(12, 12, 12))
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
                            .addComponent(lblCliente))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PaneAllLayout.createSequentialGroup()
                        .addComponent(lblPlaca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnImprimir)
                        .addComponent(lblDataHora, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(paneBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
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

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        ClienteGUI clienteGUI = ClienteGUI.getInstance(); // Garante instância única
        clienteGUI.setModoVinculacao(true);
        clienteGUI.setVisible(true); // Exibe ClienteGUI
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

    private long SalvarOrcamento(Boolean exibirMensagem) {
        // Obtém as informações digitadas na tela
        String nome = txtCliente.getText().trim();
        String veiculo = txtVeiculo.getText().trim();
        String placa = txtPlaca.getText().trim();

        // Validação dos campos obrigatórios
        if (nome.isEmpty() || veiculo.isEmpty() || placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
            return -1; // Retorna -1 em caso de erro de validação
        }

        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        long idOrcamento = -1;
        long idCliente = -1; // Inicializa com valor padrão

        try {
            ClienteDAO clienteDAO = new ClienteDAO();

            if (clienteSelecionado) { // Cliente foi selecionado na tabela
                Cliente clienteExistente = clienteDAO.getClienteById(idClienteSelecionado, session);

                if (clienteExistente != null) {
                    // Atualiza o nome do cliente se for diferente
                    if (!nome.equalsIgnoreCase(clienteExistente.getNomeCliente())) {
                        clienteDAO.atualizarCliente(
                                idClienteSelecionado,
                                nome,
                                "", // Telefone vazio por padrão
                                "", // Email vazio por padrão
                                "", // CPF ou CNPJ vazio por padrão
                                true, // Assume que é Pessoa Física
                                session
                        );
                    }
                    idCliente = idClienteSelecionado;
                } else {
                    clienteSelecionado = false; // Desvincula o cliente se não existir
                }
            }

            if (!clienteSelecionado) { // Caso o cliente não esteja selecionado
                // Verifica se já existe um cliente com o mesmo nome
                List<Cliente> clientes = clienteDAO.getAllClientes();
                Cliente clienteDuplicado = clientes.stream()
                        .filter(c -> c.getNomeCliente().equalsIgnoreCase(nome))
                        .findFirst()
                        .orElse(null);

                if (clienteDuplicado != null) {
                    // Cliente já existe: associa o cliente duplicado
                    JOptionPane.showMessageDialog(this, "O cliente já existe. Vinculando ao cliente existente.");
                    idCliente = clienteDuplicado.getIdCliente();
                    clienteSelecionado = true;
                    idClienteSelecionado = idCliente;
                } else {
                    // Cliente não existe: cria um novo
                    idCliente = clienteDAO.findNextId(session);
                    clienteDAO.salvarCliente(nome, "", "", "", true, session); // Por padrão, considera Pessoa Física
                }
            }

            // Criação do orçamento
            if (idCliente == -1) {
                throw new RuntimeException("Erro ao salvar cliente. ID do cliente não foi inicializado.");
            }

            OrcamentoDAO orcamentoDAO = OrcamentoDAO.getInstance();
            idOrcamento = orcamentoDAO.findNextId(session);
            idOrcamentoGlobal = idOrcamento; // Atualiza a variável global
            lblHead.setText("ORÇAMENTO Nº: " + idOrcamentoGlobal);

            String comandoSqlOrcamento = "INSERT INTO orcamentos (id_cliente, id_orcamento_, placa, carro, data_hora) VALUES ("
                    + idCliente + ", " + idOrcamento + ", '" + placa + "', '" + veiculo + "', current_timestamp)";
            session.createNativeQuery(comandoSqlOrcamento).executeUpdate();

            transaction.commit();

            if (exibirMensagem) {
                JOptionPane.showMessageDialog(this, "Orçamento salvo com sucesso!");
            }
        } catch (Exception ex) {
            transaction.rollback();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar orçamento: " + ex.getMessage());
        } finally {
            session.close();
        }

        return idOrcamento;
    }
    
    private void btnProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProdutoActionPerformed
        String nome = txtCliente.getText();
        String veiculo = txtVeiculo.getText();
        String placa = txtPlaca.getText();

        // Validação dos campos obrigatórios
        if (nome.isEmpty() || veiculo.isEmpty() || placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
            return;
        }

        ProdutoGUI produtoGUI = ProdutoGUI.getInstance();
        produtoGUI.limparCampos();
        produtoGUI.setDescricaoLabel("Descrição da Peça / Mercadoria");
        produtoGUI.setTipoProduto("Mercadoria"); // Definindo o tipo como Mercadoria
        produtoGUI.setVisible(true);

        if (idOrcamentoGlobal <= 0) {
            idOrcamentoGlobal = SalvarOrcamento(false);
        }

        if (idOrcamentoGlobal > 0) {
            ProdutoGUI.abrirNovaInstancia(this);
        }
    }//GEN-LAST:event_btnProdutoActionPerformed
 
    private void UpdateValorTotalOrcamento() {
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            String comandoSqlUpdateOrcamento = "UPDATE orcamentos SET val_total = " + valorTotalGlobal
                    + " WHERE id_orcamento_ = " + idOrcamentoGlobal;
            session.createNativeQuery(comandoSqlUpdateOrcamento).executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback(); // Fazer rollback em caso de erro
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o valor total: " + ex.getMessage());
        } finally {
            session.close(); // Garantir que a sessão seja fechada
        }
    }

        
        
    private double valorTotalGlobal;
    public void atualizarGridItens() {
        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        model.setRowCount(0); // Limpar tabela antes de adicionar dados
        ItemOrcamentoDAO itemOrcamentoDAO = new ItemOrcamentoDAO();
        double valorTotal = 0;
        double valorPecas = 0;
        double valorServicos = 0;

        // Obter todos os itens do orçamento
        List<ItemOrcamento> itens = itemOrcamentoDAO.getAllItens(idOrcamentoGlobal);

        if (itens != null) {
            for (ItemOrcamento item : itens) {
                String descricao = null;
                String tipoProduto = null;

                // Determinar a descrição e o tipo do item baseado na hierarquia
                if (item.getProduto() != null) {
                    descricao = item.getProduto().getDescricao();
                    tipoProduto = item.getProduto().getClass().getSimpleName();

                    // Atualizar os totais com base no tipo do produto
                    if ("Mercadoria".equals(tipoProduto)) {
                        valorPecas += item.getPrecoUn() * item.getQuantidade();
                    } else if ("Servico".equals(tipoProduto)) {
                        valorServicos += item.getPrecoUn() * item.getQuantidade();
                    }
                }

                // Adicionar linha à tabela
                Object[] row = {
                    item.getIdItemOrcamento(),
                    descricao != null ? descricao : "N/A",
                    item.getPrecoUn(),
                    item.getQuantidade(),
                    item.getPrecoUn() * item.getQuantidade()
                };
                model.addRow(row);
            }
        } else {
            System.out.println("Nenhum item encontrado ou erro ao carregar dados.");
        }

        // Atualizar os valores na interface
        valorTotal = valorPecas + valorServicos;
        valorTotalGlobal = valorTotal;

        txtValorFinal.setText(String.format("R$%.2f", valorTotal));
        txtTotalPecas.setText(String.format("R$%.2f", valorPecas));
        txtTotalServicos.setText(String.format("R$%.2f", valorServicos));

        // Atualizar no banco os valores do orçamento
        OrcamentoDAO orcamentoDAO = OrcamentoDAO.getInstance();
        orcamentoDAO.atualizarValoresOrcamento(idOrcamentoGlobal, valorPecas, valorServicos, valorTotal);
    }

    
    private void btnServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnServicoActionPerformed
        String nome = txtCliente.getText();
        String veiculo = txtVeiculo.getText();
        String placa = txtPlaca.getText();

        // Validação dos campos obrigatórios
        if (nome.isEmpty() || veiculo.isEmpty() || placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Os campos Nome, Veículo e Placa precisam ser preenchidos!");
            return;
        }

        ProdutoGUI produtoGUI = ProdutoGUI.getInstance();
        produtoGUI.limparCampos();
        produtoGUI.setDescricaoLabel("Descrição do Serviço");
        produtoGUI.setTipoProduto("Servico"); // Definindo o tipo como Serviço
        produtoGUI.setVisible(true);

        if (idOrcamentoGlobal <= 0) {
            idOrcamentoGlobal = SalvarOrcamento(false);
        }

        if (idOrcamentoGlobal > 0) {
            ProdutoGUI.abrirNovaInstancia(this);
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

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        resetCampos(); // Limpa os campos antes de fechar
        dispose(); // Fecha a janela atual
        instance = null; // Reseta a instância
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        //
    }//GEN-LAST:event_btnImprimirActionPerformed

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
    private javax.swing.JButton btnImprimir;
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