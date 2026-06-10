package com.example;

import com.example.ai_mental_helper.AiMentalHelperApplication; // 1. 确保导入了启动类
import com.example.ai_mental_helper.component.SensitiveWordFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 2. 核心修复：在这里显式指定类的全路径，让测试环境顺藤摸瓜加载所有 Bean
@SpringBootTest(classes = AiMentalHelperApplication.class)
class AiMentalHelperApplicationTests {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Test
    void testSensitiveFilter() {
        // 保持你原有的测试代码不变
        String rawText = "今天心情好差，遇到了兼职刷单骗局，感觉不想活了，真想去跳楼。";
        System.out.println("【原始文本】: " + rawText);

        boolean hasSensitive = sensitiveWordFilter.hasSensitiveWord(rawText);
        System.out.println("【是否包含敏感词】: " + hasSensitive);

        String filteredText = sensitiveWordFilter.filterText(rawText);
        System.out.println("【脱敏后文本】: " + filteredText);
    }
}