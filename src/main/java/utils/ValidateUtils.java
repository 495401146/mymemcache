package utils;

import java.util.regex.Pattern;

public class ValidateUtils {
    //判断一个数是否为数字
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
