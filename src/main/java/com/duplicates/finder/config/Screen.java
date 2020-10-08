package com.duplicates.finder.config;

public enum Screen {

   HOMEPAGE {
        @Override
        public String getFxmlFile() {
            return "/fxml/home.fxml";
        }

        @Override
        public String getTitle() {
            return "Duplicates Finder";
        }
    }
    ;

    public abstract String getFxmlFile();

    public abstract String getTitle();
}
