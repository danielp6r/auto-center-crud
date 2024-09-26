package DAO;

import Classes.HibernateUtil;
import Classes.PessoaFisica;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PessoaFisicaDAO {

    public void save(PessoaFisica pessoaFisica) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(pessoaFisica);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Outros métodos específicos de PessoaFisica podem ser adicionados aqui
}