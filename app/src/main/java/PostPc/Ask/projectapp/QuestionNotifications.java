package PostPc.Ask.projectapp;

public class QuestionNotifications
{
    private boolean newAnswer;
    private boolean newComment;
    private boolean newRating;

    public QuestionNotifications(boolean newAnswer, boolean newComment, boolean newRating)
    {
        this.newAnswer = newAnswer;
        this.newComment = newComment;
        this.newRating = newRating;
    }

    public QuestionNotifications()
    {
        this.newAnswer = true;
        this.newComment = true;
        this.newRating = true;
    }

    public boolean isNewAnswer() {
        return newAnswer;
    }

    public void setNewAnswer(boolean newAnswer) {
        this.newAnswer = newAnswer;
    }

    public boolean isNewComment() {
        return newComment;
    }

    public void setNewComment(boolean newComment) {
        this.newComment = newComment;
    }

    public boolean isNewRating() {
        return newRating;
    }

    public void setNewRating(boolean newRating) {
        this.newRating = newRating;
    }
}
