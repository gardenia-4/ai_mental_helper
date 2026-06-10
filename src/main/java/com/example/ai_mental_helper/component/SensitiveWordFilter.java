package com.example.ai_mental_helper.component;

import cn.hutool.dfa.WordTree;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 12 & 13. DFA 敏感词前缀树安全过滤网关
 */
@Component
public class SensitiveWordFilter implements InitializingBean {

    // 全局唯一驻留内存的 DFA 前缀树对象
    private final WordTree wordTree = new WordTree();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 12. 初始化时将高危词汇喂给 DFA 树
        List<String> sensitiveWords = Arrays.asList(
                "自残", "自杀", "不想活了", "跳楼", "割腕", "喝药",
                "炸弹", "海洛因", "反动", "毒品", "兼职刷单"
        );

        // 喂给前缀树
        wordTree.addWords(sensitiveWords);
        System.out.println("============= DFA 心理高危敏感词库初始化成功！ =============");
    }

    /**
     * 13. 检测文本中是否包含敏感词
     */
    public boolean hasSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return wordTree.isMatch(text);
    }

    /**
     * 13. 文本脱敏方法：找出一整棵树匹配到的所有敏感词，并将其手工高能替换为 *
     */
    public String filterText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // 1. 使用当前版本绝对支持的 matchAll 找出文本中所有的敏感词汇
        java.util.List<String> matchWords = wordTree.matchAll(text, -1, true, true);
        if (matchWords == null || matchWords.isEmpty()) {
            return text; // 没命中敏感词，原样放行
        }

        // 2. 依次遍历命中的敏感词，把它们洗成星号
        for (String word : matchWords) {
            // 根据敏感词的实际长度生成对应数量的 *
            char[] stars = new char[word.length()];
            java.util.Arrays.fill(stars, '*');

            // 全局替换文本中的该敏感词
            text = text.replace(word, new String(stars));
        }

        return text;
    }
}