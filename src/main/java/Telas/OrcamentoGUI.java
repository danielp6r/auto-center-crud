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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.util.JRLoader;
import javax.swing.SwingUtilities;


/**
 *
 * @author daniel
 */
public class OrcamentoGUI extends javax.swing.JFrame {
    // Adiciona um campo estático para armazenar a instância única
    private static OrcamentoGUI instance;
    
    // Declaração da variável na classe OrcamentoGUI
    public Orcamento orcamentoAtual;
    
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
        
        jMenuServico.setVisible(false);
        jMenuCadastros.setVisible(false);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicializa Maximizado
        
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
        
        //btnProduto.setVisible(false);
        //btnServico.setVisible(false);
        btnServicos.setVisible(false);
        //btnImprimir.setVisible(false);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                instance = null; // Reseta a instância ao fechar
            }
        });
        
        // Configs do campo de observações
        jTextObs.setLineWrap(true);
        jTextObs.setWrapStyleWord(true);
        paneObs.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        paneObs.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        // Listener para monitorar alterações no campo de observações
        jTextObs.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) { // Verifica se há um orçamento válido carregado
                    atualizarObservacoes(idOrcamentoGlobal, jTextObs.getText().trim());
                }
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) { // Verifica se há um orçamento válido carregado
                    atualizarObservacoes(idOrcamentoGlobal, jTextObs.getText().trim());
                }
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) { // Verifica se há um orçamento válido carregado
                    atualizarObservacoes(idOrcamentoGlobal, jTextObs.getText().trim());
                }
            }
        });
        
        /*// Listener para monitorar mudanças em txtVeiculo 
        txtVeiculo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) {
                    atualizarVeiculo(idOrcamentoGlobal, txtVeiculo.getText().trim());
                }
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) {
                    atualizarVeiculo(idOrcamentoGlobal, txtVeiculo.getText().trim());
                }
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) {
                    atualizarVeiculo(idOrcamentoGlobal, txtVeiculo.getText().trim());
                }
            }
        });

        // Listener para monitorar mudanças em txtPlaca
        txtPlaca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) {
                    atualizarPlaca(idOrcamentoGlobal, txtPlaca.getText().trim());
                }
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) {
                    atualizarPlaca(idOrcamentoGlobal, txtPlaca.getText().trim());
                }
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                if (idOrcamentoGlobal > 0) {
                    atualizarPlaca(idOrcamentoGlobal, txtPlaca.getText().trim());
                }
            }
        });*/
        
        //WindowListener para redefinir a instância ao fechar:
               
    }
    
    //MÉTODOS ESPECÍFICOS PARA ESTA TELA

    // Método para carregar orçamento existente
    public void carregarOrcamento(Long idOrcamento) {
        try {
            OrcamentoDAO orcamentoDAO = OrcamentoDAO.getInstance();
            Orcamento orcamento = orcamentoDAO.findById(idOrcamento);

            if (orcamento != null) {
                this.orcamentoAtual = orcamento; // Atualiza o orçamento atual
                this.idOrcamentoGlobal = idOrcamento; // Define o ID global do orçamento

                // Atualiza os campos da interface
                lblHead.setText("ORÇAMENTO Nº: " + idOrcamento);
                txtCliente.setText(orcamento.getCliente() != null ? orcamento.getCliente().getNomeCliente() : "");
                idClienteSelecionado = orcamento.getCliente() != null ? orcamento.getCliente().getIdCliente() : null;
                clienteSelecionado = orcamento.getCliente() != null;

                txtVeiculo.setText(orcamento.getCarro() != null ? orcamento.getCarro() : "");
                txtPlaca.setText(orcamento.getPlaca() != null ? orcamento.getPlaca() : "");

                if (orcamento.getDataHora() != null) {
                    String dataHoraFormatada = orcamento.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    lblDataHora.setText(dataHoraFormatada);
                } else {
                    lblDataHora.setText("");
                }

                // Atualiza o campo de observações
                jTextObs.setText(orcamento.getObservacoes() != null ? orcamento.getObservacoes() : "");

                atualizarGridItens(); // Atualiza a lista de itens
                calcularTotais(orcamento.getItensOrcamento()); // Atualiza os totais
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

        // Mapeamento da tecla F2 para inserir peça (dentro da JTable)
        tblListagem.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("F2"), "inserirPeça");
        tblListagem.getActionMap().put("inserirPeça", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("F2 pressionado - Inserir Peça (dentro da JTable)"); // Para depuração
                btnProduto.doClick(); // Simula o clique no botão inserir Peça
            }
        });

        // Mapeamento global da tecla F5 para atualizar a tela (reset)
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "reset");
        rootPane.getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Recupera o ID do orçamento atualmente carregado
                Long idAtual = getIdOrcamento(); // Método existente que retorna o ID do orçamento carregado

                // Fecha a instância atual
                dispose();
                instance = null; // Nulifica a instância para permitir recriação no singleton

                if (idAtual != null) {
                    // Reabre a tela carregando o mesmo orçamento pelo ID
                    OrcamentoGUI orcamentoGUI = OrcamentoGUI.getInstance();
                    orcamentoGUI.carregarOrcamento(idAtual); // Método existente para carregar dados do orçamento
                    orcamentoGUI.setVisible(true);
                } else {
                    // Reabre uma nova tela de orçamento em branco
                    OrcamentoGUI.getInstance().setVisible(true);
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
    public void resetCampos() {
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

    // Atualiza cliente no orçamento e na interface gráfica
    public void setClienteSelecionado(Long idCliente, String nomeCliente) {
        if (idCliente == null || idCliente <= 0) {
            JOptionPane.showMessageDialog(this, "ID do cliente inválido. Não foi possível vincular o cliente.");
            return;
        }

        this.idClienteSelecionado = idCliente; // Define o ID do cliente selecionado
        txtCliente.setText(nomeCliente); // Atualiza a interface
        clienteSelecionado = true; // Marca o cliente como selecionado

        if (orcamentoAtual != null) {
            Session session = SessionManager.getInstance().getSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                // Busca o cliente no banco
                Cliente cliente = session.find(Cliente.class, idCliente);
                if (cliente != null) {
                    // Atualiza o cliente no objeto em memória
                    orcamentoAtual.setCliente(cliente);

                    // Atualiza o orçamento no banco
                    session.update(orcamentoAtual);
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente não encontrado no banco de dados.");
                }

                transaction.commit(); // Confirma as alterações
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback(); // Reverte a transação em caso de erro
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente no banco: " + e.getMessage());
            } finally {
                session.close(); // Fecha a sessão
            }
        } 
    }

    // Vincula cliente ao orçamento no banco
    private void vincularClienteAoOrcamento(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            JOptionPane.showMessageDialog(this, "ID do cliente inválido. Não foi possível vincular ao orçamento.");
            return;
        }

        if (idOrcamentoGlobal > 0) { // Verifica se o orçamento existe
            Session session = SessionManager.getInstance().getSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                // Busca o orçamento no banco e atualiza o cliente
                Orcamento orcamento = session.find(Orcamento.class, idOrcamentoGlobal);
                if (orcamento != null) {
                    Cliente cliente = session.find(Cliente.class, idCliente);
                    if (cliente != null) {
                        orcamento.setCliente(cliente); // Atualiza o cliente
                        session.update(orcamento); // Salva a alteração no banco
                    } else {
                        JOptionPane.showMessageDialog(this, "Cliente não encontrado!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Orçamento não encontrado!");
                    return;
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao vincular cliente: " + e.getMessage());
            } finally {
                session.close();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Orçamento ainda não foi criado.");
        }
    }

    // Reseta o cliente selecionado e limpa os campos
    public void resetarCliente() {
        clienteSelecionado = false; // Reseta o controle para criação de cliente
        idClienteSelecionado = null; // Remove o ID do cliente selecionado
        txtCliente.setText(""); // Limpa o campo de texto

        // Verifica se o orçamento atual está definido e remove o cliente associado
        if (orcamentoAtual != null) {
            orcamentoAtual.setCliente(null); // Remove o cliente do orçamento em memória

            // Opcional: Salvar mudança no banco de dados
            Session session = SessionManager.getInstance().getSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.update(orcamentoAtual); // Atualiza o estado no banco de dados
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao resetar cliente no orçamento: " + e.getMessage());
            } finally {
                session.close();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nenhum orçamento carregado para resetar cliente.");
        }
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
    
    private void atualizarObservacoes(Long idOrcamento, String observacoes) {
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            String comandoSql = "UPDATE orcamentos SET observacoes = :observacoes WHERE id_orcamento_ = :idOrcamento";
            session.createNativeQuery(comandoSql)
                    .setParameter("observacoes", observacoes)
                    .setParameter("idOrcamento", idOrcamento)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar observações: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            session.close();
        }
    }
    
    private void atualizarVeiculo(Long idOrcamento, String veiculo) {
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            String comandoSql = "UPDATE orcamentos SET carro = :veiculo WHERE id_orcamento_ = :idOrcamento";
            session.createNativeQuery(comandoSql)
                    .setParameter("veiculo", veiculo)
                    .setParameter("idOrcamento", idOrcamento)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar veículo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            session.close();
        }
    }

    private void atualizarPlaca(Long idOrcamento, String placa) {
        Session session = SessionManager.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        try {
            String comandoSql = "UPDATE orcamentos SET placa = :placa WHERE id_orcamento_ = :idOrcamento";
            session.createNativeQuery(comandoSql)
                    .setParameter("placa", placa)
                    .setParameter("idOrcamento", idOrcamento)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar placa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            session.close();
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

        PaneAll = new javax.swing.JPanel();
        lblHead = new javax.swing.JLabel();
        paneBotoes = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnProduto = new javax.swing.JButton();
        btnServico = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnServicos = new javax.swing.JButton();
        paneListagem = new javax.swing.JScrollPane();
        tblListagem = new javax.swing.JTable();
        lblCliente = new javax.swing.JLabel();
        lblVeiculo = new javax.swing.JLabel();
        lblPlaca = new javax.swing.JLabel();
        lblDataHora = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        txtVeiculo = new javax.swing.JTextField();
        txtPlaca = new javax.swing.JTextField();
        paneBot = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        paneValores = new javax.swing.JPanel();
        lblTotalPecas = new javax.swing.JLabel();
        lblTotalServicos = new javax.swing.JLabel();
        lblValorFinal = new javax.swing.JLabel();
        txtTotalServicos = new javax.swing.JTextField();
        txtTotalPecas = new javax.swing.JTextField();
        txtValorFinal = new javax.swing.JTextField();
        paneObs = new javax.swing.JScrollPane();
        jTextObs = new javax.swing.JTextArea();
        btnImprimir = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuInserir = new javax.swing.JMenu();
        jMenuCliente = new javax.swing.JMenuItem();
        jMenuNovaPeca = new javax.swing.JMenuItem();
        jMenuServico = new javax.swing.JMenuItem();
        jMenuNovoServico = new javax.swing.JMenuItem();
        jMenuEditar = new javax.swing.JMenu();
        jMenuExcluirItem = new javax.swing.JMenuItem();
        jMenuJanela = new javax.swing.JMenu();
        jMenuAtualizar = new javax.swing.JMenuItem();
        jMenuCadastros = new javax.swing.JMenu();
        jMenuCadastroServicos = new javax.swing.JMenuItem();
        jMenuImprime = new javax.swing.JMenu();
        jMenuImprimir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Orçamentos");
        setMaximumSize(new java.awt.Dimension(1360, 718));
        setMinimumSize(new java.awt.Dimension(1360, 718));
        setSize(new java.awt.Dimension(1360, 718));

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

        btnProduto.setText("Nova Peça");
        btnProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProdutoActionPerformed(evt);
            }
        });

        btnServico.setText("Novo Serviço");
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

        btnServicos.setText("Serviço Cad.");

        javax.swing.GroupLayout paneBotoesLayout = new javax.swing.GroupLayout(paneBotoes);
        paneBotoes.setLayout(paneBotoesLayout);
        paneBotoesLayout.setHorizontalGroup(
            paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnServico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnServicos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 672, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166)
                .addComponent(btnSalvar)
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
                        .addGroup(paneBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnProduto)
                            .addComponent(btnServicos))
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

        lblCliente.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblCliente.setText("Cliente");

        lblVeiculo.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblVeiculo.setText("Veículo");

        lblPlaca.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblPlaca.setText("Placa");

        lblDataHora.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        lblDataHora.setText("     Data - Hora");

        txtCliente.setEditable(false);
        txtCliente.setName("txtCliente"); // NOI18N
        txtCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtClienteMouseClicked(evt);
            }
        });
        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });
        txtCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtClienteKeyPressed(evt);
            }
        });

        txtVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVeiculoActionPerformed(evt);
            }
        });
        txtVeiculo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtVeiculoKeyTyped(evt);
            }
        });

        txtPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPlacaKeyTyped(evt);
            }
        });

        jLabel1.setText("Obs.:");

        javax.swing.GroupLayout paneBotLayout = new javax.swing.GroupLayout(paneBot);
        paneBot.setLayout(paneBotLayout);
        paneBotLayout.setHorizontalGroup(
            paneBotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBotLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );
        paneBotLayout.setVerticalGroup(
            paneBotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneBotLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalPecas)
                    .addComponent(lblTotalServicos)
                    .addComponent(lblValorFinal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalServicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalPecas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValorFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTextObs.setColumns(20);
        jTextObs.setRows(5);
        paneObs.setViewportView(jTextObs);

        btnImprimir.setText("Imprimir");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PaneAllLayout = new javax.swing.GroupLayout(PaneAll);
        PaneAll.setLayout(PaneAllLayout);
        PaneAllLayout.setHorizontalGroup(
            PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneListagem, javax.swing.GroupLayout.DEFAULT_SIZE, 1360, Short.MAX_VALUE)
            .addGroup(PaneAllLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paneBotoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PaneAllLayout.createSequentialGroup()
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
                        .addGap(179, 179, 179)
                        .addComponent(lblDataHora, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimir)
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PaneAllLayout.createSequentialGroup()
                        .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(paneValores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(paneBot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblHead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PaneAllLayout.setVerticalGroup(
            PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaneAllLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHead)
                .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PaneAllLayout.createSequentialGroup()
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
                            .addGroup(PaneAllLayout.createSequentialGroup()
                                .addComponent(lblPlaca)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblDataHora, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PaneAllLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(btnImprimir)))
                .addGap(13, 13, 13)
                .addComponent(paneBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneListagem, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PaneAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PaneAllLayout.createSequentialGroup()
                        .addComponent(paneValores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneBot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(paneObs, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jMenuInserir.setText("Inserir");
        jMenuInserir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuInserirActionPerformed(evt);
            }
        });

        jMenuCliente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuCliente.setText("Vincular Cliente");
        jMenuCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuClienteActionPerformed(evt);
            }
        });
        jMenuInserir.add(jMenuCliente);

        jMenuNovaPeca.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuNovaPeca.setText("Inserir Nova Peça");
        jMenuNovaPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNovaPecaActionPerformed(evt);
            }
        });
        jMenuInserir.add(jMenuNovaPeca);

        jMenuServico.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuServico.setText("Inserir Serviço Cadastrado");
        jMenuServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuServicoActionPerformed(evt);
            }
        });
        jMenuInserir.add(jMenuServico);

        jMenuNovoServico.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuNovoServico.setText("Inserir Novo Serviço");
        jMenuNovoServico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNovoServicoActionPerformed(evt);
            }
        });
        jMenuInserir.add(jMenuNovoServico);

        jMenuBar.add(jMenuInserir);

        jMenuEditar.setText("Editar");

        jMenuExcluirItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMenuExcluirItem.setText("Excluir Item");
        jMenuExcluirItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExcluirItemActionPerformed(evt);
            }
        });
        jMenuEditar.add(jMenuExcluirItem);

        jMenuBar.add(jMenuEditar);

        jMenuJanela.setText("Janela");
        jMenuJanela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuJanelaActionPerformed(evt);
            }
        });

        jMenuAtualizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuAtualizar.setText("Atualizar");
        jMenuAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAtualizarActionPerformed(evt);
            }
        });
        jMenuJanela.add(jMenuAtualizar);

        jMenuBar.add(jMenuJanela);

        jMenuCadastros.setText("Cadastros");

        jMenuCadastroServicos.setText("Serviços Cadastrados");
        jMenuCadastroServicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCadastroServicosActionPerformed(evt);
            }
        });
        jMenuCadastros.add(jMenuCadastroServicos);

        jMenuBar.add(jMenuCadastros);

        jMenuImprime.setText("Imprimir");

        jMenuImprimir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        jMenuImprimir.setText("Imprimir Orçamento");
        jMenuImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuImprimirActionPerformed(evt);
            }
        });
        jMenuImprime.add(jMenuImprimir);

        jMenuBar.add(jMenuImprime);

        setJMenuBar(jMenuBar);

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

    private void txtTotalPecasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalPecasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalPecasActionPerformed

    
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // Verifica se os campos Cliente e Veículo foram preenchidos
        if (txtCliente.getText().equals("") || txtVeiculo.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Os campos Cliente e Veículo devem ser preenchidos!");
        } else {
            // Captura o conteúdo do campo Placa
            String placa = txtPlaca.getText().trim().toUpperCase();

            // Verifica se a placa está no formato correto
            if (!placa.isEmpty() && !placa.matches("^[A-Z]{3}[0-9]{4}$") && !placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
                JOptionPane.showMessageDialog(this, "A placa deve estar no formato ABC1234 ou ABC1D23.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe o salvamento se a placa estiver no formato incorreto
            }

            // Exibe um aviso se a placa não for preenchida
            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Atenção: O campo Placa está vazio!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Continua com o salvamento do orçamento
            if (idOrcamentoGlobal <= 0) {
                long idOrcamento = SalvarOrcamento(false); // Cria o orçamento se ele não existir
                if (idOrcamento > 0) {
                    atualizarVeiculo(idOrcamento, txtVeiculo.getText().trim());
                    atualizarPlaca(idOrcamento, placa);
                    atualizarObservacoes(idOrcamento, jTextObs.getText().trim());
                }
            } else {
                vincularClienteAoOrcamento(idClienteSelecionado); // Atualiza o cliente no orçamento existente
                atualizarVeiculo(idOrcamentoGlobal, txtVeiculo.getText().trim());
                atualizarPlaca(idOrcamentoGlobal, placa);
                atualizarObservacoes(idOrcamentoGlobal, jTextObs.getText().trim());
            }

            JOptionPane.showMessageDialog(this, "Orçamento salvo com sucesso!");
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    // Variável global para guardar a id do Orçamento;
    public long idOrcamentoGlobal;

    private long SalvarOrcamento(Boolean exibirMensagem) {
        // Obtém as informações digitadas na tela
        String nome = txtCliente.getText().trim();
        String veiculo = txtVeiculo.getText().trim();
        String placa = txtPlaca.getText().trim();
        String observacoes = jTextObs.getText().trim();

        // Validação dos campos obrigatórios
        if (nome.isEmpty() || veiculo.isEmpty() /*|| placa.isEmpty()*/) {
            JOptionPane.showMessageDialog(this, "Os campos Cliente e Veículo devem ser preenchidos!");
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

            String comandoSqlOrcamento = "INSERT INTO orcamentos (id_cliente, id_orcamento_, placa, carro, data_hora, observacoes) VALUES ("
                    + idCliente + ", " + idOrcamento + ", '" + placa + "', '" + veiculo + "', current_timestamp, '" + observacoes + "')";
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
        // Validação dos campos obrigatórios
        if (txtCliente.getText().isEmpty() || txtVeiculo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Os campos Cliente e Veículo devem ser preenchidos!");
            return;
        }

        // Validação do campo Placa
        String placa = txtPlaca.getText().trim().toUpperCase();
        if (!placa.isEmpty() && !placa.matches("^[A-Z]{3}[0-9]{4}$") && !placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            JOptionPane.showMessageDialog(this, "A placa deve estar no formato ABC1234 ou ABC1D23.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
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

                // Formatar os valores para moeda brasileira (somente na exibição)
                //Espaço pode ser removido se preferir
                String precoUnFormatado = String.format("R$ %.2f", item.getPrecoUn());
                String subtotalFormatado = String.format("R$ %.2f", item.getPrecoUn() * item.getQuantidade());

                // Adicionar linha à tabela
                Object[] row = {
                    item.getIdItemOrcamento(),
                    descricao != null ? descricao : "N/A",
                    precoUnFormatado, // Valor unitário formatado
                    item.getQuantidade(),
                    subtotalFormatado // Subtotal formatado
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
        // Validação dos campos obrigatórios
        if (txtCliente.getText().isEmpty() || txtVeiculo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Os campos Cliente e Veículo devem ser preenchidos!");
            return;
        }

        // Validação do campo Placa
        String placa = txtPlaca.getText().trim().toUpperCase();
        if (!placa.isEmpty() && !placa.matches("^[A-Z]{3}[0-9]{4}$") && !placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            JOptionPane.showMessageDialog(this, "A placa deve estar no formato ABC1234 ou ABC1D23.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
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
        // Verifica se os campos Cliente e Veículo foram preenchidos
        if (txtCliente.getText().equals("") || txtVeiculo.getText().equals("") /*|| txtPlaca.getText().equals("")*/) {
            JOptionPane.showMessageDialog(this, "Os campos Cliente e Veículo devem ser preenchidos!");
        } else {
            // Verifica se há itens na tabela
            DefaultTableModel model = (DefaultTableModel) tblListagem.getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Adicione itens para imprimir o orçamento!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Exibe um aviso se a placa não for preenchida
            if (txtPlaca.getText().equals("")) {
                int resposta = JOptionPane.showConfirmDialog(
                        this,
                        "O campo Placa está vazio! Deseja continuar?",
                        "Aviso",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (resposta == JOptionPane.NO_OPTION) {
                    return; // Interrompe o processo caso o usuário escolha "Não"
                }
            }

            imprimirRelatorioJasper();
        }
    }//GEN-LAST:event_btnImprimirActionPerformed
    //URL reportPath = Main.class.getResource("/reports/orcamento.jasper");
    private void imprimirRelatorioJasper() {
        String reportPath = getClass().getResource("/reports/orcamento.jasper").getPath();
        String imagePath = getClass().getResource("/images/vr.png").getPath();

        try {
            JasperReport reporte = (JasperReport) JRLoader.loadObject(new File(reportPath));
            Map<String, Object> parametros = new HashMap<>();
            Integer idOrcamentoInteger = (int) idOrcamentoGlobal;
            parametros.put("idOrcamento", idOrcamentoInteger);

            // Passe o caminho da imagem como parâmetro
            parametros.put("IMAGE_PATH", imagePath);

            String url = "jdbc:mysql://localhost:3306/test";
            String usuario = "root";
            String senha = "12345";
            Connection conexao = DriverManager.getConnection(url, usuario, senha);

            // Preenche o relatório
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexao);

            // Gera o PDF no diretório de saída padrão do Maven
            String pdfFilePath = "target/orcamento.pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);

            // Abre o PDF no leitor padrão
            abrirPDF(new File(pdfFilePath));

        } catch (JRException | SQLException ex) {
            Logger.getLogger(OrcamentoGUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirPDF(File pdfFile) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (pdfFile.exists()) {
                    desktop.open(pdfFile); // Abre o PDF no aplicativo padrão do sistema
                } else {
                    JOptionPane.showMessageDialog(this, "O arquivo PDF não foi encontrado: " + pdfFile.getPath(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Abrir arquivos PDF não é suportado neste sistema.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            Logger.getLogger(OrcamentoGUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Erro ao abrir o PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void txtClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtClienteMouseClicked
        ClienteGUI clienteGUI = ClienteGUI.getInstance(); // Garante instância única
        clienteGUI.setModoVinculacao(true);
        clienteGUI.setVisible(true); // Exibe ClienteGUI
    }//GEN-LAST:event_txtClienteMouseClicked

    private void txtClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClienteKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteKeyPressed

    private void txtPlacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPlacaKeyTyped
        String textoAtual = txtPlaca.getText().toUpperCase(); // Obtém o texto atual e converte para maiúsculas
        char caractere = evt.getKeyChar(); // Obtém o caractere digitado

        // Permitir teclas de controle como backspace, delete e setas
        if (Character.isISOControl(caractere)) {
            return;
        }

        // Permitir apenas letras e números
        if (!Character.isLetterOrDigit(caractere)) {
            evt.consume(); // Ignora caracteres especiais e pontuações
            return;
        }

        // Limitar o comprimento para 7 caracteres
        if (textoAtual.length() >= 7) {
            evt.consume(); // Ignora o caractere adicional
            return;
        }

        // Converte o caractere para maiúsculas automaticamente
        caractere = Character.toUpperCase(caractere);

        // Validação por posição
        int posicao = textoAtual.length();
        if (posicao < 3) {
            // Primeiras 3 posições devem ser letras
            if (!Character.isLetter(caractere)) {
                evt.consume(); // Ignora caracteres inválidos
                return;
            }
        } else if (posicao == 3 || posicao == 5 || posicao == 6) {
            // Posições 3, 5 e 6 devem ser números
            if (!Character.isDigit(caractere)) {
                evt.consume(); // Ignora caracteres inválidos
                return;
            }
        } else if (posicao == 4) {
            // Posição 4 pode ser letra ou número (para ambos os formatos)
            if (!Character.isLetter(caractere) && !Character.isDigit(caractere)) {
                evt.consume(); // Ignora caracteres inválidos
                return;
            }
        }

        // Atualiza o texto no campo com o caractere validado
        txtPlaca.setText(textoAtual + caractere);
        evt.consume(); // Evita que o caractere seja processado novamente
    }//GEN-LAST:event_txtPlacaKeyTyped

    private void txtVeiculoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVeiculoKeyTyped
        // Verifica se o texto atual ultrapassará 24 caracteres
        if (txtVeiculo.getText().length() >= 24) {
            evt.consume(); // Impede que o caractere digitado seja adicionado
            return;
        }

        SwingUtilities.invokeLater(() -> {
            String textoAtual = txtVeiculo.getText();
            if (!textoAtual.isEmpty()) {
                // Apenas a primeira letra da primeira palavra em maiúscula
                String primeiraLetra = textoAtual.substring(0, 1).toUpperCase();
                String restanteTexto = textoAtual.substring(1); // Resto do texto permanece inalterado
                txtVeiculo.setText(primeiraLetra + restanteTexto);
            }
        });
    }//GEN-LAST:event_txtVeiculoKeyTyped

    private void jMenuNovaPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNovaPecaActionPerformed
        btnProduto.doClick();
    }//GEN-LAST:event_jMenuNovaPecaActionPerformed

    private void jMenuNovoServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNovoServicoActionPerformed
        btnServico.doClick();
    }//GEN-LAST:event_jMenuNovoServicoActionPerformed

    private void jMenuServicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuServicoActionPerformed
        btnServicos.doClick();
    }//GEN-LAST:event_jMenuServicoActionPerformed

    private void jMenuExcluirItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExcluirItemActionPerformed
        btnExcluir.doClick();
    }//GEN-LAST:event_jMenuExcluirItemActionPerformed

    private void jMenuImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuImprimirActionPerformed
        btnImprimir.doClick();
    }//GEN-LAST:event_jMenuImprimirActionPerformed

    private void jMenuCadastroServicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCadastroServicosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuCadastroServicosActionPerformed

    private void jMenuInserirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuInserirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuInserirActionPerformed

    private void jMenuAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAtualizarActionPerformed
        // Simula o pressionamento da tecla F5
        ActionMap actionMap = rootPane.getActionMap();
        Action action = actionMap.get("reset");
        if (action != null) {
            action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
    }//GEN-LAST:event_jMenuAtualizarActionPerformed

    private void jMenuJanelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuJanelaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuJanelaActionPerformed

    private void jMenuClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuClienteActionPerformed
        ClienteGUI clienteGUI = ClienteGUI.getInstance(); // Garante instância única
        clienteGUI.setModoVinculacao(true);
        clienteGUI.setVisible(true); // Exibe ClienteGUI
    }//GEN-LAST:event_jMenuClienteActionPerformed

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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnProduto;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnServico;
    private javax.swing.JButton btnServicos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuAtualizar;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuCadastroServicos;
    private javax.swing.JMenu jMenuCadastros;
    private javax.swing.JMenuItem jMenuCliente;
    private javax.swing.JMenu jMenuEditar;
    private javax.swing.JMenuItem jMenuExcluirItem;
    private javax.swing.JMenu jMenuImprime;
    private javax.swing.JMenuItem jMenuImprimir;
    private javax.swing.JMenu jMenuInserir;
    private javax.swing.JMenu jMenuJanela;
    private javax.swing.JMenuItem jMenuNovaPeca;
    private javax.swing.JMenuItem jMenuNovoServico;
    private javax.swing.JMenuItem jMenuServico;
    private javax.swing.JTextArea jTextObs;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblDataHora;
    private javax.swing.JLabel lblHead;
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