package com.enjoytrip.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KmpUtil {

    public static int countMatches(String text, String keyword) { // 텍스트 내 키워드 등장 횟수 확인
        if (text == null || keyword == null || keyword.isEmpty())
            return 0;

        int cnt = 0;
        int lps[] = computeLPS(keyword);
        int textIdx = 0;
        int keywordIdx = 0;

        while (textIdx < text.length()) {
            if (keyword.charAt(keywordIdx) == text.charAt(textIdx)) {
                textIdx++;
                keywordIdx++;
            }

            if (keywordIdx == keyword.length()) {
                cnt++;
                keywordIdx = lps[keywordIdx - 1];
            } else if (textIdx < text.length() && keyword.charAt(keywordIdx) != text.charAt(textIdx)) {
                if (keywordIdx != 0)
                    keywordIdx = lps[keywordIdx - 1];
                else
                    textIdx++;
            }
        }

        return cnt;
    }

    private static int[] computeLPS(String keyword) { // 주어진 키워드에 대해 LPS 배열 구성
        int length = 0;
        int idx = 1;
        int lps[] = new int[keyword.length()];
        lps[0] = 0;

        while (idx < keyword.length()) {
            if (keyword.charAt(idx) == keyword.charAt(length)) {
                length++;
                lps[idx] = length;
                idx++;
            } else {
                if (length != 0)
                    length = lps[length - 1];
                else
                    lps[idx++] = 0;
            }
        }

        return lps;
    }
}