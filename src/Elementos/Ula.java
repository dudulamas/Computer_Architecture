package Elementos;

import Barramentos.Barramento;

public class Ula {

    private final Barramento barramentoA; //registrador de retenção H liga-se a entrada A;
    private final Barramento barramentoB;  //barramento B passa o seu valor por aqui;
    private final Barramento barramentoI;
    private String N;
    private String Z;

    public Ula(Barramento barramentoA, Barramento barramentoB, Barramento barramentoI) {
        this.barramentoA = barramentoA;
        this.barramentoB = barramentoB;
        this.barramentoI = barramentoI;
    }

    public void executar(String instrucoes) {
        System.out.println("\n\n| U L A |: Barramento A = " + barramentoA.getValor() + "\t- Barramento B = " + barramentoB.getValor() + "\t- CONTROLE ULA = " + instrucoes);
        System.out.println("  **SLL8: " + instrucoes.substring(0, 1) + " SRA: " + instrucoes.substring(1, 2)
                + " F0: " + instrucoes.substring(2, 3) + " F1: " + instrucoes.substring(3, 4)
                + " ENA: " + instrucoes.substring(4, 5) + " ENB: " + instrucoes.substring(5, 6)
                + " INVA: " + instrucoes.substring(6, 7) + " INC: " + instrucoes.substring(7, 8));

        //EXECUTANDO E ESCOLHENDO A FUNÇÃO DA ULA À EXECUTAR:
        funcaoULA(instrucoes.substring(2, 3), instrucoes.substring(3, 4), instrucoes.substring(4, 5), instrucoes.substring(5, 6),
                instrucoes.substring(6, 7), instrucoes.substring(7, 8));
        //respectivamente: f0, f1, ena, enb, inva e inc;

        //APÓS FEITO ESSA OPERAÇÃO, VAMOS PASSAR OS DADOS DO BARRAMENTO I PARA O DESLOCADOR:
    }

    private void funcaoULA(String f0, String f1, String ena, String enb, String inva, String inc) {
        // Teste boolean F0 = true;boolean F1 = true;boolean ENA = false;boolean ENB = false;boolean INVA = true;boolean INC = false;

        //TRANSFORMANDO 0 E 1 (STRING) EM TRUE OU FALSE (BOOLEAN):	
        boolean F0 = valor(f0);
        boolean F1 = valor(f1);
        boolean ENA = valor(ena);
        boolean ENB = valor(enb);
        boolean INVA = valor(inva);
        boolean INC = valor(inc);

        // Teste System.out.println(F0 + " " + F1 + " " + ENA + " " + ENB + " " + INVA + " " + INC);
        System.out.print("  **FUNCAO SELECIONADA NA ULA: ");

        //TABELA COM AS RESPECTIVAS FUNÇÕES DA ULA:
        if (!F0 && F1 && ENA && !ENB && !INVA && !INC) {
            System.out.println("A");
            barramentoI.setValor(barramentoA.getValor()); // ** Jogando no barramento I o valor de A;
        } else if (!F0 && F1 && !ENA && ENB && !INVA && !INC) {
            System.out.println("B");
            barramentoI.setValor(barramentoB.getValor()); // ** Jogando no barramento I o valor de B;
        } else if (!F0 && F1 && ENA && !ENB && INVA && !INC) {
            System.out.println("!A ou A barrado");
            barramentoI.setValor(barrado(barramentoA.getValor()));	// ** Jogando o valor barrado do barramento A;
        } else if (F0 && !F1 && ENA && ENB && !INVA && !INC) {
            System.out.println("!B ou B barrado");
            barramentoI.setValor(barrado(barramentoB.getValor())); 	// ** Jogando o valor barrado do barramento B;
        } else if (F0 && F1 && ENA && ENB && !INVA && !INC) {
            System.out.println("A+B");
            barramentoI.setValor(operador(0, barramentoA.getValor(), barramentoB.getValor())); // ** operador0 = SOMAR OS DOIS BARRAMENTOS
        } else if (F0 && F1 && ENA && ENB && !INVA && INC) {
            System.out.println("A+B+1");
            barramentoI.setValor(operador(1, barramentoA.getValor(), barramentoB.getValor())); // **operador1 = SOMAR OS DOIS BARRAMENTOS + 1;
        } else if (F0 && F1 && ENA && !ENB && !INVA && INC) {
            System.out.println("A+1");
            barramentoI.setValor(operador(2, barramentoA.getValor(), barramentoB.getValor()));// ** operador2 = A+1;
        } else if (F0 && F1 && !ENA && ENB && !INVA && INC) {
            System.out.println("B+1");
            barramentoI.setValor(operador(3, barramentoA.getValor(), barramentoB.getValor()));// **operador3 = B+1;
        } else if (F0 && F1 && ENA && ENB && INVA && INC) {
            System.out.println("B-A");
            barramentoI.setValor(operador(4, barramentoA.getValor(), barramentoB.getValor()));// **operador4 = B-A;
        } else if (F0 && F1 && !ENA && ENB && INVA && !INC) {
            System.out.println("B-1");
            barramentoI.setValor(operador(5, barramentoA.getValor(), barramentoB.getValor()));// **operador 5 = B-1;
        } else if (F0 && F1 && ENA && !ENB && INVA && INC) {
            System.out.println("-A");
            barramentoI.setValor(operador(6, barramentoA.getValor(), barramentoB.getValor()));// **operador 6 =  -A;
        } else if (!F0 && !F1 && ENA && ENB && !INVA && !INC) {
            System.out.println("A AND B");
            barramentoI.setValor(operador(7, barramentoA.getValor(), barramentoB.getValor()));// **operador 7 = A & B;
        } else if (!F0 && F1 && ENA && ENB && !INVA && !INC) {
            System.out.println("A OR B");
            barramentoI.setValor(operador(8, barramentoA.getValor(), barramentoB.getValor())); 	// **operador 8 = A or B;
        } else if (!F0 && F1 && !ENA && !ENB && !INVA && !INC) {
            System.out.println("0");
            barramentoI.setValor(0); //Setando valor 0 no barramento intermediário;
        } else if (F0 && F1 && !ENA && !ENB && !INVA && INC) {
            System.out.println("1");
            barramentoI.setValor(1); //setando valor 1 no barramento intermediário;
        } else if (F0 && F1 && !ENA && !ENB && INVA && !INC) {
            System.out.println("-1");
            barramentoI.setValor(-1); //setando valor -1 no barramento intermediário;
        }

        //DEFININDO SAIDAS N E Z:
        if (barramentoI.getValor().equals("00000000000000000000000000000000")) {
            this.Z = "1";
            this.N = "0";
        } else {
            this.Z = "0";
            this.N = "1";
        }

    }

