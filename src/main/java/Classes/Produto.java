package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "produtos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_produto")
public abstract class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco_produto", nullable = false)
    private double precoProduto;

    protected Produto() {
        // Construtor vazio para JPA
    }

    public Produto(String descricao, double precoProduto) {
        this.descricao = descricao;
        this.precoProduto = precoProduto;
    }

    // Getters e Setters
    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(double precoProduto) {
        if (precoProduto >= 0) {
            this.precoProduto = precoProduto;
        } else {
            throw new IllegalArgumentException("Preço do produto não pode ser negativo.");
        }
    }

    @Override
    public String toString() {
        return "Produto [idProduto=" + idProduto + ", descricao=" + descricao + ", precoProduto=" + precoProduto + "]";
    }
}
