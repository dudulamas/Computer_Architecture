package Barramentos;

public class Barramento {

    private String valor; //Conterá 32 bits.

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setValor(long valor) {
        //fazer a tradução do Long em Binario de 32 bits
        int qtdZeros = 32 - Long.toBinaryString(valor).length();

        this.valor = Long.toBinaryString(valor);

        for (int i = 0; i < qtdZeros; i++) {
            this.valor = 0 + this.valor;
        }
    }

}
