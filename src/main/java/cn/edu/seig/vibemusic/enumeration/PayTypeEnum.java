// 支付方式枚举
package cn.edu.seig.vibemusic.enumeration;

import lombok.Getter;

@Getter
public enum PayTypeEnum {
    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝支付");

    private final Integer type;
    private final String desc;

    PayTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static PayTypeEnum getByType(Integer type) {
        for (PayTypeEnum e : values()) {
            if (e.type.equals(type)) return e;
        }
        return null;
    }
}
