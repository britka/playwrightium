package org.brit.locators;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Data
@Accessors(chain = true)
public class ArialSearchOptions {
    public Boolean checked;
    public Boolean disabled;
    public Boolean exact;
    public Boolean expanded;
    public Boolean includeHidden;
    public Integer level;
    public Object name;
    public Boolean pressed;
    public Boolean selected;

    public ArialSearchOptions setName(String name){
        this.name = name;
        return this;
    }

    public ArialSearchOptions setName(Pattern name){
        this.name = name;
        return this;
    }

}
