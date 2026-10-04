package service;

import static java.lang.Math.min;

public class PlagiarismService {

    public String normaliseCode(String rawCode){
        if(rawCode == null)return "";
        return rawCode.replaceAll("\\s+", "");
    }

    public double calculateSimilarity(String codeA, String codeB){
        codeA = normaliseCode(codeA);
        codeB = normaliseCode(codeB);
        if(codeA.isEmpty() && codeB.isEmpty()) return 1.0;
        if(codeA.isEmpty() || codeB.isEmpty()) return 0.0;

        int[][] dp = new int[codeA.length() + 1][codeB.length() + 1];

        for(int i = 0; i <= codeA.length(); i++)dp[i][0] = i;
        for(int j = 0; j <= codeB.length(); j++)dp[0][j] = j;

        for(int i  = 1; i <= codeA.length(); i++){
            for(int j = 1; j <= codeB.length(); j++){
                int cost = (codeA.charAt(i-1) != codeB.charAt(j-1)) ? 1 : 0;
                dp[i][j] = min(min(dp[i-1][j] + 1, dp[i][j-1] + 1), dp[i-1][j-1] + cost);
            }
        }

        int editDistance = dp[codeA.length()][codeB.length()];
        int maxLength = Math.max(codeA.length(), codeB.length());

        return (1.0 - ((double)editDistance / maxLength));
    }


}
