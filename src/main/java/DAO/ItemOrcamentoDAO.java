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

    public ItemOrcamentoDAO() {
    }

    // Método para atualizar o preço dos produtos e os subtotais dos itens de orçamento
    public void setarPrecoProduto(Session session) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createNativeQuery("update produtos set preco_produto = 0").executeUpdate();
            transaction.commit();

            transaction = session.beginTransaction();
            session.createNativeQuery("update itens_orcamento set subtotal = 0").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new IllegalArgumentException("Erro ao atualizar preços: " + e.getMessage(), e);
        }
    }

    // Método para encontrar o próximo ID do produto
    public long findNextId(Session session) {
        Query query = session.createQuery("select coalesce(max(id_item_orcamento), 0) from ItensOrcamento", Long.class);
        Object maxId = query.getSingleResult();
        return (Long) maxId + 1;
    }

    // Método para excluir um item pelo ID
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
            throw e; // Retorna o erro para o chamador do método 
        }
    }

    // Método para obter todos os itens de um orçamento
    public List<ItemOrcamento> getAllItens(long idOrcamentoGlobal) {
        List<ItemOrcamento> itemOrcamentos = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            setarPrecoProduto(session);
            itemOrcamentos = session.createQuery("from ItemOrcamento where orcamento.id = :idOrcamento", ItemOrcamento.class)
                    .setParameter("idOrcamento", idOrcamentoGlobal)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return itemOrcamentos;
    }

    // Método para salvar um item de orçamento
    public void saveOrcamentoItem(Session session, long itemOrcamentoId, String valorUnitario, String quantidade, long idOrcamento, long produtoId) {
        String comandoSqlOrcamentoItem = "insert into itens_orcamento(id_item_orcamento, preco_un, quantidade, id_orcamento, id_produto) values (" + itemOrcamentoId + "," + valorUnitario + " , " + quantidade + " , " + idOrcamento + " , " + produtoId + ")";
        session.createNativeQuery(comandoSqlOrcamentoItem).executeUpdate();
    }
}
