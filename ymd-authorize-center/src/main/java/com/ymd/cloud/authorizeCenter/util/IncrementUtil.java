package com.ymd.cloud.authorizeCenter.util;

import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.UUIDUtil;

//增量数据工具
public class IncrementUtil {
	public static final String PWD_TABLE="A5A26BD211E8EC7DE5893175095AC550D094EB9BC5595EA088CE41C557E065DAD9558CD608E7AB4E80B4E42DD0325C670693691DA8199DB7A19E96BA6A48D9AD507EAA226777BD9A5177AA419A941BE7ECAA567A61DAA2026277437B470D0222893AE17146087415CB24133DCE18B6A2E4C14C855D05CE4504E17EB792DB1E8ABA75A6E547967A43ACA6A5D502DB5E109CC6B09DBB14D213B13747833B0A0328D67A431E1070E6AEB94B431E06B32341AB8BA650196A0D0338275BC525CB08AB55A589E0E9EA1247365A27584BD0A03C22670D5A8055255136904033582CE4796B60E32DB2C946603636567C883BDE60B2EB1E6C618D58EAC51399C89C7D316E";
    //获取秘钥
    public static String getEncryptKey(long time,String gateMac){
        String off= UUIDUtil.GetMD5Code(gateMac+time);
        String key= getGatePwdByTable(off,PWD_TABLE);
        return key;
    }
	private static String getGatePwdByTable(String param,String pwdTable){
        StringBuilder sb=new StringBuilder();
        String[] arr1=new String[16];
        String[] arr2=new String[256];
        for(int j=0;j<param.length()/2;j++){
            String temp=substring(param, j*2,(j+1)*2);
            arr1[j]=temp;
        }
        for(int k=0;k<pwdTable.length()/2;k++){
            String temp=substring(pwdTable, k*2,(k+1)*2);
            arr2[k]=temp;
        }

        for(int i=0;i<arr1.length;i++){
            int k=convertToDemicalSystem(arr1[i]);
            sb.append(arr2[k]);
        }
        return sb.toString();
    }
    private static  String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }
    //十六进制转换十进制
    private static Integer convertToDemicalSystem(String str){
        return Integer.parseInt(str,16);
    }
    public static String binaryToHexString(byte[] bytes) {
        if(EmptyUtil.isEmpty(bytes)){
            return null;
        }
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            // result += hex + " ";
            result+=hex;
        }
        return result;
    }
    //低位补零
    public static String  createZeroByString( String data){
        int length=data.length();
        int differ=32-(length%32);
        if(differ==0||differ==32){
            return data;
        }else{
            StringBuilder builder=new StringBuilder();
            builder.append(data);
            for(int i=0;i<differ;i++){
                builder.append("0");
            }
            return builder.toString();
        }

    }
}
