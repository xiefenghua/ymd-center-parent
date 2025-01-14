package com.ymd.cloud.authorizeCenter.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardNo {
    /**
     * @param: [content]
     * @return: int
     * @description: 十六进制转十进制
     */
    public static long has16hxTo10(String content){
        long number=0;
        content = content.toUpperCase();
        String [] HighLetter = {"A","B","C","D","E","F"};
        Map<String,Integer> map = new HashMap<>();
        for(int i = 0;i <= 9;i++){
            map.put(i+"",i);
        }
        for(int j= 10;j<HighLetter.length+10;j++){
            map.put(HighLetter[j-10],j);
        }
        String[]str = new String[content.length()];
        for(int i = 0; i < str.length; i++){
            str[i] = content.substring(i,i+1);
        }
        for(int i = 0; i < str.length; i++){
            number += map.get(str[i])*Math.pow(16,str.length-1-i);
        }
        return number;
    }
    public static String reverseCardNo(String cardNo) {
        List<String> charList=new ArrayList<>();
        int startSplitlength=0;
        for (int i = 0; i < cardNo.length(); i++) {
            if(i%2==0&&i!=0) {
                charList.add(cardNo.substring(startSplitlength,i));
                startSplitlength=i;
            }
            if(i==cardNo.length()-1){
                charList.add(cardNo.substring(startSplitlength));
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = charList.size()-1; i >=0; i--) {
            sb.append(charList.get(i));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(CardNo.has16hxTo10(CardNo.reverseCardNo("232db2c6")));
    }
}
