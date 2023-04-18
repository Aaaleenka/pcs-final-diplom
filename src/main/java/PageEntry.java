import java.util.List;

public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {
        int compareResult = pdfName.compareTo(o.pdfName);
        if (compareResult == 0) {
            compareResult = Integer.compare(page, o.page);
        }
        return compareResult;
    }

    @Override
    public String toString() {
        return pdfName + " " + page + " " + count;
    }
}
