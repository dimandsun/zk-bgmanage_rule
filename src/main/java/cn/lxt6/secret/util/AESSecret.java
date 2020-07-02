package cn.lxt6.secret.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * aes加密-解密
 */
public class AESSecret {
    private final static Logger logger = LoggerFactory.getLogger("sys_error");

	/**
     * 加密--把加密后的byte数组先进行二进制转16进制在进行base64编码
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String sKey){
        String tempStr = "";
        byte[] bytes = null;
        try {
            if (sKey == null) { //密钥不能为空
                throw new IllegalArgumentException("Argument sKey is null.");
            }
            if (sKey.length() < 16) { //密钥长度必须为16位
                throw new IllegalArgumentException("Argument sKey'length is not 16.");
            }
            byte[] raw = sKey.getBytes("ASCII"); //将密钥以ASCII转成二进制
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES/ECB/PKCS7Padding");//生成加密密钥对象

            // java 解密需要加上此方法 bcprov-jdk15-143.jar
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");//创建AES加密对象
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);//初始化AES加密对象
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));//进行AES加密
            tempStr = parseByte2HexStr(encrypted);//将AES加密后的二进制转成十六进制文本
            bytes = tempStr.getBytes("UTF-8");
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);//将数据Base64加密

    }

    public static String simpleDecrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new IllegalArgumentException("Argument sKey is null.");
        }
        if (sKey.length() < 16) {
            throw new IllegalArgumentException("Argument sKey'length is not 16.");
        }

        byte[] raw = sKey.getBytes("ASCII"); //获取密钥ASCII编码的二进制
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES");//生成AES解密对象
        cipher.init(Cipher.DECRYPT_MODE, skeySpec); //初始AES解密对象
        byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);//将解密的字符串Base64解密
        byte[] original = cipher.doFinal(encrypted1);//AES解密
        String originalString = new String(original, "utf-8");//界面结果转UTF-8编码文本
        return originalString;
    }

    /**
     * 解密--先 进行base64解码，在进行16进制转为2进制然后再解码
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String sKey){
        try {
            if (sKey == null) {
                logger.error("Argument sKey is null.");
            }
            if (sKey.length() < 16) {
                logger.error("Argument sKey'length is not 16.");
            }
            byte[] raw = new byte[0]; //获取密钥ASCII编码的二进制
            raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");//生成AES解密对象
            cipher.init(Cipher.DECRYPT_MODE, skeySpec); //初始AES解密对象
            byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);//将解密的字符串Base64解密
            String tempStr = new String(encrypted1, "utf-8");//Base64解密后取UTF-8编码文本
            encrypted1 = parseHexStr2Byte(tempStr); //十六进制转二进制
            byte[] original = cipher.doFinal(encrypted1);//AES解密
            return new String(original, "utf-8");//界面结果转UTF-8编码文本
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    
    
    
    public static void main(String[] args) {
        /*请求授权服务器的参数：code=NUExNUEyMzlDNjVGOEQ2ODIzQkE2QkFCN0NFNEZDNTBDRkUwMjY3NjY5QzAzNTgyRUQ4Mzc0MTMyMDE1NzE4NTA0NEU4QjNDNDg5NEMxQTREMjdBOTY0RkMzNTdCODNFODEwNERFMDU2MUVDNjQ3NjhEMTlFMTRCRjY3
        NTE2NzVFRUZCQjk2ODlCQzQzNzg0QUI2QjM5NTE4NDBCRjlDRDRBNURDQzdGRTNFN0YwNDJBQkU0N0FGQjQ0OEM3OENGNzU1QjA0N0I3RTFCMkU4MDA2QkIzQTAwQ0Q0RDBEODUyQjM0Nzc5QTkzMUFERjBDMEExNTQzNUQzQUVCQTI3MTk2MjZERkM4Q0IxMDc1RUQz
        QTE2Q0ZGOEFBMkVDRDA2MkZFQ0U5OEVGMkE3N0EyMzFEMjAxMUI1RTgzOTQ2RTE=&sign=94b8837f90d4ce6059e19d34686f973e&sid=00000000000001*/

