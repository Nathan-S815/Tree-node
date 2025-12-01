package com.example.treenode.controller;

import com.example.treenode.dto.CreateNodeRequest;
import com.example.treenode.dto.MoveNodeRequest;
import com.example.treenode.dto.NodeDto;
import com.example.treenode.entity.NodeEntity;
import com.example.treenode.repository.NodeRepository;
import com.example.treenode.service.NodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author Nathan
 */
@RestController
@RequestMapping(value = "/api/nodes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins = {"http://127.0.0.1:7900", "http://localhost:5500", "file://"})
public class NodeController {
    private final NodeService service;
    private final NodeRepository repo;

    public NodeController(NodeService service, NodeRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    /**
     * Retrieve all nodes in the system.
     *
     * @return List of all NodeEntity objects.
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<NodeEntity> listAll() {
        return repo.findAll();
    }

    /**
     * Create a new child node under the specified parent node.
     *
     * @param parentId ID of the parent node.
     * @param req Request body containing the name of the new node.
     * @return The created NodeEntity with HTTP 201 Created, or 404 if parent does not exist.
     */
    @PostMapping(value = "/{parentId}/children", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<NodeEntity> createChild(@PathVariable("parentId") Long parentId, @RequestBody CreateNodeRequest req) {
        Optional<NodeEntity> p = repo.findById(parentId);
        if (p.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        NodeEntity created = service.createChild(parentId, req.getName());
        return ResponseEntity.created(URI.create("/api/nodes/" + created.getId())).body(created);
    }

    /**
     * Delete a child node along with its entire subtree.
     * Ensures that the specified child belongs to the given parent node.
     *
     * @param parentId ID of the parent node.
     * @param childId ID of the child node to delete.
     * @return HTTP 204 No Content on success, 404 if parent or child not found, 400 if parent-child mismatch.
     */
    @DeleteMapping("/{parentId}/children/{childId}")
    public ResponseEntity<Void> deleteChild(@PathVariable("parentId") Long parentId, @PathVariable Long childId) {
        Optional<NodeEntity> p = repo.findById(parentId);
        if (p.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<NodeEntity> c = repo.findById(childId);
        if (c.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (c.get().getParentId() == null || !c.get().getParentId().equals(parentId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        service.deleteSubtree(childId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Move a node to a new parent node.
     *
     * @param nodeId ID of the node to move.
     * @param req Request containing the new parent ID (can be null to set as root).
     * @return The moved NodeEntity on success, or 400 Bad Request with error message.
     */
    @PutMapping("/{nodeId}/move")
    public ResponseEntity<?> moveNode(@PathVariable("nodeId") Long nodeId, @RequestBody MoveNodeRequest req) {
        try {
            NodeEntity moved = service.moveNode(nodeId, req);
            return ResponseEntity.ok(moved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Retrieve all descendant nodes of a specified node, sorted by depth.
     *
     * @param id ID of the root node.
     * @return List of NodeDto representing all descendants with depth information,
     *         or 404 Not Found if the root node does not exist.
     */
    @GetMapping("/{id}/descendants")
    public ResponseEntity<List<NodeDto>> getDescendants(@PathVariable("id") Long id) {
        Optional<NodeEntity> root = repo.findById(id);
        if (root.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<NodeDto> list = service.getDescendants(id);
        return ResponseEntity.ok(list);
    }
}
