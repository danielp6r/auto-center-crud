package Classes;

import Telas.ListagemGUI;

/**
 *
 * @author danielp6r
 */
public class Main {

    public static void main(String[] args) {
        
        // Inicializa o SessionManager e abre uma sessão
        SessionManager sessionManager = SessionManager.getInstance();;
        
        sessionManager.getSession(); // Abre a sessão
        
        
        // Adiciona um hook para fechar a sessão e o SessionFactory ao final da aplicação
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sessionManager.closeSession();
            sessionManager.shutdown();
        }));
        
        // Cria a instância da tela ClienteGUI
        ListagemGUI listagemGUI = new ListagemGUI();
        
        // Torna a tela visível
        listagemGUI.setVisible(true);
    }

}
