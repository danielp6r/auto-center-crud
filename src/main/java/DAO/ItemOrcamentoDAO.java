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
    
    public void inserirItemOrcamento(long produtoId, String valorUnitario, String quantidade, long idOrcamento) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            long itemOrcamentoId = findNextId(session);
            String comandoSqlOrcamentoItem = "insert into itens_orcamento(id_item_orcamento, preco_un, quantidade, id_orcamento, id_produto) values ("
                    + itemOrcamentoId + "," + valorUnitario + " , " + quantidade + " , " + idOrcamento + " , " + produtoId + ")";
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
    
    public void setarPrecoProduto(Session session){
            Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createNativeQuery("update produtos set preco_produto = 0").executeUpdate();
            transaction.commit();
            transaction = session.beginTransaction();
            session.createNativeQuery("update itens_orcamento set subtotal = 0").executeUpdate();
            transaction.commit();
        } catch(Exception e){
                throw new IllegalArgumentException("miau");
            }
}
    public long findNextId(Session session) {
        Query query = session.createQuery("select coalesce(max(idProduto), 0) from Produto", Long.class);
        Object maxId = query.getSingleResult();
        return (Long) maxId + 1;
    }
    
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
            throw e; // manda o erro de volta para quem chamou o método 
        }
        
    }
    
    public List<ItemOrcamento> getAllItens(long idOrcamentoGlobal) {
        
        List<ItemOrcamento> itemOrcamentos = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {            
            setarPrecoProduto(session);
            return session.createQuery("from ItemOrcamento where orcamento.id = :idOrcamento", ItemOrcamento.class)
                                                                .setParameter("idOrcamento", idOrcamentoGlobal)
                                                                .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   
    }
     
}