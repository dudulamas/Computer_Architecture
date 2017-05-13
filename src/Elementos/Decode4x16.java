package Elementos;

public class Decode4x16 {

    public String decodificar(String valor) {
        /*
         * decodificar(String valor): a partir de um pedaço de 4 bits da Microinstrução, os últimos 4, 
         * decodifica qual será o Registrador que deverá ser ativado.
         */

        int i = Integer.parseInt(valor, 2); //transformando uma cadeia de 4 bits em sua representação decimal;
        System.out.print("\n|DECODIFICADOR 4x16|: entrada -> " + valor + " = " + i);

        if (i > 8) 
            return null;
        
        //String s = "0x0" + i;
        System.out.println(" -> saida = 0x0" + i);

        return "0x0" + i;

    }

}
