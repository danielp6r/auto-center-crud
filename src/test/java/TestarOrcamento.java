import Classes.*;
import jakarta.persistence.*;

public class TestarOrcamento {

    public static void main(String[] args) {
        criarOrcamento();
    }

    private static void criarOrcamento() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("meuPU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();

            Cliente cliente = criarCliente("Daniel", "f", em);
            Orcamento orcamento = new Orcamento(cliente);
            em.persist(orcamento);

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static Cliente criarCliente(String nome, String tipoCliente, EntityManager em) {
        Cliente cliente;
        if (tipoCliente.equalsIgnoreCase("F")) {
            cliente = new PessoaFisica(nome);
        } else {
            cliente = new PessoaJuridica(nome);
        }
        
        em.persist(cliente);
        return cliente;
    }
}