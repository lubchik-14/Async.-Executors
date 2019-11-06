package response;

public class BingImage {
    private String url;
    private String hsh;

    public BingImage(String url, String hsh) {
        this.url = url;
        this.hsh = hsh;
    }

    public String getUrl() {
        return url;
    }

    public String getHsh() {
        return hsh;
    }
}
