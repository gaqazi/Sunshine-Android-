package net.elshaarawy.movies;

/**
 * Created by elshaarawy on 25-Mar-16.
 */
public class MovieEntity {

    private int page;

    private String imgURL;
    private String overView;
    private String releaseDate;
    private String title;

    private String voteCount;
    private String voteAverage;

    public MovieEntity(int page, String imgURL, String overView, String releaseDate, String title, String voteCount, String voteAverage) {
        this.page = page;

        this.imgURL = imgURL;

        this.overView = overView;

        this.releaseDate = releaseDate;

        this.title = title;

        this.voteCount = voteCount;

        this.voteAverage = voteAverage;


    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public int getPage() {

        return page;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getOverView() {
        return overView;
    }

    public String getTitle() {
        return title;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
