package newsaggregator.data.article;

public class TableData extends ArticleData implements Comparable<TableData> {
    public ArticleLink link;

    public TableData(ArticleData data) {
        super(data.getUrl(), data.getWeb_url(), data.getType(), data.getDescription(), data.getTitle(), data.getContent(), data.getCreate_date(), data.getTag(), data.getAuthor());
        this.link = new ArticleLink(data.getUrl(), this);
    }

    public ArticleLink getLink() {
        return link;
    }

    public void setLink(ArticleLink link) {
        this.link = link;
    }

    @Override
    public int compareTo(TableData o) {
        return o.getCreate_date().compareTo(this.getCreate_date());
    }
}