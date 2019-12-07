import java.io.UnsupportedEncodingException;

//
// This algorithm finds all occurrences of a pattern in a text in linear time.
// Let length of text be n and of subline be m, then total time taken is O(m + n)
// with linear space complexity.
//
class SubLine {

    //
    // finds and prints index where subline in text
    //
    public static int[] search(String text, String subline) throws UnsupportedEncodingException
    {
        text = new String(text.getBytes(), "utf-8");
        subline = new String(subline.getBytes(), "utf-8");

        String concat = subline + "$" + text;

        int l = concat.length();

        int Z[] = new int[l]; int res[] = new int[l];

        getZarr(concat, Z);

        int j = 1;
        for(int i = 0; i < l; ++i){

            if(Z[i] == subline.length()){
                res[j] =  i - subline.length() - 1; j++;

            }

        }
        res[0] = j; int cap = 0;
        return res;
    }

    //
    // Fills Z array for given string str[] 
    // An element Z[i] of Z array stores length of the longest substring starting
    // from str[i] which is also a prefix of str[0..n-1].
    //
    private static void getZarr(String str, int[] Z) {

        int n = str.length();

        int L = 0, R = 0;

        for(int i = 1; i < n; ++i) {
            if(i > R){
                L = R = i;

                while(R < n && str.charAt(R - L) == str.charAt(R))
                    R++;

                Z[i] = R - L;
                R--;

            }
            else {
                int k = i - L;

                if(Z[k] < R - i + 1)
                    Z[i] = Z[k];

                else{
                    L = i;

                    while(R < n && str.charAt(R - L) == str.charAt(R))
                        R++;

                    Z[i] = R - L;
                    R--;
                }
            }
        }
    }

    //
    // Starting point
    //
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        String text = "bla bla bla bla bla";
        String subline = "bla";
        System.out.println(text);
        System.out.println(subline);


        int[] res = search(text, subline);

        for (int i = 1; i < res[0]; i++) {

            System.out.println(res[i]);
        }
    }
} 