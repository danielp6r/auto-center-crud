package Classes;

/* A SESSION MANAGER VAI ARMAZENAR A SESSÃO CORRENTE DO SISTEMA, ASSIM AS OUTRAS
CLASSES SÓ PRECISAM CHAMAR getSession() PARA PEGAR A SESSÃO. 
Serve para manter a conexão com o BD durante o uso do sistema, assim não é necessário
abrir uma nova sessão toda vez que uma consulta será realizada.

*/
/**
 *
 * @author danielp6r
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionManager {
    private static SessionManager instance;
    private SessionFactory sessionFactory;
    private Session session;

    private SessionManager() {
        
        //cria o banco novo
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("meuPU");
        //EntityManager em = emf.createEntityManager();
        
        try 
        {
            // Configura a SessionFactory a partir do hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
            
            session = sessionFactory.openSession();
        } catch(Exception e) { 
            e.printStackTrace();
        
        }
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public Session getSession() {
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
        }
        return session;
    }

    public void closeSession() {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}