package ua.com.pedpresa.src;

import java.util.Date;

public class News implements Comparable<News> {
    private int id;
    private String title;
    private Date createdDate;
    private String content;
    private int canonical;
    private String keyWords;
    private String siteName;
    private String url;
    private int isEx;

    public News() {
    }

    public News(int id, String title, Date createdDate, String content, int canonical, String keyWords, String siteName, String url, int isEx) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate;
        this.content = content;
        this.canonical = canonical;
        this.keyWords = keyWords;
        this.siteName = siteName;
        this.url = url;
        this.isEx = isEx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        if (getCanonical() != news.getCanonical()) return false;
        if (!getTitle().equals(news.getTitle())) return false;
        return getCreatedDate().equals(news.getCreatedDate());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getCreatedDate().hashCode();
        result = 31 * result + getCanonical();
        return result;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", createdDate=" + createdDate +
                ", content='" + content + '\'' +
                ", canonical='" + canonical + '\'' +
                ", keyWords='" + keyWords + '\'' +
                ", siteName='" + siteName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCanonical() {
        return canonical;
    }

    public void setCanonical(int canonical) {
        this.canonical = canonical;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsEx() {
        return isEx;
    }

    public void setIsEx(int isEx) {
        this.isEx = isEx;
    }

    @Override
    public int compareTo(News o) {
        return (this.canonical - o.canonical);
    }

}
