package sample;

public class YTVideo {
    String name;
    String link;
    FormatEnum format;

    public YTVideo(String name, FormatEnum format, String link) {
        this.name = name;
        this.format = format;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public FormatEnum getFormat() {
        return format;
    }

    public void setFormat(FormatEnum format) {
        this.format = format;
    }
}
