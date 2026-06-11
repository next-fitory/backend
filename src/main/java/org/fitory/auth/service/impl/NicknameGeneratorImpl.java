package org.fitory.auth.service.impl;

import org.springframework.stereotype.Service;
import org.fitory.auth.service.NicknameGenerator;

import java.util.List;
import java.util.Random;

@Service
public class NicknameGeneratorImpl implements NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "빈티지", "오버핏", "슬림", "크롭", "루즈",
            "레트로", "미니멀", "스트릿", "캐주얼", "포멀",
            "올블랙", "모노톤", "그런지", "보헤미안", "아방가르드",
            "언더커버", "로파이", "하이엔드", "올드스쿨", "뉴웨이브"
    );

    private static final List<String> NOUNS = List.of(
            "후디", "트렌치", "데님", "카디건", "블레이저",
            "스니커즈", "로퍼", "첼시부츠", "머플러", "버킷햇",
            "크롭티", "슬랙스", "와이드팬츠", "점퍼", "플리스",
            "니트", "더플코트", "봄버재킷", "테일러드", "바라클라바"
    );

    private final Random random = new Random();

    @Override
    public String generate() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        int number = random.nextInt(9000) + 1000;
        return adjective + "_" + noun + "_" + number;
    }
}
