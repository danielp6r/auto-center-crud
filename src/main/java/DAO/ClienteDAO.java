package DAO;

import Classes.Cliente;
import Classes.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ClienteDAO {

    // Salva ou atualiza um cliente no banco de dados
    /*public void save(Cliente cliente) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            if (cliente.getIdCliente() == null) {
                // Novo cliente, então salva
                session.save(cliente);
            } else {
                // Cliente existente, então atualiza
                session.update(cliente);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }*/

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

    // Exclui um cliente pelo ID
    public void excluirClientePorId(long idCliente) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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

    // Retorna um cliente pelo ID
    public Cliente getClienteById(Long idCliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Cliente.class, idCliente);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
