import Classes.*;
import jakarta.persistence.*;

public class TestarPJ {

    public static void main(String[] args) {

        PessoaJuridica pj = new PessoaJuridica("Empresa X");
        pj.setCnpj("1234560001");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("meuPU");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.persist(pj);
        tx.commit();

        Cliente a = em.createQuery("from Cliente", Cliente.class)
                .getSingleResult();
        System.out.println(a);

        em.close();
    }
}
