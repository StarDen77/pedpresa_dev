package ua.com.pedpresa.src;

public class Sites {
    private int id;
    private String siteName;
    private String url;
    private String classFilter;
    private String trimFilter;
    private String postFilter;
    private String canonical;
    private int idLastPost;

    public Sites() {
    }

    public Sites(int id, String siteName, String url, String classFilter, String trimFilter, String postFilter, String canonical, int idLastPost) {
        this.id = id;
        this.siteName = siteName;
        this.url = url;
        this.classFilter = classFilter;
        this.trimFilter = trimFilter;
        this.postFilter = postFilter;
        this.canonical = canonical;
        this.idLastPost = idLastPost;
    }

    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getClassFilter() {
        return classFilter;
    }

    public void setClassFilter(String classFilter) {
        this.classFilter = classFilter;
    }

    public String getTrimFilter() {
        return trimFilter;
    }

    public void setTrimFilter(String trimFilter) {
        this.trimFilter = trimFilter;
    }

    public String getPostFilter() {
        return postFilter;
    }

    public void setPostFilter(String postFilter) {
        this.postFilter = postFilter;
    }

    public int getIdLastPost() {
        return idLastPost;
    }

    public void setIdLastPost(int idLastPost) {
        this.idLastPost = idLastPost;
    }

    @Override
    public String toString() {
        return "Sites{" +
                "id=" + id +
                ", siteName='" + siteName + '\'' +
                ", url='" + url + '\'' +
                ", classFilter='" + classFilter + '\'' +
                ", trimFilter='" + trimFilter + '\'' +
                ", postFilter='" + postFilter + '\'' +
                ", canonical='" + canonical + '\'' +
                ", idLastPost='" + idLastPost + '\'' +
                '}';
    }
}
