package DAO;

import Classes.Cliente;
import Classes.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ClienteDAO {

    // Salva ou atualiza um cliente no banco de dados
    public void saveOrUpdateCliente(String nome, String telefone, String email, String cpfOuCnpj, boolean isPessoaFisica, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            long idCliente = findNextId(session); // Obtém o próximo ID

            String tipoCliente = isPessoaFisica ? "PessoaFisica" : "PessoaJuridica";
            String comandoSqlCliente = "INSERT INTO clientes (id_cliente, tipo_cliente, nome_cliente, telefone, email";
            String values = " VALUES (" + idCliente + ", '" + tipoCliente + "', '" + nome + "', '" + telefone + "', '" + email + "'";

            if (isPessoaFisica && !cpfOuCnpj.isEmpty()) {
                comandoSqlCliente += ", cpf)";
                values += ", '" + cpfOuCnpj + "')";
            } else if (!isPessoaFisica && !cpfOuCnpj.isEmpty()) {
                comandoSqlCliente += ", cnpj)";
                values += ", '" + cpfOuCnpj + "')";
            } else {
                comandoSqlCliente += ")";
                values += ")";
            }

            String comandoSqlFinal = comandoSqlCliente + values;
            session.createNativeQuery(comandoSqlFinal).executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Erro ao salvar cliente: " + ex.getMessage());
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
