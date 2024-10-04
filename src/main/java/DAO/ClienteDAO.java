package DAO;

import Classes.Cliente;
import Classes.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ClienteDAO {

    // Salva um cliente no banco de dados
    public void save(Cliente cliente) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(cliente);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Encontra o próximo ID disponível para um novo cliente
    public long findNextId(Session session) {
        Query query = session.createQuery("select coalesce(max(idCliente), 0) from Cliente", Long.class);
        Object maxId = query.getSingleResult();
        return (Long) maxId + 1;
    }


    
    // Retorna todos os clientes cadastrados no banco de dados
    public List<Cliente> getAllClientes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Cliente", Cliente.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void excluirClientePorId(long idCliente, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Cliente cliente = session.get(Cliente.class, idCliente);
            if (cliente != null) {
                session.delete(cliente);
                transaction.commit();
            } else {
                throw new IllegalArgumentException("Cliente não encontrado para o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e; // Propaga a exceção para o método chamador
        }
    }
    
    public void atualizarCliente(long idCliente, String coluna, String novoValor, Session session) {
        // Cria a query de atualização no banco de dados
        String query = "UPDATE clientes SET " + coluna + " = :novoValor WHERE id_cliente = :idCliente";

        // Executa a atualização no banco de dados
        session.createNativeQuery(query)
                .setParameter("novoValor", novoValor)
                .setParameter("idCliente", idCliente)
                .executeUpdate();
    }
}
