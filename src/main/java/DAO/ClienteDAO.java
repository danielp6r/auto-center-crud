package DAO;

import Classes.Cliente;
import Classes.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ClienteDAO {

    // Salva um novo cliente no banco de dados
    public void salvarCliente(String nome, String telefone, String email, String cpfOuCnpj, boolean isPessoaFisica, Session session) {
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

    // Atualiza um cliente existente no banco de dados
    public void atualizarCliente(long idCliente, String nome, String telefone, String email, String cpfOuCnpj, boolean isPessoaFisica, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            String comandoSqlCliente = "UPDATE clientes SET nome_cliente = :nome, telefone = :telefone, email = :email";
            if (isPessoaFisica) {
                comandoSqlCliente += ", cpf = :cpf WHERE id_cliente = :idCliente";
            } else {
                comandoSqlCliente += ", cnpj = :cnpj WHERE id_cliente = :idCliente";
            }

            var query = session.createNativeQuery(comandoSqlCliente);
            query.setParameter("nome", nome);
            query.setParameter("telefone", telefone);
            query.setParameter("email", email);
            query.setParameter("idCliente", idCliente);

            if (isPessoaFisica) {
                query.setParameter("cpf", cpfOuCnpj);
            } else {
                query.setParameter("cnpj", cpfOuCnpj);
            }

            query.executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Erro ao atualizar cliente: " + ex.getMessage());
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

    // Retorna um cliente pelo ID usando uma sessão fornecida
    public Cliente getClienteById(Long idCliente, Session session) {
        try {
            return session.get(Cliente.class, idCliente);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

// Retorna um cliente pelo ID criando uma nova sessão automaticamente
    public Cliente getClienteById(Long idCliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return getClienteById(idCliente, session); // Reutiliza o método com a sessão fornecida
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Verifica se o cliente está vinculado a algum orçamento
    public boolean clienteTemOrcamento(long idCliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Consulta para contar quantos orçamentos estão associados ao cliente
            Query query = session.createQuery("SELECT COUNT(o) FROM Orcamento o WHERE o.cliente.id = :idCliente");
            query.setParameter("idCliente", idCliente);
            Long count = (Long) query.getSingleResult();
            return count > 0; // Retorna true se houver orçamentos vinculados
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Em caso de erro, considera que não tem orçamentos
        }
    }
}
