package org.wyeworks.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class Picture {
    private String height;
    private String width;
    private String url;

}
