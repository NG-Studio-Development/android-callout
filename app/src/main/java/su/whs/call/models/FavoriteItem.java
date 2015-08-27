package su.whs.call.models;

import java.io.Serializable;

/**
 * Created by featZima on 10.09.2014.
 */
public class FavoriteItem implements Serializable {

    public int subCategoryId;
    public String subCategoryTitle;
    public int userId;

    public FavoriteItem() { }

    public FavoriteItem(String subCategoryTitle, int subCategoryId, int userId) {
        this.subCategoryTitle = subCategoryTitle;
        this.subCategoryId = subCategoryId;
        this.userId = userId;
    }

}
