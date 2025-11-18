package cn.edu.seig.vibemusic.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomCodeUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 6; // 生成的字符串长度
    private static final Random random = new Random();

    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    /**
     * 生成随机的6个字符的字符串
     *
     * @return 随机字符串
     */
    public static String generateRandomCode() {
        StringBuilder stringBuilder = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }
    /**
     * 生成6位数字验证码
     */
    public static String generateRandomNumberCode() {
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(NUMBERS.length());
            code.append(NUMBERS.charAt(index));
        }
        return code.toString();
    }
}
