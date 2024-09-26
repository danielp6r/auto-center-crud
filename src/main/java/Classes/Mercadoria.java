package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("Mercadoria")
public class Mercadoria extends Produto{
    
    @Column(name = "cod_barras")
    private String codBarras;
    
    protected Mercadoria() {
        // Construtor vazio para JPA
    }

    public Mercadoria(String nomeProduto, Double precoProduto) {
        super(nomeProduto, precoProduto);
        this.codBarras = "";
    }

    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
        this.codBarras = codBarras;
    }
    
}