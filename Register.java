import java.util.List;

public class Register {
    public static String Registradores[][];
    public static String RenomeRegistradores[][];
    public static String Memoria[];

    public void atribuir(){
        Memoria = new String[2048];
        for (int i = 0; i < Memoria.length; i++) {
            Memoria[i] = "" + i;//(rand.nextInt(100));
        }

        String[][] R = {
                // Fixo
                {"$0", "0"}, {"$1", "1"},
                // Retornam valores de funções
                {"$v0", "vazio"}, {"$v1", "vazio"},
                // Argumentos
                {"$a0", "vazio"}, {"$a1", "vazio"},
                {"$a3", "vazio"},
                // Temporários
                {"$t0", "vazio"}, {"$t1", "vazio"},
                {"$t2", "3"}, {"$t3", "vazio"},
                {"$t4", "vazio"}, {"$t5", "vazio"},
                {"$t6", "vazio"}, {"$t7", "vazio"},
                {"$t8", "vazio"}, {"$t9", "vazio"},
                // Temporários salvos
                {"$s0", "vazio"}, {"$s1", "vazio"},
                {"$s2", "vazio"},{"$s3", "vazio"},
                {"$s4", "vazio"},{"$s5", "vazio"},
                {"$s6", "vazio"}, {"$s7", "vazio"},
        };
        Registradores = R;

        String RgR[][] = {// Renomeação
                // Nome, valor, referencia
                {"Ra", "vazio", "vazio"}, {"Rb", "vazio", "vazio"},
                {"Rc", "vazio", "vazio"}, {"Rd", "vazio", "vazio"},
                {"Re", "vazio", "vazio"}, {"Rf", "vazio", "vazio"},
        };
        RenomeRegistradores = RgR;


        for (int i = 2; i < Registradores.length; i++) {
            Registradores[i][1] = "" + i;//(rand.nextInt(100) + 1);
        }
    }

    public String RenomeVazio(String r0){
        for (int i = 0; i < RenomeRegistradores.length; i++) {
            if (RenomeRegistradores[i][2].equals("vazio")) {
                RenomeRegistradores[i][2] = r0;
                return RenomeRegistradores[i][0];
            }
        }
        return "none";
    }

    public void Ignor(linhaBF bf){
        int arr[] = PosicaoRenome(bf.rename, bf.r1, bf.r2);
        RenomeRegistradores[arr[0]][2] = "vazio";
    }

    public void Retomada(String rena, linhaBF bf){
        int arr[] = PosicaoRenome(rena, bf.r1, bf.r2);
        int arr2[] = PosicaoReg(RenomeRegistradores[arr[0]][2], bf.r1, bf.r2);
        Registradores[arr2[0]][1] = RenomeRegistradores[arr[0]][1];
        RenomeRegistradores[arr[0]][2] = "vazio";
    }

    public static int[] PosicaoReg(String r0, String r1, String r2){
        
        int a = 0, b = -1, c = 0;
        for (int j = 0; j < Registradores.length; j++) {
            if (r0.equals(Registradores[j][0]) && j > 1) {
                a = j;
            }
            if (r1.equals(Registradores[j][0])) {
                b = j;
            }
            if (r2.equals(Registradores[j][0])) {
                c = j;
            }
        }
        int arr[] = new int[3];
        arr[0] = a;
        arr[1] = b;
        arr[2] = c;
        return arr;
    }

    public static int[] PosicaoRenome(String r0, String r1, String r2){
        
        int a = 0, b = -1, c = 0;
        for (int j = 0; j < RenomeRegistradores.length; j++) {
            if (r0.startsWith(RenomeRegistradores[j][0]) && j > 1) {
                a = j;
            }
        }
        for (int j = 0; j < Registradores.length; j++) {
            if (r1.equals(Registradores[j][0])) {
                b = j;
            }
            if (r2.equals(Registradores[j][0])) {
                c = j;
            }
        }
        int arr[] = new int[3];
        arr[0] = a;
        arr[1] = b;
        arr[2] = c;
        return arr;
    }
    
