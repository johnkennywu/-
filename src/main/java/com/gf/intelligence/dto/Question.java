package com.gf.intelligence.dto;

/**
 * @author wushubiao
 * @Title: QuestionDto
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/9
 */
public class Question {
    private String id;
    private String question;
    private String answer;
    private String keyword;
    private String clicks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getClicks() {
        return clicks;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }
}
