import DAO.ClienteDAO;
import Classes.HibernateUtil;

public class TestDelete {

    public static void main(String[] args) {
        // ID do cliente que você deseja excluir
        long idClienteParaExcluir = 8; // Substitua pelo ID desejado

        // Cria uma instância de ClienteDAO
        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            // Obtém uma nova sessão do Hibernate
            var session = HibernateUtil.getSessionFactory().openSession();

            // Exclui o cliente pelo ID utilizando o método da classe ClienteDAO
            clienteDAO.excluirClientePorId(idClienteParaExcluir, session);

            // Fecha a sessão do Hibernate
            session.close();

            System.out.println("Cliente com ID " + idClienteParaExcluir + " excluído com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao excluir o cliente com ID " + idClienteParaExcluir + ": " + e.getMessage());
        }
    }
}
