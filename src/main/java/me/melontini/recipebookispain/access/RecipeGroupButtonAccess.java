package me.melontini.recipebookispain.access;

public interface RecipeGroupButtonAccess {
    default int getPage() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void setPage(int page) {
        throw new IllegalStateException("Interface not implemented");
    }
}