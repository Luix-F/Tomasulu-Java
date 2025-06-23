import java.util.ArrayList;
import java.util.List;

public class simulador {
    public static int Cycle = 0;
    public static int pc = 0;
    public static int Issue = 0;

    // Tempo de cada unidade
    public static int InteCycle = 2;
    public static int MultCycle = 3;
    public static int MemCycle = 3;
    public static int BrCycle = 1;

    // Predição
    public static prediction predic = new prediction();

    // Tamanho de cada Estação de Reserva
    public static int TamanhoJanela = 4;

    // Registradores
    public static Register re = new Register();

    // Buffer de Reordenamento
    public static List<linhaBF> bf = new ArrayList<>();

    // Unidades funcionais por ciclo
    public static linhaBF tabelaFuncional[][];

    // Memória de instrução
    public static String VecIntruction[][] = Utilitario.instructLoad("instruct.luix");

    // Inicializa o sistema
    public static void atribuir(){
        re.atribuir();

        tabelaFuncional = new linhaBF[VecIntruction.length+5][5];
        // Initialize each element
        for (int i = 0; i < VecIntruction.length+5; i++) {
            for (int j = 0; j < 5; j++) {
                tabelaFuncional[i][j] = new linhaBF(); // Assuming TabelaFuncional is a class
            }
        }

        tabelaFuncional[0][0].op = "INTEGER";
        tabelaFuncional[0][1].op = "INTEGER";
        tabelaFuncional[0][2].op = "MULT";
        tabelaFuncional[0][3].op = "MEM";
        tabelaFuncional[0][4].op = "BR";
    }

