package Registradores;

public class Mpc {

    private String valor;

    public Mpc() {
        this.valor = "000000000"; // 9 bits para definir a proxima instrucao
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setValor(long l) {
        int qtdZeros = 8 - Long.toBinaryString(l).length();

        String retorno = Long.toBinaryString(l);

        for (int i = 0; i < qtdZeros; i++) 
            retorno = "0" + retorno;
        
        System.out.println(" valor temporario = " + retorno);
        this.valor = retorno;

    }

    public String getValor() {
        return this.valor;
    }

    public int getValorInt() {
        return Integer.parseInt(this.valor, 2);
    }

    public long getValorLong() {
        return Long.parseLong(this.valor, 2);
    }

}
