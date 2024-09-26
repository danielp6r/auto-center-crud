package DAO;

import Classes.HibernateUtil;
import Classes.PessoaJuridica;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PessoaJuridicaDAO {

    public void save(PessoaJuridica pessoaJuridica) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(pessoaJuridica);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Outros métodos específicos de PessoaJuridica podem ser adicionados aqui
}