    public void SincronizaReg(List<linhaBF> bf){
        for (linhaBF linhaBF : bf) {
            if (linhaBF.status.equals("COMMIT") && !linhaBF.op.equals("BNE") && !linhaBF.op.equals("    BEQ") && !linhaBF.op.equals("BNE")) {
                int arr[] = PosicaoReg(linhaBF.r0, linhaBF.r1, linhaBF.r2);
                Registradores[arr[0]][1] = ""+linhaBF.result;
            }
        }
    }

    public float ExecInsideRenome(linhaBF bf){
        int arr[];
        float v1;
        float v2;

        switch (bf.op) { // Decodeficação
            case "ADD":
                arr = PosicaoRenome(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 + v2;
                RenomeRegistradores[arr[0]][1] = "" + v1;
            return v1;
            case "SUB":
                arr = PosicaoRenome(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 - v2;
                RenomeRegistradores[arr[0]][1] = "" + v1;
            return v1;
            case "MULT":
                arr = PosicaoRenome(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 * v2;
                RenomeRegistradores[arr[0]][1] = "" + v1;
            return v1;
            case "DIV":
                arr = PosicaoRenome(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 / v2;
                RenomeRegistradores[arr[0]][1] = "" + v1;
            return v1;
            case "LOAD":
                arr = PosicaoRenome(bf.r0, "$0", bf.r2);
                v1 = Float.parseFloat(bf.r1);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                v1 = v1 + v2;
                RenomeRegistradores[arr[0]][1] = Memoria[(int) v1];
            break;
            case "STORE":
                arr = PosicaoRenome(bf.r0, "$0", bf.r2);
                v1 = Float.parseFloat(bf.r1);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                v1 = v1 + v2;
                Memoria[(int) v1] = RenomeRegistradores[arr[0]][1];
            break;
            case "BNE":
                arr = PosicaoReg("$0", bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                if (v1 == v2) {
                    return 1;
                }else{
                    return 0;
                }
            case "BEQ":
                arr = PosicaoReg("$0", bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                if (v1 != v2) {
                    return 1;
                }else{
                    return 0;
                }
        }
        return 0;
    }

    public float ExecInside(linhaBF bf){
        int arr[];
        float v1;
        float v2;

        switch (bf.op) { // Decodeficação
            case "ADD":
            
                arr = PosicaoReg(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                

                v1 = v1 + v2;
                Registradores[arr[0]][1] = "" + v1;
            return v1;
            case "SUB":
                arr = PosicaoReg(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 - v2;
                Registradores[arr[0]][1] = "" + v1;
            return v1;
            case "MULT":
                arr = PosicaoReg(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 * v2;
                Registradores[arr[0]][1] = "" + v1;
            return v1;
            case "DIV":
                arr = PosicaoReg(bf.r0, bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);

                v1 = v1 / v2;
                Registradores[arr[0]][1] = "" + v1;
            return v1;
            case "LOAD":
                arr = PosicaoReg(bf.r0, "$0", bf.r2);
                v1 = Float.parseFloat(bf.r1);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                v1 = v1 + v2;
                Registradores[arr[0]][1] = Memoria[(int) v1];
            break;
            case "STORE":
                arr = PosicaoReg(bf.r0, "$0", bf.r2);
                v1 = Float.parseFloat(bf.r1);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                v1 = v1 + v2;
                Memoria[(int) v1] = Registradores[arr[0]][1];
            break;
            case "BNE":
                arr = PosicaoReg("$0", bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                if (v1 != v2) {
                    return 1;
                }else{
                    return 0;
                }
            case "BEQ":
                arr = PosicaoReg("$0", bf.r1, bf.r2);
                v1 = Float.parseFloat(Registradores[arr[1]][1]);
                v2 = Float.parseFloat(Registradores[arr[2]][1]);
                if (v1 == v2) {
                    return 1;
                }else{
                    return 0;
                }
        }
        return 0;
    }
}
