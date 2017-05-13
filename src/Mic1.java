import Barramentos.Barramento;
import Elementos.*;
import Registradores.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class Mic1 {
    private final String microprog = "C:\\Users\\Eduardo\\Documents\\Estudo Arquitetura\\Trabalho Arquitetura - NetBeans\\src\\Arquivos\\microprog.rom";
    private final String prog = "C:\\Users\\Eduardo\\Documents\\Estudo Arquitetura\\Trabalho Arquitetura - NetBeans\\src\\Arquivos\\prog.exe";
    
    // ATRIBUTOS DA MIC1:
    private long[] armazenamentoControle; //Aqui armazenaremos o microprog.rom;
    private byte[] memoriaPrincipal;  //Aqui armazenaremos o arquivo binário prog.exe (nossa RAM);

    // Ula:
    private final Ula ula;
    // Deslocador que localiza-se na saída da ULA:
    private final Deslocador deslocador;
    // Barramentos:
    private Barramento barramentoA;
    private Barramento barramentoB;
    private Barramento barramentoC;
    private Barramento barramentoI;

    // Decodificador responsável por pegar parte da microinstrução referente ao barramento B:
    private final Decode4x16 decode4x16;

    // Registradores do caminho de dados:
    private Registrador mar;
    private Registrador mdr;
    private Registrador pc;
    private Mbr mbr;
    private Registrador sp;
    private Registrador lv;
    private Registrador cpp;
    private Registrador tos;
    private Registrador opc;
    private Registrador h;

    // Registradores do Armazenamento de controle:
    private Mpc mpc; // responsável por guardar o endereço da próxima microinstrução;
    private Mir mir; // responsável por conter a microinstrução atual.

    private int contadorCiclo;

    //Inicialização da Mic:
    public Mic1(String exe) {

        // ** Inicializando o armazenamento de controle:
        armazenamentoControle = new long[512];
        readRom(); // Função que ler o arquivo microprog.rom e o coloca no atributo armazenamentoControle;

        // ** Inicializando a memória principal:
        memoriaPrincipal = new byte[62];
        readExe(exe); // Função que ler o arquivo prog.exe e o coloca no atributo memoriaPrincipal;

        // ** Inicializando os barramentos:
        barramentoA = new Barramento();
        barramentoB = new Barramento();
        barramentoC = new Barramento();
        barramentoI = new Barramento();

        // ** Inicializando a ULA e passando a referência dos barramentos. Assim, todas as modificiações nos barramentos que ocorrerem
        // 	na classe da ULA, também serão modificados aqui.
        ula = new Ula(barramentoA, barramentoB, barramentoI);

        // ** Inicializando o deslocador e passando por referência, também, o barramento C, para que ao modificá-lo na classe Deslocador,
        //  as mudanças tenham efeito aqui também.
        deslocador = new Deslocador(barramentoC);

        // ** Inicializando o decodificador 4 por 16:
        decode4x16 = new Decode4x16();

        // ** Inicializando os registradores do caminho de dados:
        mar = new Registrador();
        mdr = new Registrador();
        pc = new Registrador();
        mbr = new Mbr();
        sp = new Registrador();
        lv = new Registrador();
        cpp = new Registrador();
        tos = new Registrador();
        opc = new Registrador();
        h = new Registrador();

        //INICIALIZANDO MPC E MIR:
        mpc = new Mpc();
        mir = new Mir();

        contadorCiclo = 0;

    }
    
    //Um ciclo da Mic:
    public void ciclo() {
        System.out.println("\nCICLO " + contadorCiclo + " ---------------------------------------------------------------------" +
                    "-------------------------------------------------------------------------------------------------------");
        //1º: Carregar o MPC com o valor X (0);
        //    Carregar o MPC quer dizer a próxima microinstrução; 
        //    Mpc por padrão é inicializado com zero;
        //    **E entender se ele será carregado inicialmente com um valor 0 ou com o primeiro valor do armazenamento de controle:
        
        System.out.print("\nProxima Instrucao MPC = " + mpc.getValor() + " POSICAO -> [" + mpc.getValorLong() + "]");
        System.out.print("\tarmazenamentoControle[" + mpc.getValorLong() + "] = " + armazenamentoControle[mpc.getValorInt()]);
        System.out.println("\tMicroInstrucao = " + getMicroInstrucao(armazenamentoControle[mpc.getValorInt()]));
        
        //2º: Após, pegar a microinstrução endereçada em MPC e joga para a MIR. Na MIR, decodificamos a microinstrução.
        mir.setValor(armazenamentoControle[ mpc.getValorInt()]);

        System.out.print("\n|MIR|: " + mir.getValor() + " (" + mir.getValorLong() + ")");
        //Quebrando a microinstrução alocada em MIR:
        String addr, jam, controleUla, c, mem, microInstrucao, b;
        microInstrucao = mir.getValor();
        addr = microInstrucao.substring(0, 9); //(9 bits mais significativos) next address;
        jam = microInstrucao.substring(9, 12); //(3 bits) bits p/ selecionar próxima instrução;
        controleUla = microInstrucao.substring(12, 20);//(8 bits) bits seletores para o controle da ULA;
        c = microInstrucao.substring(20, 29);	//(9 bits) bits selecionadores de registradores p/ receber valor do barramento C;
        mem = microInstrucao.substring(29, 32);	//(3 bits) bits de operação de memoria;
        b = microInstrucao.substring(32, 36); //(4 bits) selecionador de registrador para o barramento B; 

        System.out.println(" | ADDR: " + addr + " | JAM: " + jam + " | ULA: " + controleUla + " | BARRAMENTO C: " + c + " | MEM: " + mem + " |BARRAMENTO B:" + b);

        // **Setando o valor armazenado nos Barramentos:
        // **O barramento B deve passar por um decodificador 4 para 16 para escolher qual o seu registrador;
        // **Carregando o barramento B dependendo do retorno do decodificador 4 x 16:
        // **Colocando no barramento A o valor do registrador h:
        carregarBarramentoB(decode4x16.decodificar(b));
        carregarBarramentoA();

        System.out.println("\nVALORES CARREGADOS NOS BARRAMENTOS: Barramento A = " + barramentoA.getValor() + "\t- Barramento B = " + barramentoB.getValor());

        //4º: Fazer a operação Aritmética na ULA a partir do valor dos Barramentos A e B e os bits de controle:
        ula.executar(controleUla);

        //5º: Passando para o Deslocador o valor do Barramento Intermediário (Barramento I), o valor de SLL8 e SRA1:
        // ** controleUla.substring(0,1) é o bit mais significativo dentre os  8 bits, ou seja, SSL8;
        // ** controleUla.substring(1,2) é o segundo bit mais significativo dentre os  8 bits, ou seja, SRA1;  
        deslocador.executar(barramentoI.getValor(), controleUla.substring(0, 1), controleUla.substring(1, 2));
        //System.out.println("\n\n  **Valor do Barramento C carregado pos Deslocador: " + barramentoC.getValor());

        //6º: Carregar o valor do Barramento C no Registrador selecionado pela microinstrução:
        carregarBarramentoC(c);

        //7º: Fazer operações de memória, passando a parte referente das microinstruções:
        carregarOperacoesMemoria(mem);

        //8º: Definir proxima instrução:
        carregarProximaInstrucao(jam.substring(0, 1), addr); //JAMPC 

        // Passar valores N, Z da ULA e JAMN e JAMZ e ADDR[0]: (FAZER BIT ALTO)
        bitAlto(ula.getN(), ula.getZ(), jam.substring(1, 2), jam.substring(2, 3), addr.substring(0, 1));
        // 			N, 			Z, 			JAMN, 				JAMZ, 				ADDR[0] (bit mais sig)

        System.out.println("\n\n  ****Proxima instrucao: " + mpc.getValor());

        contadorCiclo++;

    }

    // Função que coloca no Barramento A o valor do Registrador H:
    private void carregarBarramentoA() {
        //passar para o barramento A o valor do Registrador H;
        System.out.println("  **Carregando registrador H no Barramento A.");
        barramentoA.setValor(h.getValor());
    }

    // Função que seleciona um Registrador e coloca o seu valor no Barramento B:
    private void carregarBarramentoB(String resultadoDecodificador) {
        //System.out.println("Resultado vindo do decodificador: " + resultadoDecodificador);
        if (resultadoDecodificador.equals("0x00")) {
            System.out.println("  **Carregando registrador MDR no Barramento B");
            barramentoB.setValor(mdr.getValor());
        } else if (resultadoDecodificador.equals("0x01")) {
            System.out.println("  **Carregando registrador PC no Barramento B");
            barramentoB.setValor(pc.getValor());
        } else if (resultadoDecodificador.equals("0x02")) {
            System.out.println("  **Carregando registrador MBR no Barramento B");
            barramentoB.setValor(mbr.getValor());
        } else if (resultadoDecodificador.equals("0x03")) {
            System.out.println("  **Carregando registrador MBR com Extensao de Sinal no Barramento B");
            barramentoB.setValor(mbr.getValor()); //getValorExtensaoSinal()
        } else if (resultadoDecodificador.equals("0x04")) {
            System.out.println("  **Carregando registrador SP no Barramento B");
            barramentoB.setValor(sp.getValor());
        } else if (resultadoDecodificador.equals("0x05")) {
            System.out.println("  **Carregando registrador LV no Barramento B");
            barramentoB.setValor(lv.getValor());
        } else if (resultadoDecodificador.equals("0x06")) {
            System.out.println("  **Carregando registrador CPP no Barramento B");
            barramentoB.setValor(cpp.getValor());
        } else if (resultadoDecodificador.equals("0x07")) {
            System.out.println("  **Carregando registrador TOS no Barramento B");
            barramentoB.setValor(tos.getValor());
        } else if (resultadoDecodificador.equals("0x08")) {
            System.out.println("  **Carregando registrador OPC no Barramento B");
            barramentoB.setValor(opc.getValor());
        }
    }

    // Função que pega o valor do Barramento C e coloca no(s) Registrador(es) correspondente(s):
    private void carregarBarramentoC(String c) {
        System.out.print("\n\nCarregando Registrador a partir da parte da Microinstrucao sobre o barramento C: ");
        System.out.println(c + " -> valor armazenado no barramento C = " + barramentoC.getValor() + " (" + Long.parseLong(barramentoC.getValor(), 2) + ")");

        if (c.substring(0, 1).equals("1")) {
            System.out.println("  **Registrador H selecionado");
            h.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador H = " + h.getValor());
        }
        if (c.substring(1, 2).equals("1")) {
            System.out.println("  **Registrador OPC selecionado");
            opc.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador OPC = " + opc.getValor());
        }
        if (c.substring(2, 3).equals("1")) {
            System.out.println("  **Registrador TOS selecionado");
            tos.setValor(barramentoC.getValor());
            System.out.println(" **Valor do Registrador TOS = " + tos.getValor());
        }
        if (c.substring(3, 4).equals("1")) {
            System.out.println("  **Registrador CPP selecionado");
            cpp.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador CPP = " + cpp.getValor());
        }
        if (c.substring(4, 5).equals("1")) {
            System.out.println("  **Registrador LV selecionado");
            lv.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador LV = " + lv.getValor());
        }
        if (c.substring(5, 6).equals("1")) {
            System.out.println("  **Registrador SP selecionado");
            sp.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador SP = " + sp.getValor());
        }
        if (c.substring(6, 7).equals("1")) {
            System.out.println("  **Registrador PC selecionado");
            pc.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador PC = " + pc.getValor());
        }
        if (c.substring(7, 8).equals("1")) {
            System.out.println("  **Registrador MDR selecionado");
            mdr.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador MDR = " + mdr.getValor());
        }
        if (c.substring(8, 9).equals("1")) {
            System.out.println("  **Registrador MAR selecionado");
            mar.setValor(barramentoC.getValor());
            System.out.println("  **Valor do Registrador MAR = " + mar.getValor());
        }
    }

    //Função que executará as operações na memória
    private void carregarOperacoesMemoria(String mem) {
        String write, read, fetch;
        write = mem.substring(0, 1);
        read = mem.substring(1, 2);
        fetch = mem.substring(2, 3);

        System.out.println("\n|OPERACOES DE MEMORIA|: write = " + write + " - read = " + read + " fetch = " + fetch + "\n");

        if (write.equals("1")) {
            System.out.println("  **WRITE: Escrevendo os 4 bytes (palavra) do MDR em 4 posicoes de memoria consecutivas a partir de memoriaPrincipal[" + (4 * mar.getValorLong()) + "]");

            // Anotando uma word na memoria principal:
            // Converter valor armazenado em byte:
            System.out.println("  **Valor do MDR = " + mdr.getValor());

            int endereco = (int) mar.getValorLong();

            System.out.println("Byte do MDR menos significativo para memoriaPrincipal[" + (4 * endereco) + "] = " + mdr.getValor().substring(24, 32) + " (" + setBinary(mdr.getValor().substring(24, 32)) + ")");
            System.out.println("Segundo Byte do MDR menos significativo para memoriaPrincipal[" + (4 * endereco + 1) + "] = " + mdr.getValor().substring(16, 24) + " (" + setBinary(mdr.getValor().substring(16, 24)) + ")");
            System.out.println("Byte do MDR menos significativo para memoriaPrincipal[" + (4 * endereco + 2) + "] = " + mdr.getValor().substring(8, 16) + " (" + setBinary(mdr.getValor().substring(8, 16)) + ")");
            System.out.println("Byte do MDR menos significativo para memoriaPrincipal[" + (4 * endereco + 3) + "] = " + mdr.getValor().substring(0, 8) + " (" + setBinary(mdr.getValor().substring(0, 8)) + ")");

            this.memoriaPrincipal[4 * endereco] = setBinary(mdr.getValor().substring(24, 32)); //byte menos significativo; 
            this.memoriaPrincipal[4 * endereco + 1] = setBinary(mdr.getValor().substring(16, 24));
            this.memoriaPrincipal[4 * endereco + 2] = setBinary(mdr.getValor().substring(8, 16));
            this.memoriaPrincipal[4 * endereco + 3] = setBinary(mdr.getValor().substring(0, 8)); //byte mais significativo;
        }

        if (read.equals("1")) {
            System.out.println("  **READ: Lendo os 4 bytes (palavra) a partir de memoriaPrincipal[" + (4 * mar.getValorLong()) + "] e jogando no MDR");

            int endereco = (int) mar.getValorLong();

            System.out.println("  **Valor da palavra da memoria = " + memoriaPrincipal[4 * endereco + 3]
                    + "." + memoriaPrincipal[4 * endereco + 2] + "." + memoriaPrincipal[4 * endereco + 1] + "."
                    + memoriaPrincipal[4 * endereco]);

            System.out.println("  ****Valor da palavra da memoria = " + getBinary(memoriaPrincipal[4 * endereco + 3])
                    + "." + getBinary(memoriaPrincipal[4 * endereco + 2]) + "." + getBinary(memoriaPrincipal[4 * endereco + 1]) + "."
                    + getBinary(memoriaPrincipal[4 * endereco]));

            String valor = "";
            valor = getBinary(this.memoriaPrincipal[4 * endereco]) + valor;
            valor = getBinary(this.memoriaPrincipal[4 * endereco + 1]) + valor;
            valor = getBinary(this.memoriaPrincipal[4 * endereco + 2]) + valor;
            valor = getBinary(this.memoriaPrincipal[4 * endereco + 3]) + valor;

            mdr.setValor(valor);

        }

        if (fetch.equals("1")) {
            System.out.print("  **FETCH: Lendo 1 byte da memoriaPrincipal[" + pc.getValorLong() + "] = ");
            String valor = "";

            int endereco = (int) pc.getValorLong();

            valor = getBinary(memoriaPrincipal[endereco]) + valor;

            System.out.println(" e jogando no MBR");
            mbr.setValor(valor);

            System.out.println("  **Valor setado agora no MBR= " + mbr.getValor());
        }

    }

    private void carregarProximaInstrucao(String jmpc, String addr) {
        System.out.println("\n\n|PROXIMA INSTRUCAO NO MPC|: ");
        if (jmpc.equals("1")) {
            // JMPC = 1, entao MPC = ADDR or MBR
            System.out.print("\tJMPC = 1 ->");
            System.out.print(" ADDR = " + addr + " (" + Long.parseLong(addr, 2) + ")");
            System.out.println(" MBR = " + mbr.getValor() + " (" + Long.parseLong(mbr.getValor(), 2) + ")");

            mpc.setValor(Long.parseLong(addr, 2) | Long.parseLong(mbr.getValor(), 2));

            System.out.println("  **Valor MPC = " + mpc.getValor());

        } else {
            // JMPC = 0, entao MPC = ADDR
            System.out.print("\tJMPC = 0 ->");
            System.out.print(" ADDR = " + addr + " (" + Long.parseLong(addr, 2) + ")");

            mpc.setValor(Long.parseLong(addr, 2));

            System.out.println("  **Valor MPC = " + mpc.getValor());
        }

        System.out.println();

    }

    private void bitAlto(String n, String z, String jamn, String jamz, String addr) {
        System.out.println("\n\n|Bit Alto|:\tN=" + n + " Z=" + z + " JAMN= " + jamn + " JAMZ= " + jamz + " ADDR[msb]= " + addr);

        boolean N = valor(n);
        boolean Z = valor(z);
        boolean JAMN = valor(jamn);
        boolean JAMZ = valor(jamz);
        boolean ADDR = valor(addr);

        System.out.println("\t" + N + " " + Z + " " + JAMN + " " + JAMZ + " " + ADDR + " -> MPC ATUAL = " + mpc.getValor());

        String valor;

        if ((JAMZ && Z) || (JAMN && N) || ADDR) {
            System.out.print("  **Bit Alto = 1");
            valor = "1";
            valor = valor + mpc.getValor();
            System.out.println(" -> MPC pos BIT ALTO = " + valor);
            mpc.setValor(valor);
        } else {
            System.out.print("  **Bit Alto = 0");
            valor = "0";
            valor = valor + mpc.getValor();
            System.out.println(" -> MPC pos BIT ALTO = " + valor);
            mpc.setValor(valor);
        }

    }

    // ** Função cujo objetivo é receber "0" ou "1" e retornar true ou false.
    private boolean valor(String valor) {
        if (valor.equals("0")) {
            return false;
        }
        return true;
    }

    private String getBinary(byte b) {

        System.out.print(b);
        int qtdZeros = 8 - Integer.toBinaryString(b).length();

        String bits = Integer.toBinaryString(b);

        for (int i = 0; i < qtdZeros; i++) {
            bits = "0" + bits;
        }

        return bits;
    }

    private byte setBinary(String b) {

        System.out.println(" setBinary(): " + Integer.parseInt(b, 2));
        return (byte) Integer.parseInt(b, 2);
    }

    private String getMicroInstrucao(long l) {
        //retornamos uma microinstrução de 36 bits a partir de um numero qualquer:
        String microInstrucao = Long.toBinaryString(l);
        int qtdZeros = 36 - Long.toBinaryString(l).length();

        for (int i = 0; i < qtdZeros; i++) {
            microInstrucao = 0 + microInstrucao;
        }

        return microInstrucao;

    }

    
    private void printArmazenamentoControle(int index) {
        if (index >= 0 && index < 512) {
            String microInstrucao = getMicroInstrucao(armazenamentoControle[index]);

            System.out.println(armazenamentoControle[index] + " -->\t" + microInstrucao);

            //Contém o endereço para a próxima instrução;
            System.out.println("Addr: " + microInstrucao.substring(0, 9));

            //Determina como a próxima microinstrução é selecionada;
            System.out.println("JAM: " + microInstrucao.substring(9, 12));

            //Funções da ULA e do Deslocador; 
            System.out.println("ULA: " + microInstrucao.substring(12, 20));

            //Seleciona quais registradores são escritos a partir do barramento C;
            System.out.println("C: " + microInstrucao.substring(20, 29));

            //Funções de memória; 
            System.out.println("Mem: " + microInstrucao.substring(29, 32));

            //Seleciona a fonte do barramento B; 
            System.out.println("B: " + microInstrucao.substring(32, 36));
        }

    }

    private void printArmazenamentoControle(int initIndex, int endIndex) {
        /*
			getArmazenamentoControle(): imprimimos de initIndex a endIndex;
         */
        if (initIndex >= 0 && initIndex < 512 && endIndex >= 0 && endIndex < 512 && initIndex < endIndex) {
            for (int i = initIndex; i < endIndex; i++) {
                System.out.print(armazenamentoControle[i]);
                System.out.print(" -->\t");

                if (armazenamentoControle[i] == 0) {
                    System.out.print("\t");
                }

                System.out.println(getMicroInstrucao(armazenamentoControle[i]));

            }
        }
    }

    private void printMemoriaPrincipal() {
        /*
			getMemoriaPrincipal: Imprime todos os dados os dados armazenados no vetor de bytes da memória principal;
         */
        System.out.println("|Memoria Principal|: vetor de bytes (em representacao decimal)");
        for (byte b : memoriaPrincipal) {
            System.out.print(b + " ");
        }

        System.out.println();
    }

    private void readRom() {
        try {
            InputStream entrada = new FileInputStream(this.microprog); //lendo o arquivo binário. Fluxo baseado em bytes;

            //Variáveis essenciais:
            int intReaded = entrada.read();
            String microInstrucao = "";

            //Variáveis auxiliares:
            byte bByte;
            String x = "", bBinary;

            for (int i = 0; i < 512; i++) { //512
                //System.out.print((i+1) + ": ");

                for (int j = 0; j < 8; j++) { //8 incrementos de byte = 1 microinstrução

                    bByte = (byte) intReaded;
                    bBinary = String.format("%8s", Integer.toBinaryString(bByte & 0xFF)).replace(' ', '0');

                    x = intReaded + " " + x; //concatenando na ordem inversa os inteiros lidos;

                    microInstrucao = bBinary + microInstrucao; //concatenando na ordem inversa os numeros binarios de oito;

                    //System.out.print(intReaded + " "); //lendo até a404 da segunda linha
                    intReaded = entrada.read();

                }

                //System.out.print("--> " + x + " --> " + microInstrucao); //cadeia de bytes em formato de inteiros ao contrário;
                microInstrucao = microInstrucao.substring(28, 64); // Pegando os 36 bits da microinstrução.
                //System.out.print(" --> (36 bits) " + microInstrucao); 

                long l = Long.parseLong(microInstrucao, 2);
                //System.out.println("\n ** --> (decimal) " + l + "\n");

                armazenamentoControle[i] = l;

                microInstrucao = "";
                x = "";

            }

        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }
    }

    private void readExe(String exe) {
        try {
            InputStream entrada;

            if (exe == null) {
                entrada = new FileInputStream(this.prog); //lendo o arquivo binário. Fluxo baseado em bytes;
            } else {
                entrada = new FileInputStream(exe);
            }

            //Variáveis essenciais:
            int intReaded = entrada.read();
            int index = 0;

            while (intReaded != -1) {
                memoriaPrincipal[index] = (byte) intReaded;
                //System.out.println(intReaded + " - " + (byte)intReaded);
                index++;
                intReaded = entrada.read();
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }
    }

    public static void main(String[] args) {
        Mic1 mic1;

        if (args.length == 0) {
            mic1 = new Mic1(null);
        } else {
            mic1 = new Mic1(args[0]);
        }

        //mic1.printArmazenamentoControle(0);
        mic1.printMemoriaPrincipal();

        mic1.ciclo();
        mic1.ciclo();
        mic1.ciclo();
        mic1.ciclo();
        
        /*
        mic1.ciclo();
        mic1.ciclo();
        mic1.ciclo();
        mic1.ciclo();

        mic1.ciclo();
        mic1.ciclo();
        mic1.ciclo();
        mic1.ciclo();
        */

    }

}
