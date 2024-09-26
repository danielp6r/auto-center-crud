import Classes.*;
import jakarta.persistence.*;

public class TestarPF {

    public static void main(String[] args) {

        PessoaFisica c = new PessoaFisica("Joao");
        c.setCpf("123456789");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("meuPU");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.persist(c);
        tx.commit();

        Cliente a = em.createQuery("from Cliente", Cliente.class)
                .getSingleResult();
        System.out.println(a);

        em.close();
    }
}
