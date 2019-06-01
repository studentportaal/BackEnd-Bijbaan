package models.dto;

import java.util.Date;

public class ReviewDto {
    private String author;
    private String target;
    private String content;
    private Date postDate;
    private int stars;
    private boolean isWritten;

    public ReviewDto(String author, String target) {
        this.author = author;
        this.target = target;
        this.content = "";
        this.stars = 1;
        this.isWritten = false;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public boolean isWritten() {
        return isWritten;
    }

    public void setWritten(boolean written) {
        isWritten = written;
    }
}
