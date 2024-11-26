package DAO;

import Classes.Orcamento;
import Classes.HibernateUtil;
import Classes.ItemOrcamento;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class OrcamentoDAO extends GenericDAO<Orcamento, Long> {
    private static OrcamentoDAO instance;

    // Construtor privado para Singleton
    public OrcamentoDAO() {
        // Construtor vazio
    }

    // Singleton para garantir uma única instância
    public static OrcamentoDAO getInstance() {
        if (instance == null) {
            synchronized (OrcamentoDAO.class) {
                if (instance == null) {
                    instance = new OrcamentoDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Encontra todos os orçamentos associados a um cliente específico.
     *
     * @param idCliente ID do cliente
     * @return Lista de orçamentos associados ao cliente
     */
    public List<Orcamento> findByCliente(Long idCliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Orcamento where cliente.idCliente = :idCliente", Orcamento.class)
                          .setParameter("idCliente", idCliente)
                          .getResultList();
        }
    }

    /**
     * Encontra o próximo ID disponível para um novo orçamento.
     *
     * @param session Sessão atual do Hibernate
     * @return Próximo ID disponível
     */
    public long findNextId(Session session) {
        Long maxId = session.createQuery("select max(idOrcamento) from Orcamento", Long.class).uniqueResult();
        return (maxId != null) ? maxId + 1 : 1;
    }

    /**
     * Retorna todos os orçamentos cadastrados.
     *
     * @return Lista de todos os orçamentos
     */
    public List<Orcamento> getAllOrcamentos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {            
            return session.createQuery("from Orcamento", Orcamento.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   
    }

    /**
     * Encontra um orçamento por ID, carregando seus itens associados.
     *
     * @param id ID do orçamento
     * @return Instância do orçamento com itens associados
     */
    @Override
    public Orcamento findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT o FROM Orcamento o LEFT JOIN FETCH o.itensOrcamento WHERE o.idOrcamento = :id",
                    Orcamento.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    /**
     * Encontra os itens associados a um orçamento específico.
     *
     * @param idOrcamento ID do orçamento
     * @return Lista de itens associados ao orçamento
     */
    public List<ItemOrcamento> findItensByOrcamentoId(Long idOrcamento) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ItemOrcamento io WHERE io.orcamento.idOrcamento = :idOrcamento",
                    ItemOrcamento.class)
                    .setParameter("idOrcamento", idOrcamento)
                    .list();
        }
    }

    /**
     * Exclui um orçamento pelo ID.
     *
     * @param idOrcamento ID do orçamento a ser excluído
     * @param session Sessão atual do Hibernate
     */
    public void excluirOrcamentoPorId(long idOrcamento, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Orcamento orcamento = session.get(Orcamento.class, idOrcamento);
            if (orcamento != null) {
                session.delete(orcamento);
                transaction.commit();
            } else {
                throw new IllegalArgumentException("Orçamento não encontrado para o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e; // Propaga a exceção para o método chamador
        }
    }

    /**
     * Atualiza os valores de mercadorias, serviços e total de um orçamento.
     *
     * @param idOrcamento ID do orçamento a ser atualizado
     * @param valMercadorias Valor das mercadorias
     * @param valServicos Valor dos serviços
     * @param valTotal Valor total do orçamento
     */
    public void atualizarValoresOrcamento(long idOrcamento, double valMercadorias, double valServicos, double valTotal) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            String sql = "UPDATE orcamentos SET val_mercadorias = :valMercadorias, valor_servicos = :valServicos, val_total = :valTotal WHERE id_orcamento_ = :idOrcamento";
            session.createNativeQuery(sql)
                   .setParameter("valMercadorias", valMercadorias)
                   .setParameter("valServicos", valServicos)
                   .setParameter("valTotal", valTotal)
                   .setParameter("idOrcamento", idOrcamento)
                   .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
