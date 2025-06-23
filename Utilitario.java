import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilitario{
    List<String> list22 = List.of("Item V1", "Item V2", "Item V3");

    public static  String[][] instructLoad(String filePath){
        // Caminho do arquivo de entrada
        //String filePath = "./instruct.luix";

        // Lista para armazenar as instruções
        ArrayList<String> instructions = new ArrayList<>();

        // Ler o arquivo
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    instructions.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            String[][] vazio = new String[1][1];
            vazio[0][0] = "-1";
            return vazio;
        }

        // Criar matriz para armazenar as instruções separadas
        String[][] matrix = new String[instructions.size()][];

        // Processar cada instrução
        for (int i = 0; i < instructions.size(); i++) {
            // Separar por vírgula e remover espaços em branco
            matrix[i] = Arrays.stream(instructions.get(i).split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
        }
        return matrix;
    }

    /*public static List<String> copiarVetorParaLista(String[] vetor) {
        List<String> a = List.of(vetor);

        return a;
    }*/

    public static void PrintBuffer(List<linhaBF> bf){
        System.out.println("Buffer de Reordenamento");
        for (linhaBF linhaBF : bf) {
            System.out.println(linhaBF.op + " " + linhaBF.rename + " " + linhaBF.r0 + " " + linhaBF.r1 + " " + linhaBF.r2 + " " + linhaBF.issue + " " + linhaBF.status + " " + linhaBF.result);
        }
    }

    public static void PrintFuncs(linhaBF tabelaFuncional[][]){
        System.out.println("Tabelas Funcionais");
        int cy = 0;
        for (linhaBF[] linhaBFs : tabelaFuncional) {
            if (linhaBFs[0] != null) {
                System.out.println("Cycle: " + cy);
                cy++;
                for (linhaBF lFB : linhaBFs) {
                    System.out.println(lFB.op + " " + lFB.rename + " " + lFB.r0 + " " + lFB.r1 + " " + lFB.r2 + " " + lFB.issue + " " + lFB.status + " ");
                }
                System.out.println("==========================================================================================");
            }
            
        }
    }

    public static void printMatriz(String[][] a){
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + "  ");
            }
            System.out.println("");
        }
    }

    public static List<String> copiarVetorParaLista(String[] vetor) {
        List<String> a = new ArrayList<>(List.of(vetor));
        for (int i = 0; i < vetor.length; i++) {
            a.set(i,i+": "+vetor[i]);
        }

        return a;
    }

    public static String LinhaUnica(String[] vetor){
        StringBuilder s= new StringBuilder();
        for (int i = 0; i < vetor.length; i++) {
            s.append(vetor[i] + "  ");
        }
        return s.toString();
    }


    public static List<String> copiarLinhasParaLista(String[][] matriz) {
        List<String> list22 = new ArrayList<>();

        for (String[] linha : matriz) {
            StringBuilder linhaComoString = new StringBuilder();
            for (int i = 0; i < linha.length; i++) {
                linhaComoString.append(linha[i]);
                if (i < linha.length - 1) {
                    linhaComoString.append(", "); // separador entre colunas
                }
            }
            list22.add(linhaComoString.toString());
        }

        return list22;
    }


}
