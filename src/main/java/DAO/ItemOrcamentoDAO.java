package DAO;

import Classes.HibernateUtil;
import Classes.ItemOrcamento;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ItemOrcamentoDAO extends GenericDAO<ItemOrcamento, Long> {

    private SessionFactory sessionFactory;

    public ItemOrcamentoDAO() {}

    // Método para inserir itens no orçamento
    public void inserirItemOrcamento(long produtoId, double valorUnitario, int quantidade, long idOrcamento) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            long itemOrcamentoId = findNextId(session);
            String comandoSqlOrcamentoItem = "INSERT INTO itens_orcamento(id_item_orcamento, preco_un, quantidade, id_orcamento, id_produto) VALUES ("
                    + itemOrcamentoId + ", " + valorUnitario + ", " + quantidade + ", " + idOrcamento + ", " + produtoId + ")";
            session.createNativeQuery(comandoSqlOrcamentoItem).executeUpdate();

            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            throw new IllegalArgumentException("Erro ao salvar item no orçamento: " + ex.getMessage());
        } finally {
            session.close();
        }
    }

    // Método para atualizar preços na tabela produtos
    public void atualizarPrecoProduto(long produtoId, double precoProduto) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String comandoSqlAtualizarPreco = "UPDATE produtos SET preco_produto = :precoProduto WHERE id_produto = :produtoId";
            session.createNativeQuery(comandoSqlAtualizarPreco)
                    .setParameter("precoProduto", precoProduto)
                    .setParameter("produtoId", produtoId)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao atualizar preço do produto: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    // Método para encontrar o próximo ID de produto
    public long findNextId(Session session) {
        Query query = session.createQuery("SELECT COALESCE(MAX(idProduto), 0) FROM Produto", Long.class);
        Object maxId = query.getSingleResult();
        return (Long) maxId + 1;
    }

    // Método para excluir item por ID
    public void excluirItemPorId(long idItem, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            ItemOrcamento itemOrcamento = session.get(ItemOrcamento.class, idItem);
            if (itemOrcamento != null) {
                session.delete(itemOrcamento);
                transaction.commit();
            } else {
                throw new IllegalArgumentException("Item não encontrado para o ID fornecido.");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e; // Reenvia a exceção
        }
    }

    // Método para buscar todos os itens de um orçamento
    public List<ItemOrcamento> getAllItens(long idOrcamentoGlobal) {
        List<ItemOrcamento> itemOrcamentos = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ItemOrcamento WHERE orcamento.id = :idOrcamento", ItemOrcamento.class)
                          .setParameter("idOrcamento", idOrcamentoGlobal)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
