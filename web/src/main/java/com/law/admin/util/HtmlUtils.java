package com.law.admin.util;

/**
 * HTML 工具類
 */
public class HtmlUtils {

    /**
     * 移除 HTML 標籤並截取摘要
     *
     * @param html   原始 HTML 字串
     * @param maxLen 最大長度
     * @return 純文字摘要
     */
    public static String stripHtml(String html, int maxLen) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String text = html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen) + "…";
    }
}
