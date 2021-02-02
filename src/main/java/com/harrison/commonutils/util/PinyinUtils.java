package com.harrison.commonutils.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @Description: 汉字拼音转化工具类
 * @Author: HanYu
 * @Date: 2021/2/2
 **/
@Slf4j
public class PinyinUtils {

    /**
     * 获取中文拼音
     * @param chinese 中文 汉字
     * @return 汉字拼音
     */
    public static String getPinYin(String chinese){
        HanyuPinyinOutputFormat format=new HanyuPinyinOutputFormat();
        // 设置拼音字母的大小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置拼音的音标 不显示音标
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置字母V u
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder builder=new StringBuilder();
        char[] chineseArray=chinese.toCharArray();

        try {
            for (int i = 0; i < chineseArray.length; i++) {
                // 判断是否为汉字字符
                if (Character.toString(chineseArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chineseArray[i], format);
                    // 如果为多音字, 取第一个读音
                    builder.append(pinyinArray[0]);
                } else {
                    // 如果不是汉字字符则直接追加该字符
                    builder.append(Character.toString(chineseArray[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e){
            log.error("汉字拼音格式设置有误");
        }

        return builder.toString();
    }

    /**
     * 获取中文汉字首字母
     * @param chinese 中文 汉字
     * @return 汉字首字母
     */
    public static String getPinYinHead(String chinese){
        StringBuilder builder =new StringBuilder("-");
        for (int i=0;i<chinese.length();i++){
            char word=chinese.charAt(i);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if(pinyinArray != null){
                // 获取第一个读音的第一个字母
                builder.append(pinyinArray[0].charAt(0));
            }else {
                builder.append(word);
            }
        }
        // 设置首字母都大写
        return builder.toString().toUpperCase();
    }
    
}
