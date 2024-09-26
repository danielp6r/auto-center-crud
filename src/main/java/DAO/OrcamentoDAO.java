package DAO;

import Classes.Orcamento;
import Classes.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class OrcamentoDAO extends GenericDAO<Orcamento, Long> {

    private SessionFactory sessionFactory;

    public OrcamentoDAO() {
       /* try {
            this.sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }*/
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
        Query query = session.createQuery("select max(idOrcamento) from Orcamento", Long.class);
        Long maxId = (Long) query.getSingleResult();

        // Se maxId for nulo, retorna 1; caso contrário, retorna maxId + 1
        return (maxId != null) ? maxId + 1 : 1;
    }
    
    public List<Orcamento> getAllOrcamentos() {
        List<Orcamento> orcamentos = null;
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
}