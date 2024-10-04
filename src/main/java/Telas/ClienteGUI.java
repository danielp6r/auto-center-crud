package Telas;

import Classes.Cliente;
import Classes.PessoaFisica;
import Classes.PessoaJuridica;
import Classes.SessionManager;
import DAO.ClienteDAO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author danielp6r
 */
public class ClienteGUI extends javax.swing.JFrame {
    // Campo estático para armazenar a instância única
    private static ClienteGUI instance;

    /**
     * Creates new form ClienteGUI
     */
    public ClienteGUI() {
        initComponents();
        carregarClientes();
        
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

    }
    
    /*
    MÉTODOS ESPECÍFICOS PARA ESSA TELA
    */
    
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
    }
    
    // Método para adicionar um WindowListener para limpar os campos ao fechar a janela
    private void addWindowClosingListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                limparCampos();
            }
        });
    }
    
    // Método para limpar os campos de texto
    private void limparCampos() {
        txtNome.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        txtCPFouCNPJ.setText("");
    }
    
    // Método para listar os clientes em ordem alfabética, ignorando diferenças de maiúsculas/minúsculas
    private void carregarClientes() {
        ClienteDAO clienteDAO = new ClienteDAO();
        List<Cliente> clientes = clienteDAO.getAllClientes();

        // Ordena a lista de clientes em ordem alfabética pelo nome, ignorando maiúsculas/minúsculas
        Collections.sort(clientes, new Comparator<Cliente>() {
            @Override
            public int compare(Cliente c1, Cliente c2) {
                // Utilize compareToIgnoreCase para ignorar diferenças de maiúsculas/minúsculas
                return c1.getNomeCliente().compareToIgnoreCase(c2.getNomeCliente());
            }
        });

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
                        cliente.getId() 
                    };
                } else if (cliente instanceof PessoaJuridica) {
                    PessoaJuridica pessoaJuridica = (PessoaJuridica) cliente;
                    rowData = new Object[]{
                        cliente.getNomeCliente(),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        pessoaJuridica.getCnpj(),
                        cliente.getId()
                    };
                } else {
                    // Se não for nem Pessoa Física nem Pessoa Jurídica, adiciona uma mensagem genérica
                    rowData = new Object[]{
                        cliente.getNomeCliente(),
                        cliente.getTelefone(),
                        cliente.getEmail(),
                        "CPF/CNPJ não disponível",
                        cliente.getId() 
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
        txtTel = new javax.swing.JFormattedTextField();
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

        txtTel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0.###"))));
        txtTel.setText("");

        lblEmail.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(0, 0, 0));
        lblEmail.setText("e-mail:");

        lblImgCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/cliente.png"))); // NOI18N

        paneOpcoes.setBackground(new java.awt.Color(255, 255, 255));
        paneOpcoes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnExcluir.setText("Excluir");
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

        btnEditar.setText("Editar");
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
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
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

        tblListagem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Telefone", "email", "CPF/CNPJ", "Número"
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
                        .addComponent(lblHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(lblImgCliente)
                        .addGap(103, 103, 103)
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(paneListagem))
                    .addGroup(paneTelaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(paneOpcoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paneTelaLayout.setVerticalGroup(
            paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneTelaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHead, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(lblTel)
                    .addComponent(btnPJ)
                    .addComponent(btnPF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneTelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneTelaLayout.createSequentialGroup()
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
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addComponent(paneOpcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneListagem, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblImgCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        String nome = txtNome.getText().trim(); // Nome é obrigatório, trim() remove espaços extras

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
            if (cliente.getNomeCliente().equalsIgnoreCase(nome)) {
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
        Cliente novoCliente;
        if (btnPF.isSelected()) {
            novoCliente = new PessoaFisica(nome);
            if (!cpfOuCnpj.isEmpty()) {
                ((PessoaFisica) novoCliente).setCpf(cpfOuCnpj); // Define CPF se não estiver vazio
            }
        } else {
            novoCliente = new PessoaJuridica(nome);
            if (!cpfOuCnpj.isEmpty()) {
                ((PessoaJuridica) novoCliente).setCnpj(cpfOuCnpj); // Define CNPJ se não estiver vazio
            }
        }

        // Configura os dados opcionais do cliente
        novoCliente.setTelefone(telefone);
        novoCliente.setEmail(email);

        // Salva o cliente no banco de dados
        Transaction transaction = session.beginTransaction();
        try {
            long idCliente = clienteDAO.findNextId(session);

            String tipoCliente = (novoCliente instanceof PessoaFisica) ? "PessoaFisica" : "PessoaJuridica";
            String comandoSqlCliente = "insert into clientes(id_cliente, tipo_cliente, nome_cliente, telefone, email";
            String values = " values (" + idCliente + ", '" + tipoCliente + "', '" + nome + "', '" + telefone + "', '" + email + "'";

            if (novoCliente instanceof PessoaFisica && !cpfOuCnpj.isEmpty()) {
                comandoSqlCliente += ", cpf)";
                values += ", '" + cpfOuCnpj + "')";
            } else if (novoCliente instanceof PessoaJuridica && !cpfOuCnpj.isEmpty()) {
                comandoSqlCliente += ", cnpj)";
                values += ", '" + cpfOuCnpj + "')";
            } else {
                comandoSqlCliente += ")";
                values += ")";
            }

            String comandoSqlFinal = comandoSqlCliente + values;
            session.createNativeQuery(comandoSqlFinal).executeUpdate();

            transaction.commit();
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");

            // Após salvar o cliente com sucesso, atualiza a lista de clientes na tabela
            carregarClientes();

        } catch (Exception ex) {
            //se der erro, dá um rollback na transação
            transaction.rollback();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage());
        } finally {
            session.close(); // Fechar a sessão após o uso
            limparCampos(); // Limpar os campos de texto
            //this.dispose(); // Fecha a tela após salvar o cliente
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void txtBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscaActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        int selectedRow = tblListagem.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long idCliente = (long) tblListagem.getValueAt(selectedRow, 4); // 4 é o índice da coluna ID na tabela

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este cliente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            Session session = SessionManager.getInstance().getSession();
            ClienteDAO clienteDAO = new ClienteDAO();
            try {
                clienteDAO.excluirClientePorId(idCliente, session);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                carregarClientes(); // Atualiza a lista de clientes na tabela após a exclusão
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                session.close(); // Fecha a sessão
            }
        }
    }//GEN-LAST:event_btnExcluirActionPerformed
    
    //Aceita somente entrada de números
    private void txtCPFouCNPJKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCPFouCNPJKeyTyped
        char c = evt.getKeyChar();
        
        if (!Character.isDigit(c)){
            evt.consume();
        }
    }//GEN-LAST:event_txtCPFouCNPJKeyTyped
    
    //Não aceita entrada de números
    private void txtNomeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeKeyTyped
        char c = evt.getKeyChar();
        
        if (Character.isDigit(c)){
            evt.consume();
        }
    }//GEN-LAST:event_txtNomeKeyTyped

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        // Obtém a linha e a coluna selecionadas
        int selectedRow = tblListagem.getSelectedRow();
        int selectedColumn = tblListagem.getSelectedColumn();

        if (selectedRow == -1 || selectedColumn == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma célula para editar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obter o nome da coluna que está sendo editada
        String columnName = tblListagem.getColumnName(selectedColumn);

        // Obter o valor atual da célula
        Object valorAtual = tblListagem.getValueAt(selectedRow, selectedColumn);

        // Exibir uma caixa de diálogo para confirmar a edição
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja editar o valor da coluna " + columnName + "?", "Confirmar Edição", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            // O usuário confirmou a edição
            // Exibe um input dialog para o usuário digitar o novo valor
            Object novoValor = JOptionPane.showInputDialog(this, "Digite o novo valor para " + columnName + ":", valorAtual);

            // Verifica se o novo valor não está vazio
            if (novoValor != null && !novoValor.toString().trim().isEmpty()) {
                // Atualizar a célula da tabela
                tblListagem.setValueAt(novoValor, selectedRow, selectedColumn);

                // Atualizar o banco de dados
                long idCliente = (long) tblListagem.getValueAt(selectedRow, 4); // Supondo que a coluna 4 é o ID do cliente
                Session session = SessionManager.getInstance().getSession();
                ClienteDAO clienteDAO = new ClienteDAO();

                try {
                    session.beginTransaction();

                    // Atualizar o cliente no banco de dados
                    clienteDAO.atualizarCliente(idCliente, columnName, novoValor.toString(), session);

                    session.getTransaction().commit();
                    JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
                } catch (Exception ex) {
                    session.getTransaction().rollback();
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                    session.close();
                }
            }
        }
    }//GEN-LAST:event_btnEditarActionPerformed

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
    private javax.swing.JFormattedTextField txtTel;
    // End of variables declaration//GEN-END:variables
}