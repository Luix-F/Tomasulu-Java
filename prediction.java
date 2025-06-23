public class prediction { // predição de 2 bits (0 - 3)
    public int nivel = 3;

    public boolean last = false;

    public void aumento(){
        if (nivel < 3) {
            nivel++;
        }
    }

    public void diminui(){
        if (nivel > 0) {
            nivel--;
        }
    }

    public boolean desvia(String op){
        if (op.equals("BNE")|| op.equals("BEQ")) {
            if (nivel <= 1) {
                return false;
            }else{
                return true;
            }
        }
        return false;
    }
}