package com.example.treenode.dto;

import lombok.Data;
import lombok.Getter;


/**
 * @author Nathan
 */
@Getter
public class NodeDto {
    private Long id;
    private String name;
    private Integer depth;

    public NodeDto() {}
    public NodeDto(Long id, String name, Integer depth) {
        this.id = id; this.name = name; this.depth = depth;
    }

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setDepth(Integer depth) { this.depth = depth; }
}
