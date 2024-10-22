package Telas;

import Classes.Cliente;
import Classes.PessoaFisica;
import Classes.PessoaJuridica;
import Classes.SessionManager;
import DAO.ClienteDAO;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author danielp6r
 */
public class ClienteGUI extends javax.swing.JFrame {
    
    // Variável para saber se está no modo de edição ou criação de cliente
    private boolean isEditing = false;
    
    // Campo estático para armazenar a instância única
    private static ClienteGUI instance;

    /**
     * Creates new form ClienteGUI
     */
    public ClienteGUI() {
        initComponents();
        carregarClientes();
        atalhos();
        padrao();
        
        // Ajusta para fechar apenas a janela atual
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        
        // Adicionando um WindowListener para limpar os campos ao fechar a janela
        addWindowClosingListener();
        
        // Agrupa os botões de Pessoa Física e Pessoa Jurídica
        ButtonGroup tipoClienteGroup = new ButtonGroup();
        tipoClienteGroup.add(btnPF);
        tipoClienteGroup.add(btnPJ);

        // Define Pessoa Física como selecionado por padrão
        btnPF.setSelected(true);
        lblCPFouCNPJ.setText("CPF");

        btnPF.addActionListener(e -> lblCPFouCNPJ.setText("CPF"));
        btnPJ.addActionListener(e -> lblCPFouCNPJ.setText("CNPJ"));
        
        // Adiciona o MouseListener à lupa
        addLupaClickListener();
        
        // Adiciona o DocumentListener para filtrar os clientes
        addSearchListener();
        
        // Ocultando a coluna num
        TableColumnModel columnModel = tblListagem.getColumnModel();
        TableColumn column = columnModel.getColumn(4); // 4 é o índice da coluna
        // Definindo a largura mínima, máxima e preferida da coluna como 0
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setWidth(0);
        
        // Impedindo que o usuário reorganize os índices das colunas
        tblListagem.getTableHeader().setReorderingAllowed(false);
        
        // Listeners para Labels de Criação e nome do Cliente
        txtNome.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Ignora o evento se a tecla não for letra ou número
                if (!Character.isLetterOrDigit(e.getKeyChar())) {
                    return; // Ignora teclas que não são letras ou números
                }

                if (clienteIdEdicao == null) { // Verifica se estamos criando um cliente
                    lblCliente.setText(txtNome.getText()); // Atualiza o nome enquanto digita
                    lblCriandoOuEditando.setText("Criando Novo Cliente"); // Atualiza para "Criando Novo Cliente"
                }
            }
        });
        
        // Atualiza ao ativar a janela
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                carregarClientes();
                }
        });
             
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
        
        // Mapeamento da tecla F2 para Editar dentro da JTable
        tblListagem.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("F2"), "editarAction");
        tblListagem.getActionMap().put("editarAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("F2 pressionado - Editar na JTable"); // Para depuração
                int selectedRow = tblListagem.getSelectedRow(); // Verifica a linha selecionada
                if (selectedRow != -1) { // Se uma linha está selecionada
                    btnEditar.doClick(); // Simula o clique no botão Editar
                } else {
                    JOptionPane.showMessageDialog(ClienteGUI.this, "Selecione um cliente para editar.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Mapeamento da tecla F2 para Editar fora da JTable
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "editarAction");
        rootPane.getActionMap().put("editarAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("F2 pressionado - Editar fora da JTable"); // Para depuração
                btnEditar.doClick(); // Simula o clique no botão Editar
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
                new ClienteGUI().setVisible(true);
                
            }
        });
        
        // Mapeamento global da tecla esc para fechar a tela
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "dispose");
        rootPane.getActionMap().put("dispose", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                padrao();
            }
        });
    }
    
    // Redefine os padrões da tela
    public void padrao() {
        btnPF.setEnabled(true); // Reativa o botão Pessoa Física
        btnPJ.setEnabled(true); // Reativa o botão Pessoa Jurídica
        btnPF.setSelected(true);  // Seleciona Pessoa Física
        btnPJ.setSelected(false); // Desmarca Pessoa Jurídica
        lblCPFouCNPJ.setText("CPF");
        tblListagem.clearSelection();
        txtNome.requestFocusInWindow();
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
    
    // Método para obter a instância única da tela
    public static ClienteGUI getInstance() {
        if (instance == null) {
            instance = new ClienteGUI();
        }
        return instance;
    }
    
    // Método para abrir a tela de novo Cliente
    public static void abrirNovaInstancia() {
        if (instance == null) {
            instance = new ClienteGUI();
            // Define a janela como sempre no topo
            //instance.setAlwaysOnTop(true);
        }
        instance.setVisible(true);
        instance.requestFocus(); // Garante que a janela receba o foco
        
        instance.padrao(); // Seta para padrão toda vez que abre a tela
    }
    
    // Limpas os campos e seta pada padrão ao fechar a janela
    private void addWindowClosingListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                limparCampos();
                isEditing = false; // Resetar o estado de edição ao fechar a janela
                
                padrao();
            }
        });
    }
    
    // Método para limpar os campos de texto
    private void limparCampos() {
        txtNome.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        txtCPFouCNPJ.setText("");
        lblCliente.setText(""); // Limpa o label de nome do cliente
        lblCriandoOuEditando.setText(""); // Limpa o label de estado
        clienteIdEdicao = null; // Reseta o ID do cliente em edição
    }
    
    // Método ordenar os clientes (true: id decrescente , false: alfabetico)
    private void ordenarClientes(List<Cliente> clientes, boolean porID) {
        if (porID == true) {
            // Ordena a lista de clientes em ordem decrescente pelo ID
            Collections.sort(clientes, (c1, c2) -> Long.compare(c2.getIdCliente(), c1.getIdCliente()));
        } else {
            // Ordena a lista de clientes em ordem alfabética pelo nome, ignorando maiúsculas/minúsculas
            Collections.sort(clientes, (c1, c2) -> c1.getNomeCliente().compareToIgnoreCase(c2.getNomeCliente()));
        }
    }
    
    // Método para listar os clientes 
    private void carregarClientes() {
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> clientes = clienteDAO.getAllClientes();

        ordenarClientes(clientes, true); // true por id decrescente , false para alfabetico

        DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
        model.setRowCount(0); // Limpa a tabela antes de preencher com os novos dados

        if (clientes != null) {
            for (Cliente cliente : clientes) {
                Object[] rowData;
                // Verifica se o cliente é Pessoa Física ou Pessoa Jurídica
                if (cliente instanceof PessoaFisica) {
                    PessoaFisica pessoaFisica = (PessoaFisica) cliente;
                    rowData = new Object[]{
                        cliente.getNomeCliente(),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        pessoaFisica.getCpf(),
                        cliente.getIdCliente() 
                    };
                } else if (cliente instanceof PessoaJuridica) {
                    PessoaJuridica pessoaJuridica = (PessoaJuridica) cliente;
                    rowData = new Object[]{
                        cliente.getNomeCliente(),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        pessoaJuridica.getCnpj(),
                        cliente.getIdCliente()
                    };
                } else {
                    // Se não for nem Pessoa Física nem Pessoa Jurídica, adiciona uma mensagem genérica
                    rowData = new Object[]{
                        cliente.getNomeCliente(),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        "CPF/CNPJ não disponível",
                        cliente.getIdCliente() 
                    };
                }
                // Adiciona os dados do cliente à tabela
                model.addRow(rowData);
            }
        }
        // Define um filtro inicial vazio na tabela
        txtBusca.setText("");
        filterClients();
    }

    
    // Método para adicionar um DocumentListener ao campo de busca
    private void addSearchListener() {
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterClients();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterClients();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterClients();
            }
        });
    }

    // Método para filtrar os clientes de acordo com o texto digitado no campo de busca
    private void filterClients() {
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneTela = new javax.swing.JPanel();
        lblHead = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        lblTel = new javax.swing.JLabel();
        txtTel = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblImgCliente = new javax.swing.JLabel();
        paneOpcoes = new javax.swing.JPanel();
        btnSalvar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        txtBusca = new javax.swing.JTextField();
        lblImgLupa = new javax.swing.JLabel();
        btnEditar = new javax.swing.JButton();
        btnPF = new javax.swing.JRadioButton();
        btnPJ = new javax.swing.JRadioButton();
        lblCPFouCNPJ = new javax.swing.JLabel();
        txtCPFouCNPJ = new javax.swing.JTextField();
        lblCliente = new javax.swing.JLabel();
        lblCriandoOuEditando = new javax.swing.JLabel();
        paneListagem = new javax.swing.JScrollPane();
        tblListagem = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Clientes");
        setResizable(false);

        paneTela.setBackground(new java.awt.Color(255, 255, 255));

        lblHead.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblHead.setForeground(new java.awt.Color(0, 0, 0));
        lblHead.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHead.setText("Cadastro de Clientes");
        lblHead.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNome.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblNome.setForeground(new java.awt.Color(0, 0, 0));
        lblNome.setText("Nome");

        txtNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomeKeyTyped(evt);
            }
        });

        lblTel.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblTel.setForeground(new java.awt.Color(0, 0, 0));
        lblTel.setText("Telefone");

        txtTel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelKeyTyped(evt);
            }
        });

        lblEmail.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(0, 0, 0));
        lblEmail.setText("e-mail:");

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEmailKeyTyped(evt);
            }
        });

        lblImgCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/cliente.png"))); // NOI18N

        paneOpcoes.setBackground(new java.awt.Color(255, 255, 255));
        paneOpcoes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSalvar.setForeground(new java.awt.Color(76, 175, 80));
        btnSalvar.setText("Salvar (F1)");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnExcluir.setForeground(new java.awt.Color(244, 67, 54));
        btnExcluir.setText("Excluir (Del)");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        txtBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscaActionPerformed(evt);
            }
        });

        lblImgLupa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/pesquisar.png"))); // NOI18N
        lblImgLupa.setText(" ");

        btnEditar.setForeground(new java.awt.Color(90, 155, 213));
        btnEditar.setText("Editar (F2)");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneOpcoesLayout = new javax.swing.GroupLayout(paneOpcoes);
        paneOpcoes.setLayout(paneOpcoesLayout);
        paneOpcoesLayout.setHorizontalGroup(
            paneOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneOpcoesLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(lblImgLupa, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        paneOpcoesLayout.setVerticalGroup(
            paneOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneOpcoesLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(paneOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSalvar)
                        .addComponent(btnEditar)
                        .addComponent(btnExcluir))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneOpcoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblImgLupa)))
                .addContainerGap())
        );

        btnPF.setText("P.F");

        btnPJ.setText("P.J");

        lblCPFouCNPJ.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblCPFouCNPJ.setForeground(new java.awt.Color(0, 0, 0));
        lblCPFouCNPJ.setText("CPF/CNPJ");

        txtCPFouCNPJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCPFouCNPJKeyTyped(evt);
            }
        });

        lblCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        lblCriandoOuEditando.setForeground(new java.awt.Color(153, 153, 153));
        lblCriandoOuEditando.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        tblListagem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Telefone", "e-mail", "CPF/CNPJ", "Num"
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

        javax.swing.GroupLayout paneTelaLayout = new javax.swing.GroupLayout(paneTela);
        paneTela.setLayout(paneTelaLayout);
        paneTelaLayout.setHorizontalGroup(
            paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneTelaLayout.createSequentialGroup()
                .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(paneListagem)
                            .addComponent(paneOpcoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(paneTelaLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(lblImgCliente))
                            .addGroup(paneTelaLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(lblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(paneTelaLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(lblCriandoOuEditando, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(29, 29, 29)
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtNome, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCPFouCNPJ))
                            .addGroup(paneTelaLayout.createSequentialGroup()
                                .addComponent(lblNome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPJ)
                                .addGap(6, 6, 6))
                            .addComponent(txtCPFouCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                            .addComponent(lblEmail)
                            .addComponent(lblTel)
                            .addComponent(txtTel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paneTelaLayout.setVerticalGroup(
            paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneTelaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHead, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNome)
                            .addComponent(lblTel)
                            .addComponent(btnPJ)
                            .addComponent(btnPF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                            .addComponent(txtTel))
                        .addGap(18, 18, 18)
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCPFouCNPJ)
                            .addComponent(lblEmail))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCPFouCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addComponent(lblCriandoOuEditando, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImgCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(paneOpcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneListagem, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneTela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneTela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // Captura os dados da GUI
        String nome = txtNome.getText().trim();

        // Verifica se o campo Nome não está vazio
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verifica se já existe um cliente com o mesmo nome
        Session session = SessionManager.getInstance().getSession();
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> clientes = clienteDAO.getAllClientes();
        for (Cliente cliente : clientes) {
            if (cliente.getNomeCliente().equalsIgnoreCase(nome) && cliente.getIdCliente() != clienteIdEdicao) { // Alterado de idClienteEditando para clienteIdEdicao
                JOptionPane.showMessageDialog(this, "Já existe um cliente cadastrado com o mesmo nome.", "Erro", JOptionPane.ERROR_MESSAGE);
                session.close(); // Fecha a sessão
                return;
            }
        }

        // Continua com o salvamento do cliente
        String telefone = txtTel.getText().trim();
        String email = txtEmail.getText().trim();
        String cpfOuCnpj = txtCPFouCNPJ.getText().trim();

        // Verifica se é Pessoa Física ou Pessoa Jurídica
        boolean isPessoaFisica = btnPF.isSelected();

        try {
            if (clienteIdEdicao != null) { // Se um cliente está sendo editado
                clienteDAO.atualizarCliente(clienteIdEdicao, nome, telefone, email, cpfOuCnpj, isPessoaFisica, session);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!"); // Mensagem de sucesso
            } else { // Caso contrário, salve um novo cliente
                clienteDAO.salvarCliente(nome, telefone, email, cpfOuCnpj, isPessoaFisica, session);
                JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
            }
            carregarClientes(); // Atualiza a lista de clientes na tabela
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            session.close(); // Fechar a sessão após o uso
            limparCampos(); // Limpar os campos de texto
            clienteIdEdicao = null; // Reseta o ID após a operação
        }
        // Limpa os labels após a ação
        lblCliente.setText(""); // Limpa o nome do cliente
        lblCriandoOuEditando.setText(""); // Limpa o texto de criando ou editando
        
        padrao();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void txtBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscaActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        int selectedRow = tblListagem.getSelectedRow();

        // Verifica se algum cliente está selecionado
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtém o ID do cliente a partir da tabela
        long idCliente = (long) tblListagem.getValueAt(selectedRow, 4); // 4 é o índice da coluna ID na tabela
        
        
        // Declaração da variável clienteDAO
        ClienteDAO clienteDAO = new ClienteDAO(); // Cria uma instância do DAO
        
        // Verifica se o cliente está vinculado a algum orçamento
        if (clienteDAO.clienteTemOrcamento(idCliente)) {
            JOptionPane.showMessageDialog(this, "Não é possível excluir o cliente! Há orçamento(s) vinculado(s).", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria a caixa de confirmação
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cliente " + tblListagem.getValueAt(selectedRow, 0) + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        // Verifica se o usuário confirmou a exclusão
        if (confirmacao == JOptionPane.YES_OPTION) {
            Session session = SessionManager.getInstance().getSession(); // Abre a sessão
            try {
                // Tenta excluir o cliente
                clienteDAO.excluirClientePorId(idCliente);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarClientes(); // Atualiza a lista de clientes na tabela após a exclusão
            } catch (Exception ex) {
                // Exibe uma mensagem de erro se a exclusão falhar
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Imprime o erro no console para depuração
            } finally {
                session.close(); // Fecha a sessão
                limparCampos(); // Limpa os campos de texto
            }
        }

        // Limpa os labels após a ação
        lblCliente.setText(""); // Limpa o nome do cliente
        lblCriandoOuEditando.setText(""); // Limpa o texto de criando ou editando

        padrao(); // Restaura o estado padrão
    }//GEN-LAST:event_btnExcluirActionPerformed
    
    //Aceita somente entrada de números
    private void txtCPFouCNPJKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCPFouCNPJKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // Impede a digitação de qualquer coisa que não seja número
        }

        int maxLength = btnPF.isSelected() ? 11 : 14; // 11 para CPF, 14 para CNPJ
        if (txtCPFouCNPJ.getText().length() >= maxLength) {
            evt.consume(); // Limita o tamanho conforme o tipo de cliente
        }
    }//GEN-LAST:event_txtCPFouCNPJKeyTyped
    
    private void txtNomeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeKeyTyped
        if (txtNome.getText().length() >= 30) {
            evt.consume(); // Limita o tamanho a 30 caracteres
        } 
    }//GEN-LAST:event_txtNomeKeyTyped

    private Long clienteIdEdicao = null;
    
    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
       
        int selectedRow = tblListagem.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            btnPF.setEnabled(false); // Desabilita o botão Pessoa Física
            btnPJ.setEnabled(false); // Desabilita o botão Pessoa Jurídica
        }

        // Pega os dados da linha selecionada
        String nome = (String) tblListagem.getValueAt(selectedRow, 0); // Nome
        String telefone = (String) tblListagem.getValueAt(selectedRow, 1); // Telefone
        String email = (String) tblListagem.getValueAt(selectedRow, 2); // Email
        String cpfOuCnpj = (String) tblListagem.getValueAt(selectedRow, 3); // CPF ou CNPJ
        Long idCliente = (Long) tblListagem.getValueAt(selectedRow, 4); // ID

        // Preenche os campos de edição com os dados do cliente
        txtNome.setText(nome);
        txtTel.setText(telefone);
        txtEmail.setText(email);
        txtCPFouCNPJ.setText(cpfOuCnpj);

        // Atualiza o label para mostrar que está editando
        lblCliente.setText(nome);
        lblCriandoOuEditando.setText("Editando Cliente"); // Atualiza o label para indicar que está editando

        // Muda o estado para edição
        isEditing = true; // Adicione esta linha

        // Verifique se o cliente é Pessoa Física ou Jurídica e selecione o radio button correspondente
        Session session = SessionManager.getInstance().getSession();
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = clienteDAO.getClienteById(idCliente);

        if (cliente instanceof PessoaFisica) {
            btnPF.setSelected(true);  // Seleciona Pessoa Física
            btnPJ.setSelected(false); // Desmarca Pessoa Jurídica
            lblCPFouCNPJ.setText("CPF");
        } else if (cliente instanceof PessoaJuridica) {
            btnPJ.setSelected(true);  // Seleciona Pessoa Jurídica
            btnPF.setSelected(false); // Desmarca Pessoa Física
            lblCPFouCNPJ.setText("CNPJ");
        }

        // Guarda o ID do cliente em edição para usá-lo no botão Salvar
        clienteIdEdicao = idCliente; // Aqui foi utilizado clienteIdEdicao para armazenar o ID

        session.close(); // Fecha a sessão
    }//GEN-LAST:event_btnEditarActionPerformed

    private void txtTelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // impede a digitação de qualquer coisa que não seja número
        }
        if (txtTel.getText().length() >= 15) {
            evt.consume(); // Limita o tamanho a 11 caracteres
        }
    }//GEN-LAST:event_txtTelKeyTyped

    private void txtEmailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isLetterOrDigit(c) && c != '@' && c != '.' && c != '-' && c != '_') {
            evt.consume(); // Impede caracteres que não são válidos em endereços de e-mail
        }
        if (txtEmail.getText().length() >= 30) {
            evt.consume(); // Limita o tamanho a 30 caracteres
        }
    }//GEN-LAST:event_txtEmailKeyTyped

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
            java.util.logging.Logger.getLogger(ClienteGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClienteGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClienteGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClienteGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClienteGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JRadioButton btnPF;
    private javax.swing.JRadioButton btnPJ;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel lblCPFouCNPJ;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblCriandoOuEditando;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblHead;
    private javax.swing.JLabel lblImgCliente;
    private javax.swing.JLabel lblImgLupa;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblTel;
    private javax.swing.JScrollPane paneListagem;
    private javax.swing.JPanel paneOpcoes;
    private javax.swing.JPanel paneTela;
    private javax.swing.JTable tblListagem;
    private javax.swing.JTextField txtBusca;
    private javax.swing.JTextField txtCPFouCNPJ;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtTel;
    // End of variables declaration//GEN-END:variables
}