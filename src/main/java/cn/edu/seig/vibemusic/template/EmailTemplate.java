package cn.edu.seig.vibemusic.template;

public class EmailTemplate {
    // 推广邮件模板
    public static String getPromotionTemplate(String username, String content, String url) {
        return "<html>" +
               "<body>" +
               "<h3>亲爱的 " + username + "，您好！</h3>" +
               "<p>" + content + "</p>" +
               "<p>点击查看详情：<a href='" + url + "'>立即查看</a></p>" +
               "<p>如有疑问，请联系客服</p>" +
               "</body>" +
               "</html>";
    }
}