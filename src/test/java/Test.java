import com.xzm.AdaptiveHuff;

/**
 * @author xiangzhimin
 * @Description
 * @create 2021-12-30 10:16
 */
public class Test {

    public static void main(String[] args) {

        char [] arr = {'A','B','C','D'};
        int sum = 0;
        for(int i=0;i<10;i++){
            int length = (int) (Math.random() * 100);
            String charList = new String();
            for(int j=0;j<length;j++){
                char c = arr[(int)(Math.random()*4)];
                charList+=c;
            }
            AdaptiveHuff huff = new AdaptiveHuff();
            String run = huff.encode(charList);
            String decode = huff.decode(run);
            if(charList.equals(decode)){
                sum++;
            }else{
                System.out.println("ERROR");
                break;
            }
        }
        if(sum==10){
            System.out.println("Successful.");
        }

    }

}