    // Verifica se a Estação de Reserva esta lotada
    public static boolean EstacaoLotada(String op){
        int inter = 0;
        int mult = 0;
        int mem = 0;
        int br = 0;
        for (linhaBF lBF : bf) {
            if (lBF.status.equals("Not-Use")) {
                switch (lBF.op) { // Decodeficação
                    case "ADD":
                        inter++;
                        if (inter > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "SUB":
                        inter++;
                        if (inter > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "MULT":
                        mult++;
                        if (mult > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "DIV":
                        mult++;
                        if (mult > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "LOAD":
                        mem++;
                        if (mem > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "STORE":
                        mem++;
                        if (mem > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "BNE":
                        br++;
                        if (br > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                    case "BEQ":
                        br++;
                        if (br > TamanhoJanela-1) {
                            return true;
                        }
                    break;
                }
            }
        }
        return false;
    }

    // Verifica se existe dependências para o despacho neste ciclo
    public static boolean Dependencias(int id){
        /*
        for (int i = 0; i < tabelaFuncional[0].length; i++) {
            // Dependências diretas
            if ((bf.get(id).r1.equals(tabelaFuncional[Cycle+1][i].r0) || bf.get(id).r2.equals(tabelaFuncional[Cycle+1][i].r0))) {
                return false;
            }
            // Dependências indiretas
            if (bf.get(id).r0.equals(tabelaFuncional[Cycle+1][i].r1) || bf.get(id).r0.equals(tabelaFuncional[Cycle+1][i].r2)) {
                bf.get(id).r0 = "R==" + bf.get(id).r0;
            }
        }*/
        for (int i = 0; i < id; i++) {
            if (!bf.get(i).status.equals("IGNORE")) {
                if (!bf.get(i).status.equals("END") && !bf.get(i).status.equals("COMMIT")) {
                    // Dependências diretas
                    if ((bf.get(id).r1.equals(bf.get(i).r0) || bf.get(id).r2.equals(bf.get(i).r0))) {
                        return false;
                    }/*
                    if (bf.get(id).r0.equals(bf.get(i).r1) || bf.get(id).r0.equals(bf.get(i).r2)) {
                        bf.get(id).rename = "R==";
                    }*/
                }
            }
            
        }
        return true;
    }

    // Pesquisa a instrução pelo Issue
    public static int SearchIssue(int isu){
        for (int i = 0; i < bf.size(); i++) {
            if (isu == bf.get(i).issue) {
                return i;
            }
        }
        return 0;
    }


    // Atualiza o fim da vida de uma instrução
    public static void AtzCycle(){
        
        for (int i = 0; i < tabelaFuncional[0].length; i++) {
            if (tabelaFuncional[Cycle+1][i].status.equals("END")) {
                int tmp = SearchIssue(tabelaFuncional[Cycle+1][i].issue);

                bf.get(tmp).status = "END";

                if (bf.get(tmp).issue == 8) {
                    System.out.println("null");
                }

                if (!bf.get(tmp).rename.startsWith("R")) {
                    float result = re.ExecInside(bf.get(tmp));
                    bf.get(tmp).result = result;
                    if (bf.get(tmp).op.equals("BNE") || bf.get(tmp).op.equals("BEQ")) {
                        if (((int)result) == 1) {
                            if (!predic.last) {
                                int  jump = (int) Float.parseFloat(bf.get(tmp).r0);
                                pc = bf.get(tmp).pcAtual + (jump);
                                for (int j = tmp+1; j < bf.size(); j++) {
                                    if (bf.get(j).rename.startsWith("R")) {
                                        //System.out.println("null" + re.Registradores[9][0]);
                                        bf.get(j).rename = "";
                                        re.Ignor(bf.get(j));
                                    }
                                    bf.get(j).status = "IGNORE";
                                }
                                IgnoreMode(bf.get(tmp));
                            }
                            predic.aumento();
                        }else{
                            if (predic.last) {
                                pc = bf.get(tmp).pcAtual+1;
                                for (int j = tmp+1; j < bf.size(); j++) {
                                    if (bf.get(j).rename.startsWith("R")) {
                                        //System.out.println("null" + re.Registradores[9][0]);
                                        bf.get(j).rename = "";
                                        re.Ignor(bf.get(j));
                                    }
                                    bf.get(j).status = "IGNORE";
                                }
                                IgnoreMode(bf.get(tmp));
                            }
                            predic.diminui();
                        }
                    }
                }else{
                    float result = re.ExecInsideRenome(bf.get(tmp));
                    bf.get(tmp).result = result;
                    if (bf.get(tmp).op.equals("BNE") || bf.get(tmp).op.equals("BEQ")) {
                        if (((int)result) == 1) {
                            int  jump = (int) Float.parseFloat(bf.get(tmp).r0);
                            pc = bf.get(tmp).pcAtual + (jump);
                            for (int j = tmp+1; j < bf.size(); j++) {
                                bf.get(j).status = "IGNORE";
                            }
                            IgnoreMode(bf.get(tmp));
                            predic.aumento();
                        }else{
                            predic.diminui();
                        }
                    }
                }
                re.SincronizaReg(bf);
            }
        }
    }

    // Despacha todas as intruções possíveis da Reserva
    public static boolean Despacho(){
        int posi = 0;
        for (linhaBF lBF : bf) {
            if (lBF.status.equals("Not-Use")) {
                switch (lBF.op) { // Decodeficação
                    case "ADD":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "SUB":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "MULT":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "DIV":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "LOAD":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "STORE":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "BNE":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                    case "BEQ":
                        if (Dependencias(posi)) {
                            decode(posi);
                        }
                    break;
                }
            }
            posi++;
        }
        return false;
    }

    public static void IgnoreMode(linhaBF lBF){
        for (int i = lBF.StartCycle+1; i < tabelaFuncional.length; i++) {
            for (int j = 0; j < tabelaFuncional[0].length; j++) {
                if (lBF.issue < tabelaFuncional[i][j].issue) {
                    tabelaFuncional[i][j].del();
                }
            }
        }
        //!bf.get(id).status.equals("IGNORE")
    }
    
    // Encontra próxima instrução
    public static void Find(){
        for (int i = pc; i < pc+1; i++) {
            if (!VecIntruction[i][0].equals("end") && i < VecIntruction.length) {
                linhaBF tlbf = new linhaBF();
                tlbf.op = VecIntruction[i][0];
                tlbf.r0 = VecIntruction[i][1];
                tlbf.r1 = VecIntruction[i][2];
                tlbf.r2 = VecIntruction[i][3];
                if (!EstacaoLotada(tlbf.op)) {
                    tlbf.issue = Issue;
                    tlbf.pcAtual = pc;
                    bf.add(tlbf);
                    Issue++;
                    if (predic.desvia(tlbf.op)) {
                        pc = (int) (pc + Float.parseFloat(tlbf.r0));
                        i = pc;
                        predic.last = true;
                    }else{
                        pc++;
                    }
                    
                }
            }
        }
    }
    

    public static void AntDependencias(int id, int iTB){
        for (int i = 0; i < id; i++) {
            if (!bf.get(i).status.equals("IGNORE")) {
                if (!bf.get(i).status.equals("END") && !bf.get(i).status.equals("COMMIT")) {
                    // Dependências indiretas
                    
                    if (bf.get(id).r0.equals(bf.get(i).r1) || bf.get(id).r0.equals(bf.get(i).r2)) {
                        String s = re.RenomeVazio(bf.get(id).r0);
                        bf.get(id).rename = s+"=";
                        tabelaFuncional[Cycle+1][iTB].rename = s+"=";
                    }
                }
            }
            
        }
    }


    // Separa a instrução e incrementa na Unidade Funcional
    public static int decode(int id){
        // Decodeficação
        if (bf.get(id).op.equals("ADD") || bf.get(id).op.equals("SUB")) {
            if (!bf.get(id).status.equals("IGNORE")) {
                for (int i = 0; i < tabelaFuncional[0].length; i++) {
                    if (tabelaFuncional[0][i].op.equals("INTEGER") && tabelaFuncional[Cycle+1][i].status.equals("Not-Use")) {
                        AntDependencias(id, i);
                        tabelaFuncional[Cycle+1][i].op = bf.get(id).op;
                        tabelaFuncional[Cycle+1][i].r0 = bf.get(id).r0;
                        tabelaFuncional[Cycle+1][i].r1 = bf.get(id).r1;
                        tabelaFuncional[Cycle+1][i].r2 = bf.get(id).r2;
                        tabelaFuncional[Cycle+1][i].status = "EXECUTE";
                        bf.get(id).status = "EXECUTE";
    
                        bf.get(id).NeedCycle = InteCycle;
                        bf.get(id).StartCycle = Cycle;
                        bf.get(id).EndCycle = bf.get(id).StartCycle + bf.get(id).NeedCycle;
                        
                        //tabelaFuncional[Cycle+1][i].issue = Issue;
                        //bf.get(id).issue = Issue;
                        tabelaFuncional[Cycle+1][i].issue = bf.get(id).issue;
    
                        if (bf.get(id).NeedCycle > 1) {
                            for (int j = bf.get(id).StartCycle+1; j < bf.get(id).EndCycle; j++) {
                                tabelaFuncional[j+1][i].rename = bf.get(id).rename;
                                tabelaFuncional[j+1][i].op = bf.get(id).op;
                                tabelaFuncional[j+1][i].r0 = bf.get(id).r0;
                                tabelaFuncional[j+1][i].r1 = bf.get(id).r1;
                                tabelaFuncional[j+1][i].r2 = bf.get(id).r2;
                                tabelaFuncional[j+1][i].status = "EXECUTE";
                                bf.get(id).status = "EXECUTE";
    
                                //tabelaFuncional[j+1][i].issue = Issue;
                                //bf.get(id).issue = Issue;
                                tabelaFuncional[j+1][i].issue = bf.get(id).issue;
    
                                if (j == bf.get(id).EndCycle-1) {
                                    tabelaFuncional[j+1][i].status = "END";
                                    //bf.get(id).status = "END";
                                }
                            }
                        }else{
                            tabelaFuncional[Cycle+1][i].status = "END";
                            //bf.get(id).status = "END";
                        }
                        
                        //Issue++;
                        return 0;
                    }
                }
            }
        }else if (bf.get(id).op.equals("MULT") || bf.get(id).op.equals("DIV")) {
            if (!bf.get(id).status.equals("IGNORE")) {
                for (int i = 0; i < tabelaFuncional[0].length; i++) {
                    if (tabelaFuncional[0][i].op.equals("MULT") && tabelaFuncional[Cycle+1][i].status.equals("Not-Use")) {
                        AntDependencias(id, i);
                        tabelaFuncional[Cycle+1][i].op = bf.get(id).op;
                        tabelaFuncional[Cycle+1][i].r0 = bf.get(id).r0;
                        tabelaFuncional[Cycle+1][i].r1 = bf.get(id).r1;
                        tabelaFuncional[Cycle+1][i].r2 = bf.get(id).r2;
                        tabelaFuncional[Cycle+1][i].status = "EXECUTE";
                        bf.get(id).status = "EXECUTE";
    
                        bf.get(id).NeedCycle = MultCycle;
                        bf.get(id).StartCycle = Cycle;
                        bf.get(id).EndCycle = bf.get(id).StartCycle + bf.get(id).NeedCycle;
                        
                        //tabelaFuncional[Cycle+1][i].issue = Issue;
                        //bf.get(id).issue = Issue;
                        tabelaFuncional[Cycle+1][i].issue = bf.get(id).issue;
    
                        if (bf.get(id).NeedCycle > 1) {
                            for (int j = bf.get(id).StartCycle+1; j < bf.get(id).EndCycle; j++) {
                                tabelaFuncional[j+1][i].rename = bf.get(id).rename;
                                tabelaFuncional[j+1][i].op = bf.get(id).op;
                                tabelaFuncional[j+1][i].r0 = bf.get(id).r0;
                                tabelaFuncional[j+1][i].r1 = bf.get(id).r1;
                                tabelaFuncional[j+1][i].r2 = bf.get(id).r2;
                                tabelaFuncional[j+1][i].status = "EXECUTE";
                                bf.get(id).status = "EXECUTE";
    
                                //tabelaFuncional[j+1][i].issue = Issue;
                                //bf.get(id).issue = Issue;
                                tabelaFuncional[j+1][i].issue = bf.get(id).issue;
    
                                if (j == bf.get(id).EndCycle-1) {
                                    tabelaFuncional[j+1][i].status = "END";
                                    //bf.get(id).status = "END";
                                }
                            }
                        }else{
                            tabelaFuncional[Cycle+1][i].status = "END";
                            //bf.get(id).status = "END";
                        }
                        
                        //Issue++;
                        return 0;
                    }
            }
            }
        }else if (bf.get(id).op.equals("LOAD") || bf.get(id).op.equals("STORE")) {
            if (!bf.get(id).status.equals("IGNORE")) {
                for (int i = 0; i < tabelaFuncional[0].length; i++) {
                    if (tabelaFuncional[0][i].op.equals("MEM") && tabelaFuncional[Cycle+1][i].status.equals("Not-Use")) {
                        AntDependencias(id, i);
                        tabelaFuncional[Cycle+1][i].op = bf.get(id).op;
                        tabelaFuncional[Cycle+1][i].r0 = bf.get(id).r0;
                        tabelaFuncional[Cycle+1][i].r1 = bf.get(id).r1;
                        tabelaFuncional[Cycle+1][i].r2 = bf.get(id).r2;
                        tabelaFuncional[Cycle+1][i].status = "EXECUTE";
                        bf.get(id).status = "EXECUTE";
    
                        bf.get(id).NeedCycle = MemCycle;
                        bf.get(id).StartCycle = Cycle;
                        bf.get(id).EndCycle = bf.get(id).StartCycle + bf.get(id).NeedCycle;
                        
                        //tabelaFuncional[Cycle+1][i].issue = Issue;
                        //bf.get(id).issue = Issue;
                        tabelaFuncional[Cycle+1][i].issue = bf.get(id).issue;
    
                        if (bf.get(id).NeedCycle > 1) {
                            for (int j = bf.get(id).StartCycle+1; j < bf.get(id).EndCycle; j++) {
                                tabelaFuncional[j+1][i].rename = bf.get(id).rename;
                                tabelaFuncional[j+1][i].op = bf.get(id).op;
                                tabelaFuncional[j+1][i].r0 = bf.get(id).r0;
                                tabelaFuncional[j+1][i].r1 = bf.get(id).r1;
                                tabelaFuncional[j+1][i].r2 = bf.get(id).r2;
                                tabelaFuncional[j+1][i].status = "EXECUTE";
                                bf.get(id).status = "EXECUTE";
    
                                //tabelaFuncional[j+1][i].issue = Issue;
                                //bf.get(id).issue = Issue;
                                tabelaFuncional[j+1][i].issue = bf.get(id).issue;
    
                                if (j == bf.get(id).EndCycle-1) {
                                    tabelaFuncional[j+1][i].status = "END";
                                    //bf.get(id).status = "END";
                                }
                            }
                        }else{
                            tabelaFuncional[Cycle+1][i].status = "END";
                            //bf.get(id).status = "END";
                        }
                        
                        //Issue++;
                        return 0;
                    }
            }
            }
        }else if (bf.get(id).op.equals("BNE") || bf.get(id).op.equals("BEQ")) {
            if (!bf.get(id).status.equals("IGNORE")) {
                for (int i = 0; i < tabelaFuncional[0].length; i++) {
                    if (tabelaFuncional[0][i].op.equals("BR") && tabelaFuncional[Cycle+1][i].status.equals("Not-Use")) {
                        AntDependencias(id, i);
                        tabelaFuncional[Cycle+1][i].op = bf.get(id).op;
                        tabelaFuncional[Cycle+1][i].r0 = bf.get(id).r0;
                        tabelaFuncional[Cycle+1][i].r1 = bf.get(id).r1;
                        tabelaFuncional[Cycle+1][i].r2 = bf.get(id).r2;
                        tabelaFuncional[Cycle+1][i].status = "EXECUTE";
                        bf.get(id).status = "EXECUTE";
    
                        bf.get(id).NeedCycle = BrCycle;
                        bf.get(id).StartCycle = Cycle;
                        bf.get(id).EndCycle = bf.get(id).StartCycle + bf.get(id).NeedCycle;
                        
                        //tabelaFuncional[Cycle+1][i].issue = Issue;
                        //bf.get(id).issue = Issue;
                        tabelaFuncional[Cycle+1][i].issue = bf.get(id).issue;
    
                        if (bf.get(id).NeedCycle > 1) {
                            for (int j = bf.get(id).StartCycle+1; j < bf.get(id).EndCycle; j++) {
                                tabelaFuncional[j+1][i].rename = bf.get(id).rename;
                                tabelaFuncional[j+1][i].op = bf.get(id).op;
                                tabelaFuncional[j+1][i].r0 = bf.get(id).r0;
                                tabelaFuncional[j+1][i].r1 = bf.get(id).r1;
                                tabelaFuncional[j+1][i].r2 = bf.get(id).r2;
                                tabelaFuncional[j+1][i].status = "EXECUTE";
                                bf.get(id).status = "EXECUTE";
    
                                //tabelaFuncional[j+1][i].issue = Issue;
                                //bf.get(id).issue = Issue;
                                tabelaFuncional[j+1][i].issue = bf.get(id).issue;
    
                                if (j == bf.get(id).EndCycle-1) {
                                    tabelaFuncional[j+1][i].status = "END";
                                    //bf.get(id).status = "END";
                                }
                            }
                        }else{
                            tabelaFuncional[Cycle+1][i].status = "END";
                            //bf.get(id).status = "END";
                        }
                        
                        //Issue++;
                        return 0;
                    }
            }
            }
        }
        
        
        return 0;
    }

    public static void AntDependenciaCommit(int id){
        boolean vr1 = false;
        boolean vr2 = false;
        String rename = "";

        if (!bf.get(id).rename.equals("")) {vr1 = true; rename = bf.get(id).rename;}
        bf.get(id).rename = "";

        for (int i = 0; i < id; i++) {
            if (bf.get(i).status.equals("END") || bf.get(i).status.equals("COMMIT")) {
                // Dependências indiretas
                
                if (bf.get(id).r0.equals(bf.get(i).r1) || bf.get(id).r0.equals(bf.get(i).r2)) {
                    // bf.get(id).rename = "";
                }
            }else if(!bf.get(i).status.equals("IGNORE")){
                if (bf.get(id).r0.equals(bf.get(i).r1) || bf.get(id).r0.equals(bf.get(i).r2)) {
                    bf.get(id).rename = rename;
                }
            }
        }
        if (bf.get(id).rename.equals("")) {vr2 = true;}

        if (vr1 && vr2) {
            re.Retomada(rename, bf.get(id));
        }
    }

    public static void commit(){
        for (int i = 0; i < bf.size(); i++) {
            AntDependenciaCommit(i);
            if (bf.get(i).status.equals("END")) {
                if (bf.get(i).rename.equals("")) {
                    bf.get(i).status = "COMMIT";
                }
            }
        }
    }


    // Responsável pelo organização de chamados
    public static void step(){
        atribuir();
        
        for (int i = 0; i < VecIntruction.length+2; i++) {
            commit();
            Find();
            Find();
            Find();
            Find();
            Find();
            Find();
            
            Despacho();
            AtzCycle();
            Utilitario.PrintBuffer(bf);
            System.out.println("=================----------------------------------------------------------------===================================");
            Utilitario.printMatriz(printReserv("MULT"));
            System.out.println();
            
            Cycle++;
        }
        
    }

    public static String[][] printReserv(String op){
        String ALU[][] = new String[bf.size()][8];
        String Mem[][] = new String[bf.size()][8];
        String Mult[][] = new String[bf.size()][8];
        String br[][] = new String[bf.size()][8];

        for(int i = 0; i < bf.size(); i++){
            if (bf.get(i).status.equals("Not-Use")) {
                if (bf.get(i).op.equals("ADD") || bf.get(i).op.equals("SUB") ) {
                    ALU[i][0] = bf.get(i).op;
                    ALU[i][1] = bf.get(i).r0;
                    ALU[i][2] = bf.get(i).r1;
                    ALU[i][3] = bf.get(i).r2;
                    ALU[i][4] = bf.get(i).status;
                    ALU[i][5] = bf.get(i).rename;
                    ALU[i][6] = "" + bf.get(i).issue;
                    ALU[i][7] = "" +bf.get(i).result;
                }
                if (bf.get(i).op.equals("MULT") || bf.get(i).op.equals("DIV") ) {
                    Mult[i][0] = bf.get(i).op;
                    Mult[i][1] = bf.get(i).r0;
                    Mult[i][2] = bf.get(i).r1;
                    Mult[i][3] = bf.get(i).r2;
                    Mult[i][4] = bf.get(i).status;
                    Mult[i][5] = bf.get(i).rename;
                    Mult[i][6] = "" + bf.get(i).issue;
                    Mult[i][7] = "" +bf.get(i).result;
                }
                if (bf.get(i).op.equals("LOAD") || bf.get(i).op.equals("STORE") ) {
                    Mem[i][0] = bf.get(i).op;
                    Mem[i][1] = bf.get(i).r0;
                    Mem[i][2] = bf.get(i).r1;
                    Mem[i][3] = bf.get(i).r2;
                    Mem[i][4] = bf.get(i).status;
                    Mem[i][5] = bf.get(i).rename;
                    Mem[i][6] = "" + bf.get(i).issue;
                    Mem[i][7] = "" +bf.get(i).result;
                }
                if (bf.get(i).op.equals("BNE") || bf.get(i).op.equals("BEQ") ) {
                    br[i][0] = bf.get(i).op;
                    br[i][1] = bf.get(i).r0;
                    br[i][2] = bf.get(i).r1;
                    br[i][3] = bf.get(i).r2;
                    br[i][4] = bf.get(i).status;
                    br[i][5] = bf.get(i).rename;
                    br[i][6] = "" + bf.get(i).issue;
                    br[i][7] = "" +bf.get(i).result;
                }
            }
        }
        if (op.equals("ALU")) {
            return ALU;
        }else if (op.equals("MEM")) {
            return Mem;
        }else if (op.equals("MULT")) {
            return Mult;
        }else{
            return br;
        }
        

    }

    // Main
    public static void main(String[] args) {
        step();
        Utilitario.PrintBuffer(bf);
        System.out.println("==========================================================================================");
        Utilitario.PrintFuncs(tabelaFuncional);

        
    }

}