package Registradores;

public class Registrador {

    protected String valor; //conterá 32 bits

    public Registrador() {
        this.valor = "00000000000000000000000000000000";
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return this.valor;
    }

    public long getValorLong() {
       return Long.parseLong(valor, 2);
    }

}
