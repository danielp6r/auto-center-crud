package Telas;

import Classes.ItemOrcamento;
import Classes.Orcamento;
import Classes.SessionManager;
import DAO.OrcamentoDAO;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.hibernate.Session;

/**
 *
 * @author danielp6r
 */
public class ListagemGUI extends javax.swing.JFrame {
    
    // Lista para gerenciar múltiplas instâncias de OrcamentoGUI abertas
    private final Map<Long, OrcamentoGUI> instanciasOrcamentos = new HashMap<>();

    /**
     * Creates new form ClienteGUI
     */
    
    public ListagemGUI() {
        
        initComponents();
        ajustarAlinhamentoTabela();
        atalhos();
        
        // Remove foco de todos os componentes
        setFocusable(true);
        requestFocusInWindow(); // Remove o foco de qualquer componente
        
        // Carregar orçamentos na tabela
        loadOrcamentosIntoTable();
        filtrarPorData(); // Garante a filtragem depois de atualizar
        
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicializa Maximizado
        
        // Fecha todas as janelas quando clicar no x
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE); 
        
        setarDatasPadrao();
        
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
                if (txtBusca.getText().isEmpty()) {
                    //Não faz nada se tiver vazio
                } else {
                    filtrarOrcamentos(); // Filtra pelo allTimeSearch
                }               
            }
        });
        
        // Listener para duplo clique na tabela
        tblListagem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tblListagem.getSelectedRow() != -1) {
                    int selectedRow = tblListagem.getSelectedRow();
                    String idFormatado = tblListagem.getValueAt(selectedRow, 0).toString();
                    Long idOrcamento = Long.parseLong(idFormatado);

                    // Obtém a instância existente da OrcamentoGUI
                    OrcamentoGUI orcamentoGUI = OrcamentoGUI.getInstance();

                    // Carrega os dados do orçamento
                    orcamentoGUI.carregarOrcamento(idOrcamento);

                    // Mostra a janela e traz para frente
                    orcamentoGUI.setVisible(true);
                    orcamentoGUI.toFront();
                }
            }
        });
        
        // Listener para duplo clique na tabela com várias instancias (Não funciona)
        /*tblListagem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tblListagem.getSelectedRow() != -1) {
                    int selectedRow = tblListagem.getSelectedRow();
                    String idFormatado = tblListagem.getValueAt(selectedRow, 0).toString();
                    Long idOrcamento = Long.parseLong(idFormatado);

                    // Verifica se já existe uma instância para este ID
                    OrcamentoGUI orcamentoGUI = instanciasOrcamentos.get(idOrcamento);

                    // Se já estiver aberto na listagem, foca na janela
                    if (orcamentoGUI != null) {
                        orcamentoGUI.setVisible(true);
                        orcamentoGUI.toFront();
                        return;
                    }

                    // Se o orçamento estiver sendo editado como "Novo Orçamento"
                    if (OrcamentoGUI.getInstance().getIdOrcamento() != null
                            && OrcamentoGUI.getInstance().getIdOrcamento().equals(idOrcamento)) {
                        OrcamentoGUI.getInstance().setVisible(true);
                        OrcamentoGUI.getInstance().toFront();
                        return;
                    }

                    // Cria nova instância e adiciona ao mapa
                    orcamentoGUI = new OrcamentoGUI();
                    orcamentoGUI.carregarOrcamento(idOrcamento);
                    instanciasOrcamentos.put(idOrcamento, orcamentoGUI);

                    // Adiciona listener para remover do mapa ao fechar
                    orcamentoGUI.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            instanciasOrcamentos.remove(idOrcamento);
                        }
                    });

                    // Mostra e foca na instância
                    orcamentoGUI.setVisible(true);
                    orcamentoGUI.toFront();
                }
            }
        });*/
        
    }
    
    //MÉTODOS ESPECÍFICOS PARA ESTA TELA:
       
    // Método para listar os Orçamentos na tabela
    private void loadOrcamentosIntoTable() {
        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        model.setRowCount(0); // Limpar tabela antes de adicionar dados
        OrcamentoDAO orcamentoDAO = OrcamentoDAO.getInstance();

        List<Orcamento> orcamentos = orcamentoDAO.getAllOrcamentos();

        if (orcamentos != null) {
            Collections.reverse(orcamentos); // Inverte a lista para mostrar os mais novos no topo

            // Configura um formato de moeda brasileira sem espaço
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
            dfs.setCurrencySymbol("R$ "); //Espaço pode ser removido se preferir

            DecimalFormat currencyFormat = new DecimalFormat("¤#,##0.00", dfs);

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
                        : "R$0,00"; // Ou algum valor padrão

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
    
    // Método para setar datas padrão nos calendários
    private void setarDatasPadrao() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -8); // uma semana atrás
        jDateChooser1.setDate(calendar.getTime()); // data uma semana atrás
        jDateChooser2.setDate(currentDate); // data atual
    }
    
    // Método personalizado para configurar os atalhos de teclado
    private void atalhos() {
        JRootPane rootPane = this.getRootPane();

        // Mapeamento global da tecla F1 para Criar Novo Orçamento
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "NovoOrçamento");
        rootPane.getActionMap().put("NovoOrçamento", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNovoOrcamento.doClick(); // Simula o clique no botão
            }
        });

        // Mapeamento global da tecla F2 para abrir a tela de cadastros
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "Cadastros");
        rootPane.getActionMap().put("Cadastros", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCadastro.doClick(); // Simula o clique no botão
            }
        });
        
        // Mapeamento da tecla F2 para abrir cadastros se tiver dentro da jTable
        tblListagem.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("F2"), "Cadastros");
        tblListagem.getActionMap().put("Cadastros", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblListagem.getSelectedRow(); // Verifica a linha selecionada
                if (selectedRow != -1) { // Se uma linha está selecionada
                    btnCadastro.doClick(); // Simula o clique no botão de Cadastro
                } else {
                    btnCadastro.doClick(); // Simula o clique no botão de Cadastro
                }
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
                new ListagemGUI().setVisible(true);

            }
        });
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
        btnServicos = new javax.swing.JButton();

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
        lblImgLupa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblImgLupaMouseExited(evt);
            }
        });

        txtBusca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtBuscaMouseEntered(evt);
            }
        });
        txtBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscaActionPerformed(evt);
            }
        });
        txtBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaKeyReleased(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 636, Short.MAX_VALUE)
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

        btnServicos.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        btnServicos.setText("Serviços (F3)");

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
                            .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 1369, Short.MAX_VALUE)))
                    .addGroup(paneAllLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnNovoOrcamento, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(btnCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(btnServicos, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(btnRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(btnRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnServicos, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        // Obtém a instância existente da OrcamentoGUI
        OrcamentoGUI orcamentoGUI = OrcamentoGUI.getInstance();

        // Atualiza a data e hora no lblDataHora
        orcamentoGUI.atualizarDataHora();
        
        orcamentoGUI.resetCampos();

        // Abre a instância da OrcamentoGUI
        OrcamentoGUI.abrirNovaInstancia();   
    }//GEN-LAST:event_btnNovoOrcamentoActionPerformed

    private void btnCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastroActionPerformed
        
        ClienteGUI.abrirNovaInstancia(); 
        ClienteGUI.getInstance().setModoVinculacao(false);
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
            OrcamentoDAO orcamentoDAO = OrcamentoDAO.getInstance();
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

    private void txtBuscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaKeyReleased
        // Verifica se o campo de texto está completamente vazio
        if (txtBusca.getText().isEmpty()) {
            setarDatasPadrao(); // Chama o método para setar as datas padrão
            // Habilita os jDateChooser quando o campo de busca está vazio
            jDateChooser1.setEnabled(true);
            jDateChooser2.setEnabled(true);
        } else {
            // Altera a data inicial para 1º de janeiro de 2000
            Calendar calendar = Calendar.getInstance();
            calendar.set(2000, Calendar.JANUARY, 1); // Define para 1º de janeiro de 2000
            jDateChooser1.setDate(calendar.getTime());

            // Desabilita os jDateChooser para evitar que sejam acessados
            jDateChooser1.setEnabled(false);
            jDateChooser2.setEnabled(false);

            // Chama o método para filtrar os orçamentos
            filtrarOrcamentos(); // Certifique-se de ter o método que filtra os orçamentos
        }
    }//GEN-LAST:event_txtBuscaKeyReleased

    private void lblImgLupaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImgLupaMouseExited
        lblImgLupa.setToolTipText("Clique para allTimeSearch!");
    }//GEN-LAST:event_lblImgLupaMouseExited

    private void txtBuscaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBuscaMouseEntered
        txtBusca.setToolTipText("Digite a tecla de espaço para exibir todos os Orçamentos.");
    }//GEN-LAST:event_txtBuscaMouseEntered

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
    private javax.swing.JButton btnServicos;
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
