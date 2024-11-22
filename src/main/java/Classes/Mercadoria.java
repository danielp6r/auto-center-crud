package Classes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Mercadoria")
public class Mercadoria extends Produto {

    protected Mercadoria() {
        // Construtor vazio para JPA
    }

    public Mercadoria(String descricao, double precoProduto) {
        super(descricao, precoProduto);
    }

    @Override
    public String toString() {
        return "Mercadoria [" + super.toString() + "]";
    }
}
