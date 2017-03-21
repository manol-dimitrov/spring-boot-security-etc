package com.zamrad.dto.posts;

public enum PostType {
    COLLABORATE("collaborate"),
    FIND_BAND_MEMBERS("find-band-members"),
    SHARE_EQUIPMENT("share-equipment"),
    GENERAL_HELP("general-help"),
    JAM_SESSION("jam-session");

    private String value;

    PostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
