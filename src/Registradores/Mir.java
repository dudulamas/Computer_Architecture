package Registradores;

public class Mir {

    private String valor;

    public Mir() {
        this.valor = "000000000000000000000000000000000000";
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setValor(long valor) {
        int qtdZeros = 36 - Long.toBinaryString(valor).length();

        this.valor = Long.toBinaryString(valor);

        for (int i = 0; i < qtdZeros; i++) {
            this.valor = 0 + this.valor;
        }
    }

    public String getValor() {
        return this.valor;
    }

    public long getValorLong() {
        return Long.parseLong(this.valor, 2);
    }

}
