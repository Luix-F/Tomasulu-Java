import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.table.*;

public class TomasuloGUI extends JFrame {
    private JTable bufferROBTable, tabelaFuncionalTable, registradoresTable;
    private JTable estacaoLOADSTORETable, estacaoBRTable, estacaoALUTable, estacaoMULTTable;
    private JTable memoriaTable;
    private JLabel ciclosLabel, pcLabel, ipcLabel, bolhaLabel;
    private JButton btnNextCycle, btnReset, btnCarregar;

    // Modelos das tabelas
    private DefaultTableModel bufferROBModel, tabelaFuncionalModel, registradoresModel;
    private DefaultTableModel estacaoLOADSTOREModel, estacaoBRModel, estacaoALUModel, estacaoMULTModel;
    private DefaultTableModel memoriaModel;

    public TomasuloGUI() {
        setTitle("Simulador Tomasulo - GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 800); // um pouco mais largo para comportar todas as abas
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Painel superior de métricas e controles
        JPanel metricPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ciclosLabel = new JLabel("Ciclos: 0");
        pcLabel = new JLabel("PC: 0");
        ipcLabel = new JLabel("IPC: 0.00");
        bolhaLabel = new JLabel("Bolhas: 0");

        metricPanel.add(ciclosLabel);
        metricPanel.add(Box.createHorizontalStrut(20));
        metricPanel.add(pcLabel);
        metricPanel.add(Box.createHorizontalStrut(20));
        metricPanel.add(ipcLabel);
        metricPanel.add(Box.createHorizontalStrut(20));
        metricPanel.add(bolhaLabel);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCarregar = new JButton("Carregar Instruções");
        btnNextCycle = new JButton("Próximo Ciclo");
        btnReset = new JButton("Resetar");
        controlPanel.add(btnCarregar);
        controlPanel.add(btnNextCycle);
        controlPanel.add(btnReset);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(metricPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Painel central com abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Buffer de Reordenação (ROB)
        bufferROBModel = new DefaultTableModel();
        bufferROBTable = new JTable(bufferROBModel);
        bufferROBTable.setDefaultRenderer(Object.class, new ROBSpecRenderer());
        tabbedPane.add("Buffer de Reordenação (ROB)", new JScrollPane(bufferROBTable));

        // Tabela Funcional (pipeline)
        tabelaFuncionalModel = new DefaultTableModel();
        tabelaFuncionalTable = new JTable(tabelaFuncionalModel);
        tabbedPane.add("Tabela Funcional", new JScrollPane(tabelaFuncionalTable));

        // Registradores
        registradoresModel = new DefaultTableModel();
        registradoresTable = new JTable(registradoresModel);
        tabbedPane.add("Registradores", new JScrollPane(registradoresTable));

        // Estações de Reserva - ALU
        estacaoALUModel = new DefaultTableModel();
        estacaoALUTable = new JTable(estacaoALUModel);
        tabbedPane.add("Estação ALU", new JScrollPane(estacaoALUTable));

        // Estações de Reserva - MULT
        estacaoMULTModel = new DefaultTableModel();
        estacaoMULTTable = new JTable(estacaoMULTModel);
        tabbedPane.add("Estação MULT", new JScrollPane(estacaoMULTTable));

        // Estações de Reserva - LOAD/STORE
        estacaoLOADSTOREModel = new DefaultTableModel();
        estacaoLOADSTORETable = new JTable(estacaoLOADSTOREModel);
        tabbedPane.add("Estação LOAD/STORE", new JScrollPane(estacaoLOADSTORETable));

        // Estações de Reserva - BRANCH
        estacaoBRModel = new DefaultTableModel();
        estacaoBRTable = new JTable(estacaoBRModel);
        tabbedPane.add("Estação BRANCH", new JScrollPane(estacaoBRTable));

        // Memória de Dados
        memoriaModel = new DefaultTableModel();
        memoriaTable = new JTable(memoriaModel);
        tabbedPane.add("Memória de Dados", new JScrollPane(memoriaTable));

        add(tabbedPane, BorderLayout.CENTER);

        // Listeners dos botões
        btnNextCycle.addActionListener(e -> proximoCiclo());
        btnReset.addActionListener(e -> resetarSimulador());
        btnCarregar.addActionListener(e -> carregarArquivo());

        // Inicializa simulador e exibe
        simulador.atribuir();
        
        atualizarTabelas();
    }

    private void proximoCiclo() {
        simulador.commit();
        simulador.Find();
        simulador.Despacho();
        simulador.AtzCycle();
        simulador.Cycle++;
        atualizarTabelas();
        atualizarMetricas();
    }

    private void resetarSimulador() {
        simulador.Cycle = 0;
        simulador.pc = 0;
        simulador.Issue = 0;
        simulador.atribuir();
        simulador.Find();
        atualizarTabelas();
        atualizarMetricas();
    }

    // Permite carregar arquivo de instrução (opcional)
    private void carregarArquivo() {
        JFileChooser chooser = new JFileChooser();
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                simulador.VecIntruction = Utilitario.instructLoad(file.getAbsolutePath());
                simulador.Cycle = 0;
                simulador.pc = 0;
                simulador.Issue = 0;
                simulador.atribuir();
                simulador.Find();
                atualizarTabelas();
                atualizarMetricas();
            }
        }
    }

