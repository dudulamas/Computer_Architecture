package Registradores;

public class Mbr extends Registrador {

    public Mbr() {
        this.valor = "00000000"; //no caso, MBR terá que armazenar somente um byte ou 8 bits.
    }

    public void setValor(byte valor) {
        int qtdZeros = 8 - Integer.toBinaryString(valor).length();

        this.valor = Integer.toBinaryString(valor);

        for (int i = 0; i < qtdZeros; i++) 
            this.valor = 0 + this.valor;
        
    }

    public String getValor32Bits() {
        int qtdZeros = 32 - this.valor.length();
        
        String retorno = this.valor;

        for (int i = 0; i < qtdZeros; i++) 
            retorno = 0 + retorno;
        
        return retorno;
    }

    public String getValor32BitsExtensaoSinal() {
        String bitSinal = this.valor.substring(0, 1); // pegando o bit mais significativo (trocar por um char)
        int qtdBits = 32 - this.valor.length();

        String retorno = this.valor;

        for (int i = 0; i < qtdBits; i++) 
            retorno = bitSinal + retorno;
        

        return retorno;
    }
}
