package net.elshaarawy.movies;

/**
 * Created by elshaarawy on 23-Apr-16.
 */
public class Utility {
    public boolean isPopular(String orderType){
        boolean isPopular;
        switch (orderType){
            case "popular":
                isPopular = true;
                break;
            default:
            case "top_rated":
                isPopular=false;
                break;
        }
        return isPopular;
    }
}