    	String code = "machineid=0863920030682970";
        String code2 = "studentid=1#?machineid=1901120100000009#?nbid=868221041516397#?channel_type=2#?studentid=00067E1F#?cardid=00000000#?random=U28BJS";
        String code3 = "id=1";
        String code4 = "studentid=140000000000245501#?schoolid=1#?password=234234#?clienttype=1#?mobile=17722861850#?typeid=11#?rand=U28BJS";
        String code5 = "QUE5NTlCMjZGRUVCNzhDMENBOTYzNTk3QTQ4NkU2ODA1MTdGODY1RjkyMzNFMEZENUQ5MjY4MEE0NUM3Mjg4MTY2QkY3M0I1QTkxNjRDOTFGQTY5RDI4M0FGNzBCNkE1QkJGRDFERkRDQUIyQUExQTI1NkYwNDM3QzIxRTMzODkwQzQzODFEMTU0QTk5NzE2Qzg0MzNBRDFEQjFFOTc2OTAzRjA3MDY4N0NBREVBN0I5NDM0QkMyRkE5MEFEQkExNjBDM0FDQzhERkVFNTRDRjIyQ0UxQzNBRjE3OTRBM0I2MUY1MjMxQkQ0MTIyMzFCRTQ5RkYyMzQ5NzU3MEU3M0Y3MDQ4NUVDMzNEMTExOTc3MDZBMUNCRkVCQUU3MjQ2QUU4NERCNzA0NDJDQzEwQ0RGMUI2RERFNzY0NDM3QzcyRkZBNjg0RkUwNTIyMTg0OEMyODU3NURENjVERDM3ODM2RTA2QUMzQzg2MTMzRThFNkREODMwQzVFQTUzRUJE";
        try {
			//System.out.println("解密CODE:"+decrypt(code, "ykt_2017_@%020_WZQITSTYUIJhd0802"));
            //System.out.println(DataHandleToolXJ.getSign(code));
//            String newCode = encrypt(code3,"test_lxt_00000000000000000000001");
              String newCode = encrypt(code,"ykt_2017_@%020_WZQITSTYUIJhd0802");
              String newSign = null;//DataHandleToolXJ.getSign(newCode);
              System.out.println("code="+newCode+"&sign="+newSign+"&sid=140000000000000001&requestType=2");
//            System.out.println(newCode);

//            System.out.println("加密CODE:"+ decrypt(code2,"ykt_2017_@%020_WZQITSTYUIJhd0802"));
            System.out.println("解密CODE:"+ decrypt("MDZBQUFDRDY3NjRGQTdFOTc2ODU1OUU4RTFDMjVGMTA1NTI3NzZGNjBCRDQ4NDU5MkJGMTRDOEQ0QUQ4RkRFMjQyNjM2Q0U0NzVFMDJDNEI5NzFGODc3NTU1NERBOEE0NjNGOTZFQzcyNUE2QTcxRTY3RDA1ODNBMDU1MzU5NUIwMDZEMkExRjAxNEFBOTk4NEFCNERGNTM0NTc0QTIyRDkxRjVFRjFCOTBDMjA2MzNDOTYwNjk0RDhBNDJGNzJCNDJEOUNBNjMzN0M4Q0NBQTIzNjBCQTQ2NjY5RTdFNjI2QzZCNThFQzgzNjAzQkQzMEJFNEJBRjEyNDVBN0ZEQ0Y1RjNCOTlDQzY0QkE0QkUyQkVDM0JBNzIxNjZCNUVCQzVDOTFBQUU2QjBFNzU4RkY1RjEyNDM0QzhDRDFEOUYyOTlFNzBBMjZDNzRGMjIwMDYyMEY2QjdFRDAyRjk3MTRCMkQ4NDA1NTA1RjFDOUU0NjRDN0Y3QzI1MzY5ODQ3NTYzMzc5QjY2NkVDMURBMEE3NENFQzhBQkMzQjFBODUzNUFERERFNDVGNERFQUQ4MjRFRUFDNzU2REY3M0RBODY0RDI1OEE3RkZBNzlGRDVGRDk5RTI3OTMxQzEyMTg5RTQ4QzE3MjUzNkJGM0UyMjhEMkNENDhCRDRFNjMxMzU1OTg2QjI0MTlBNUI0REFBOUZBMDg4RTgxNjM1NjI4MEM4RUZFQUJGRDMxMDlFNEIxODM1MzQxQjJBNEJGM0ZDMzhDNzk2MjM3REIzNkUwNTgxNDczREYyREMwQTlDQTcyRTk1Qzc5ODM3Q0EwNDhDNkM2NDMwQ0Q5REQ2QkYwMjMwNTkzQTEyM0VERDM3MDQyNkVCMDcyNUY2MUU4MEMzRDNCRDk0ODQxNkFDODJCMzkyNEMyQUQ2QTFDQ0U5OUIzMjVGRjVFNDU2MTEzN0UwMUM1RkU2OEM1NDJFODkyNDMyOTFEN0M1NUExMkE0MjBGMEQ5ODYzRkQ1MjYzMEJFMUQxMTA4MzNBNzJDODUzMkI3OUNDNTNEOTA2MzQzN0YyMTlEODA3MEZBMkI1NkQzRjAzQjk4NDYxRTQ4MEE4NDZGNEVBMjhDNkM0MDk5OEU0Rjg2MzcxN0IyMkQ4NTRBNkY2Q0U1MzRFNUU4QzE1NkZDMDU2MEMyM0RDQTA5OENGOEZGOUFFOUQyQjM2QjM3OTBEOTE5NTg5OERDQzNEODVBMDVCNEQ4OEIxRDZCRDFENjgyMUZDRENCMTM5QTYxMTIwOUZDOTdDMDM1M0I4OTE5MjFGQUU4M0IxRDJEMjVCOUM3RjRGNDNCOThDQkM2NTAyM0U1Q0MyNTIyQTNFNDVDNTgxMzk1MkFGMTUwQzlDREM1OTRGMDc2NTRCOUJCNjA1NDc0Q0EwQzc4NTUzMzA3QTBCQjdDQUU4QUMwRkFBQjUzRDlDQjAyRUVGQzFBQUQzMDQ0NERGMUYzMEIyM0FFMTU4OTM0MDQ3NEM1OTgyMzU1RTBBMjExNTIyOEY2RUNFQjMxMzk3NDFDNjA1M0IzOTA2RUY2NDFFQjE5M0UyNERBRDJBQTQzN0E5RjhEM0I0MDBEMzRDN0MwQzgyQ0U4ODYxN0VBNUQ5NjAxOUM2MTI5N0Q3RDYxREJDQ0JCRjQ2MTk5MzQ5RUEzOThCMDkxRTk5MDFCQjM3QkI2MjA0RjVCNEVGQ0JFRTFFOTVGRDc1NDY5RDhFQTJEQ0Y0RkREMDg3ODgxRTlFRUEwMTc5QkY3QUI0RkFFREJBMEE3QjE2NzUzOEUzNjU3QTkyRTAzOUMwMTYxQjk2QTUwM0MzMDUwQUExMzVFQ0ZDMTY5Qjg1QjA3MjM0RDY5NkI4M0E3NjBEQkUzN0YzQ0UzQTU2MzFGQUMwNjIyMDcxNzQ3MEM0NkJDRkZDQzkyOENDREU4RTIxNzIwMUI0NTcwNjI5MTI5MTM5RTUwODE3NDc1M0I5NDE4NzU5MzEyMERCOUVERTNBNUREODNEQ0QwOUFERUQ4QzM0NjFBOTA1QUY2MzQ2MkM0ODdBODE5NjhEOUI4Qzg2NjQ4QzAyNDk3QThBMTM3RTBFQTNCNzI5Qzk1RkE1RTY1RDlEMjg3MUI0OTMzOUM2RTg3RUJDMDJCMzUzQjIwQjUzQTA2NUUwMDNBRjkxMEY5QjdDNjg4QzNBOTE0QzBBMDhDRjYwRUYyMDMyQTBEOERDM0NFQkE5ODg1QURFOEFGM0VBOUYzRjlBMDQxM0ZEOTA2ODQ5NkE0M0Y2MjdGMTgwRDAzMUE2RTRBRTREREM1QkI2QkYxRjhDNzZFRDQ2N0VBMjFEN0NCOURCNTlFRjM3MzlCQTg1RTdDRkZCOERDOTM5OUE1REM2NDY1NTY2NTNEQUUyOTRBRDdBRDIzQTgzOEE2MjI4QkEy","ykt_2017_@%020_WZQITSTYUIJhd0802"));
//            System.out.println("加密SIGN:"+newSign);

        } catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
