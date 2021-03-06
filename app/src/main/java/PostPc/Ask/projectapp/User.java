package PostPc.Ask.projectapp;

import java.util.ArrayList;

public class User {

    private ArrayList<Question> myQuestions;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String id;
    private String location;
    private ArrayList<String> lastViewed=new ArrayList<>();
    private ArrayList<Answer> UserAnswers = new ArrayList<>();
    private String phoneNumber;

    public ArrayList<String> getLastViewed() {
        return lastViewed;
    }

    public User(String fullName, String email, String id, String phoneNumber) {
        this.fullName = fullName;
        this.firstName = "";
        this.lastName = "";
        this.email = email;
        this.id = id;
        this.location = "";
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Question> AddQuestion(Question newQuestion){
        this.myQuestions.add(newQuestion);
        return myQuestions;
    }
    public void addLastViewed(String questionKey){
        this.lastViewed.add(0,questionKey);
    }

    public ArrayList<Answer> getUserAnswers() {
        return UserAnswers;
    }
    public void addNewAnswer(Answer answer){
        this.UserAnswers.add(answer);
    }
}
