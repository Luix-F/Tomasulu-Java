public class linhaBF {
    public String rename = "";

    public float result;

    public String op;
    public String r0;
    public String r1;
    public String r2;

    public int issue = -1;
    public String status = "Not-Use";

    public int NeedCycle;
    public int StartCycle;
    public int EndCycle;

    public int pcAtual;

    public boolean speculative;

    public void del(){
        rename = "";

        result = 0;

        op = null;
        r0 = null;
        r1 = null;
        r2 = null;

        issue = -1;
        status = "Not-Use";

        NeedCycle = -1;
        StartCycle = -1;
        EndCycle = -1;

        pcAtual = -1;
    }
}
