import com.fasterxml.jackson.annotation.JsonProperty;

public class PageEntryJson {

    private String pdfName;
    private int page;
    private int count;

    public PageEntryJson() {
    }

    public PageEntryJson(
            @JsonProperty("Название файла") String pdfName,
            @JsonProperty("Номер страницы") int page,
            @JsonProperty("Кол-во вхождений") int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return
                "pdfName " + pdfName  +
                        "page " + page  +
                        "count " + count;

    }
}
