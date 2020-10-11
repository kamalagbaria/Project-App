package PostPc.Ask.projectapp;

import java.io.Serializable;
import java.util.UUID;

public class Answer implements Serializable {
    private String text;
    private String ownerId;
    private String ownerName;
    private String imageUrl;
    private String key;
    private String answerId;
    private String question_title;

    public Answer(){

    }

    public Answer(String text, String uuid,String imageUrl, String name,String key,String question_title){
        this.text = text;
        this.ownerId = uuid;
        this.imageUrl=imageUrl;
        this.ownerName = name;
        this.key = key;
        this.answerId= UUID.randomUUID().toString();
        this.question_title = question_title;
    }
    public String getText(){
        return text;
    }

    public String getOwnerId(){
        return ownerId;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setOwnerId(String uuid){
        this.ownerId = uuid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }
}
