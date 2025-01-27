package org.jellyfin.androidtv.data.model;

import org.jellyfin.apiclient.model.querying.ItemFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterOptions {
    private boolean favoriteOnly;
    private boolean unwatchedOnly;
    private boolean noneEmptyOnly;

    public void setFavoriteOnly(boolean value) {
        favoriteOnly = value;
    }

    public void setUnwatchedOnly(boolean value) {
        unwatchedOnly = value;
    }

    public void setNoneEmptyOnly(boolean value) {
        noneEmptyOnly = value;
    }

    public boolean isUnwatchedOnly() {
        return unwatchedOnly;
    }

    public boolean isFavoriteOnly() {
        return favoriteOnly;
    }

    public boolean isNoneEmptyOnly() {
        return noneEmptyOnly;
    }

    public ItemFilter[] getFilters() {
        if (!unwatchedOnly && !favoriteOnly) return null;

        List<ItemFilter> filters = new ArrayList<>();
        if (favoriteOnly) filters.add(ItemFilter.IsFavorite);
        if (unwatchedOnly) filters.add(ItemFilter.IsUnplayed);

        return filters.toArray(new ItemFilter[0]);
    }
}
