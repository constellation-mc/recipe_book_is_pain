package me.melontini.recipebookispain.access;

public interface RecipeBookWidgetAccess {
    default void updatePages() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void updatePageSwitchButtons() {
        throw new IllegalStateException("Interface not implemented");
    }

    default int getPage() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void setPage(int page) {
        throw new IllegalStateException("Interface not implemented");
    }

    default int getPageCount() {
        throw new IllegalStateException("Interface not implemented");
    }
}