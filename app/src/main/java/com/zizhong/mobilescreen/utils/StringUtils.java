package com.zizhong.mobilescreen.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;


import com.zizhong.mobilescreen.utils.log.LogUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 判断字符串是否为空
     *
     * @param mString
     * @return
     */
    public static Boolean isNullOrBlank(String mString) {
        if (mString != null && !mString.isEmpty()
                && !mString.equals("") && !mString.equals("null")) {
            return false;
        } else {
            LogUtils.i("StringUtils", "StringisNull");
            return true;
        }
    }

    /**
     * 验证身份证号码
     */
    public static boolean isIdCard(String idCard) {
        String pattern = "\\d{15}|\\d{18}";
        boolean result = Pattern.matches(pattern, idCard);
        return result;
    }

    /**
     * 验证输入价格正确
     */
    public static boolean isMoney(String money) {
        String pattern = "^([0-9]+)?(\\.([0-9]{1,2})?)?$";
        boolean result = Pattern.matches(pattern, money);
        return result;
    }

    /**
     * 保留两位小数
     *
     * @param str
     * @return
     */
    public static String decimalFormat(String str) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(Double.parseDouble(str));
    }

    /**
     * 四舍五入
     *
     * @param round 需要改变的数据
     * @param scale 保留小数点后面几位有效数字
     * @return
     */
    public static BigDecimal bigDecimal(String round, int scale) {
        BigDecimal bd = new BigDecimal(round);
        BigDecimal r = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return r;
    }

    /***
     * 判断是否是手机号码类型
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneType(String phone) {
        String pattern = "(^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$)";
        if (isNullOrBlank(phone)) {
            return false;
        }
        boolean result = Pattern.matches(pattern, phone);
        return result;
    }

    /**
     * 验证电话号码和手机号码
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     * <p>
     * 座机验证：
     * 1:区号 + 座机号码
     * 2:400 + 座机号码
     * 3:800 + 座机号码
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNumberType(String phone) {
      // String pattern = "(^0\\d{9,11}$)|(^400\\d{7}$)|(^800\\d{7}$)|(^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$)";
      String pattern = "1[3|4|5|6|7|8|9][0-9]{9}";

        if (isNullOrBlank(phone)) {
            return false;
        }
        boolean result = Pattern.matches(pattern, phone);
        return result;
    }

    /**
     * 电话号码中间的加密
     *
     * @param phone
     * @return
     */
    public static String phoneHiddenCenterNum(String phone) {
        String newPhone = "";
        if (!isNullOrBlank(phone)) {
            newPhone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
        }
        return newPhone;
    }

    /***
     * 判断是否是email类型
     *
     * @param email
     * @return
     */
    public static boolean isEmailType(String email) {
        String pattern = "^[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9_\\-]+\\.([a-zA-Z0-9_\\-]+)+$";
        boolean result = Pattern.matches(pattern, email);
        return result;
    }

    /**
     * 判断字符串是不是邮箱格式
     */
    public static boolean isEmail(String str) {
        return (str.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)" +
                "|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"))
                ? true : false;
    }

    /**
     * 是否为车牌号
     *
     * @param licensePlateNum
     * @return
     */
    public static boolean isCarNumberNO(String licensePlateNum) {
        String carNumRegex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z][A-Z]" +
                "[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]?[A-Z0-9]{4}[A-Z0-9挂学警港澳]$";
        if (licensePlateNum.matches(carNumRegex)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCarNo(String carNo) {
        /*
         车牌号格式：汉字 + A-Z + 5位A-Z或0-9
        （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
         */
        String carNumRegex = "[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}";
        if (isNullOrBlank(carNo)) {
            return false;
        } else {
            return carNo.matches(carNumRegex);
        }
    }

    /**
     * 密码是否含有数字和字母
     *
     * @param string 密码
     * @return
     */
    public static boolean isHasLetterAndDigit(String string) {
        boolean isRight = false;
        if (!isNullOrBlank(string)) {
            boolean isDigit = false;// 定义一个boolean值，用来表示是否包含数字
            boolean isLetter = false;// 定义一个boolean值，用来表示是否包含字母
            for (int i = 0; i < string.length(); i++) {
                if (Character.isDigit(string.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
                    isDigit = true;
                }
                if (Character.isLetter(string.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
                    isLetter = true;
                }
            }
            isRight = isDigit && isLetter;
        }
        return isRight;
    }


    /**
     * 关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text,
                                                     String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * 多个关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字数组
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text,
                                                     String[] keyword) {
        SpannableString s = new SpannableString(text);
        for (int i = 0; i < keyword.length; i++) {
            Pattern p = Pattern.compile(keyword[i]);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }


    /**
     * 集合转String  例：list -> xx,xx,xx,xx
     *
     * @param list
     * @return
     */
    public static String listToString(List<String> list) {
        Object object = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                object = list.get(i);
            } else {
                object = object + "," + list.get(i);
            }
        }
        return (String) object;
    }

    /**
     * 集合转String  例：arr -> xx,xx,xx,xx
     *
     * @param arr
     * @return
     */
    public static String arrToString(String[] arr) {
        String stringArr = "";
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                stringArr = arr[i];
            } else {
                stringArr = stringArr + "," + arr[i];
            }
        }
        return stringArr;
    }

    /**
     * 数组中是否含有某个值
     *
     * @param arr
     * @param indexStr
     * @return
     */
    public static boolean arrHasIndexString(String[] arr, String indexStr) {

        boolean isHas = false;
        for (String string : arr) {
            if (string.equals(indexStr)) {
                isHas = true;
            }
        }

        return isHas;
    }
   /* 判断非空*/
    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0 || str.equals("null"));
    }
}
