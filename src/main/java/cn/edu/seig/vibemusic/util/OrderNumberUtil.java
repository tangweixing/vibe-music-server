package cn.edu.seig.vibemusic.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单号生成工具（时间戳+随机数，确保唯一）
 */
public class OrderNumberUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    public static String generateOrderNo() {
        // 1. 时间戳（14位：年月日时分秒）
        String time = LocalDateTime.now().format(FORMATTER);
        // 2. 随机数（6位，避免并发重复）
        String random = String.format("%06d", RANDOM.nextInt(1000000));
        return "PAY" + time + random; // 前缀区分订单类型
    }
}