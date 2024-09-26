package DAO;

import jakarta.persistence.Query;
import org.hibernate.Session;

/**
 *
 * @author danielp6r
 */
public class ProdutoDAO {
    
    public long findNextId(Session session) {
        Query query = session.createQuery("select coalesce(max(idProduto), 0) from Produto", Long.class);
        Object maxId = query.getSingleResult();
        return (Long) maxId + 1;
    }
}
