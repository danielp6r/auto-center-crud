package DAO;

import Classes.Orcamento;
import Classes.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class OrcamentoDAO extends GenericDAO<Orcamento, Long> {

    public OrcamentoDAO() {
        // Construtor vazio
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
    
    public long findNextId(Session session) {
        Long maxId = session.createQuery("select max(idOrcamento) from Orcamento", Long.class).uniqueResult();
        return (maxId != null) ? maxId + 1 : 1;
    }
    
    public List<Orcamento> getAllOrcamentos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {            
            return session.createQuery("from Orcamento", Orcamento.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   
    }
    
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