    private String[][] copyWithoutNullRows(String[][] matrix) {
        // Contar linhas não nulas
        int tam = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0] != null) {
                tam++;
            }
        }
        
        // Criar nova matriz com tamanho adequado
        String[][] result = new String[tam][8];
        
        int jk = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0] != null) {
                result[jk] = matrix[i];
                jk++;
            }
        }
        
        return result;
    }

    private void atualizarTabelas() {
        // Buffer ROB
        String[][] rob = getBufferROBMatrix();
        String[] robHeader = {"#", "PC", "OP", "R0", "R1", "R2", "Rename", "Status", "Spec", "Issue", "Result"};
        bufferROBModel.setDataVector(rob, robHeader);

        // Tabela Funcional
        String[][] tf = getTabelaFuncionalMatrix();
        String[] tfHeader = {"Ciclo", "INTEGER0", "INTEGER1", "MULT", "MEM", "BR"};
        tabelaFuncionalModel.setDataVector(tf, tfHeader);

        // Registradores
        String[][] regs = getRegistradoresMatrix();
        String[] regHeader = {"Nome", "Valor"};
        registradoresModel.setDataVector(regs, regHeader);

        // Estação ALU
        String[][] alu = copyWithoutNullRows(simulador.printReserv("ALU"));
        String[] aluHeader = {"OP", "R0", "R1", "R2", "Status", "Rename", "Issue", "Result"};
        estacaoALUModel.setDataVector(alu, aluHeader);

        // Estação MULT
        String[][] mult = copyWithoutNullRows(simulador.printReserv("MULT"));
        String[] multHeader = {"OP", "R0", "R1", "R2", "Status", "Rename", "Issue", "Result"};
        estacaoMULTModel.setDataVector(mult, multHeader);

        // Estação LOAD/STORE (juntas!)
        String[][] loadstore = copyWithoutNullRows(simulador.printReserv("MEM"));
        String[] loadstoreHeader = aluHeader;
        estacaoLOADSTOREModel.setDataVector(loadstore, loadstoreHeader);

        // Estação BRANCH
        String[][] br = copyWithoutNullRows(simulador.printReserv("BR"));
        String[] brHeader = aluHeader;
        estacaoBRModel.setDataVector(br, brHeader);

        // Memória de Dados
        String[][] memoria = getMemoriaMatrix();
        String[] memoriaHeader = {"Endereço", "Valor"};
        memoriaModel.setDataVector(memoria, memoriaHeader);
    }

    private void atualizarMetricas() {
        // Cálculo das métricas: IPC e bolhas
        int ciclos = simulador.Cycle;
        int commits = 0;
        for (linhaBF l : simulador.bf) if ("COMMIT".equals(l.status)) commits++;
        double ipc = ciclos > 0 ? (double)commits / ciclos : 0.0;
        int bolhas = ciclos - commits; // ciclos sem commit

        ciclosLabel.setText("Ciclos: " + ciclos);
        pcLabel.setText("PC: " + simulador.pc);
        ipcLabel.setText("IPC: " + String.format("%.2f", ipc));
        bolhaLabel.setText("Bolhas: " + bolhas);
    }

    // ---------- MÉTODOS DE CONVERSÃO PARA TABELAS ----------

    private String[][] getBufferROBMatrix() {
        int size = simulador.bf.size();
        String[][] mat = new String[size][11];
        for (int i = 0; i < size; i++) {
            linhaBF l = simulador.bf.get(i);
            mat[i][0] = String.valueOf(i);
            mat[i][1] = String.valueOf(l.pcAtual);
            mat[i][2] = l.issue + ":" + (l.op != null ? l.op : "");
            mat[i][3] = l.r0 != null ? l.r0 : "";
            mat[i][4] = l.r1 != null ? l.r1 : "";
            mat[i][5] = l.r2 != null ? l.r2 : "";
            mat[i][6] = l.rename != null ? l.rename : "";
            mat[i][7] = l.status != null ? l.status : "";
            mat[i][8] = l.speculative ? "Sim" : "";
            mat[i][9] = String.valueOf(l.issue);
            mat[i][10] = String.valueOf(l.result);
        }
        return mat;
    }

    private String[][] getTabelaFuncionalMatrix() {
        int maxC = simulador.Cycle + 2;
        int units = simulador.tabelaFuncional[0].length;
        String[][] mat = new String[maxC][units + 1];
        for (int i = 0; i < maxC; i++) {
            mat[i][0] = String.valueOf(i);
            for (int j = 0; j < units; j++) {
                linhaBF l = simulador.tabelaFuncional[i][j];
                String conteudo = "";
                if (l != null && l.op != null && !"Not-Use".equals(l.status)) {
                    conteudo = l.op + "(" + 
        (l.r0 != null ? l.r0 : "") + ", " + 
        (l.r1 != null ? l.r1 : "") + ", " + 
        (l.r2 != null ? l.r2 : "") + ") - " + 
        (l.status != null ? l.status : "");
                }
                mat[i][j + 1] = conteudo;
            }
        }
        return mat;
    }

    private String[][] getRegistradoresMatrix() {
        String[][] regs = Register.Registradores;
        if (regs == null) return new String[0][0];
        String[][] out = new String[regs.length][2];
        for (int i = 0; i < regs.length; i++) {
            out[i][0] = regs[i][0];
            out[i][1] = regs[i][1];
        }
        return out;
    }

    // Estação de reserva: filtra buffer pelo tipo de operação (para ALU, LOAD/STORE, BRANCH)
    private String[][] getEstacaoReservaMatrix(String tipo) {
        java.util.List<String[]> linhas = new java.util.ArrayList<>();

        for (int i = 0; i < simulador.bf.size(); i++) {
            linhaBF l = simulador.bf.get(i);
            if (tipo.equals("ALU") && ("ADD".equals(l.op) || "SUB".equals(l.op) || "MULT".equals(l.op) || "DIV".equals(l.op)))
                linhas.add(toEstacaoRow(i, l));
            else if (tipo.equals("LOADSTORE") && ("LOAD".equals(l.op) || "STORE".equals(l.op)))
                linhas.add(toEstacaoRow(i, l));
            else if (tipo.equals("BR") && ("BNE".equals(l.op) || "BEQ".equals(l.op)))
                linhas.add(toEstacaoRow(i, l));
        }
        return linhas.toArray(new String[linhas.size()][]);
    }

    private String[] toEstacaoRow(int idx, linhaBF l) {
        String[] row = new String[10];
        row[0] = String.valueOf(idx);
        row[1] = String.valueOf(l.pcAtual);
        row[2] = l.op != null ? l.op : "";
        row[3] = l.r0 != null ? l.r0 : "";
        row[4] = l.r1 != null ? l.r1 : "";
        row[5] = l.r2 != null ? l.r2 : "";
        row[6] = l.rename != null ? l.rename : "";
        row[7] = l.status != null ? l.status : "";
        row[8] = l.speculative ? "Sim" : "";
        row[9] = String.valueOf(l.issue);
        return row;
    }

    // Memória de Dados
    private String[][] getMemoriaMatrix() {
        String[] mem = Register.Memoria;
        if (mem == null) return new String[0][0];
        int max = Math.min(mem.length, 64); // Mostra só os 64 primeiros endereços por padrão
        String[][] out = new String[max][2];
        for (int i = 0; i < max; i++) {
            out[i][0] = String.valueOf(i);
            out[i][1] = mem[i];
        }
        return out;
    }

    // ---- Renderizador do ROB para destacar especulativas ----
    private static class ROBSpecRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String espec = (String) table.getModel().getValueAt(row, 8); // coluna "Spec"
            if ("Sim".equals(espec)) {
                c.setBackground(new Color(255, 255, 180));
                setToolTipText("Instrução especulativa (será descartada se o desvio errar)");
            } else {
                c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                setToolTipText(null);
            }
            return c;
        }
    }

    // --------- MAIN ---------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TomasuloGUI gui = new TomasuloGUI();
            gui.setVisible(true);
        });
    }
}