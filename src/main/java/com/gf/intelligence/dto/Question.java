package com.gf.intelligence.dto;

/**
 * @author wushubiao
 * @Title: QuestionDto
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/9
 */
public class Question {
    private Long id;
    private String question;
    private String answer;
    private String keyword;
    private Long clicks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }
}
