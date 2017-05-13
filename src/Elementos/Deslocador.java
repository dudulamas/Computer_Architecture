package Elementos;

import Barramentos.Barramento;

public class Deslocador {

    private final Barramento barramentoC;
    //private Barramento barramentoI;

    public Deslocador(Barramento barramentoC) {
        this.barramentoC = barramentoC;
    }

    public void executar(String valorBarramentoI, String ssl8, String sra1) {
        System.out.println("\n\n|DESLOCADOR|: Barramento I = " + valorBarramentoI + " - SSL8 = " + ssl8 + " - SRA1 = " + sra1);

        String saidaDeslocador = valorBarramentoI;

        //SHIFT LEFT LOGICAL: desloca para a esquerda em 8 bits o valor. 
        if (ssl8.equals("1") && sra1.equals("0")) {
            saidaDeslocador = saidaDeslocador.substring(8, 32);
            System.out.print("  **Pegando do bit 0 ao 23: (" + saidaDeslocador + ") ");

            saidaDeslocador = saidaDeslocador + "00000000";
            System.out.println(" -> (apos ssl8)" + saidaDeslocador);

        } //SHIFT RIGHT ARITHMETIC: desloca o conteúdo para a direita por 1 bit, deixando inalterado o bit menos significativo.
        else if (sra1.equals("1") && ssl8.equals("0")) {
            String bitMaisSignificativo = saidaDeslocador.substring(0, 1); // Pega o bit mais significativo;
            saidaDeslocador = saidaDeslocador.substring(0, 31); // Pega todos os bits de 0-30;
            System.out.print(" **Pegando do bit 0 ao 30: (" + saidaDeslocador + ") ");
            saidaDeslocador = bitMaisSignificativo + saidaDeslocador;

            System.out.println(" -> (apos SRA1)" + saidaDeslocador);

        }

        //Caso ambos sejam 11 ou 00 nao fazemos nada.
        System.out.println(" **Saida do Deslocador = " + saidaDeslocador);

        // ** Setando o valor do barramento C, que foi passado por referência:
        barramentoC.setValor(saidaDeslocador);
    }

}
