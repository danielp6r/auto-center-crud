    import Classes.*;
import jakarta.persistence.*;

public class TestarItem {

    public static void main(String[] args) {

        ItemOrcamento i = new ItemOrcamento();
        
        //i.setOrcamento(orcamento);
        

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("meuPU");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.persist(i);
        tx.commit();

        Cliente a = em.createQuery("from Cliente", Cliente.class)
                .getSingleResult();
        System.out.println(a);

        em.close();
    }
}