    // Função cujo objetivo é receber "0" ou "1" e retornar true ou false.
    private boolean valor(String valor) {
        if (valor.equals("0")) 
            return false;
        
        return true;
    }

    // Função cujo objetivo é receber uma cadeia "0011" e retornar "1100".
    private String barrado(String valor) {
        String barrado = "";

        for (int i = 0; i < valor.length(); i++) {
            if (valor.charAt(i) == '0') {
                barrado = barrado + 1;
            } else {
                barrado = barrado + 0;
            }
        }

        return barrado;

    }

    // Função que faz a operação matemática propriamente dita pela função da ULA. Todas as funções da ULA que fazem operações,
    // seja soma subtração ou -1, deverá obrigatoriamente passar por aqui.
    private String operador(int operacao, String a, String b) {
        long A, B;
        A = Long.parseLong(a, 2); //Fazendo a conversão dos bits em sua representação Long;
        B = Long.parseLong(b, 2); //Fazendo a conversão dos bits em sua representação Long;

        String resultado = "";

        if (operacao == 0) {
            resultado = Long.toBinaryString(A + B);
        } else if (operacao == 1) {
            resultado = Long.toBinaryString(A + B + 1);
        } else if (operacao == 2) {
            resultado = Long.toBinaryString(A + 1);
        } else if (operacao == 3) {
            resultado = Long.toBinaryString(B + 1);
        } else if (operacao == 4) {
            resultado = Long.toBinaryString(B - A);
        } else if (operacao == 5) {
            resultado = Long.toBinaryString(B - 1);
        } else if (operacao == 6) {
            resultado = Long.toBinaryString(A * (-1));
        } else if (operacao == 7) {
            resultado = Long.toBinaryString(A & B);
        } else if (operacao == 8) {
            resultado = Long.toBinaryString(A | B);
        }

        int qtdZeros = 32 - resultado.length();

        for (int i = 0; i < qtdZeros; i++) {
            resultado = 0 + resultado;
        }

        System.out.println("  **RESULTADO = " + resultado);

        return resultado;
    }

    public Barramento getBarramentoA() {
        return barramentoA;
    }

    public Barramento getBarramentoB() {
        return barramentoB;
    }

    public Barramento getBarramentoI() {
        return barramentoI;
    }

    public String getN() {
        return N;
    }

    public String getZ() {
        return Z;
    }

}
