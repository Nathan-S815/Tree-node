package com.example.treenode.service;

import com.example.treenode.dto.MoveNodeRequest;
import com.example.treenode.dto.NodeDto;
import com.example.treenode.entity.NodeEntity;
import com.example.treenode.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Service layer for managing tree nodes.
 * Provides operations to create, move, delete, and retrieve nodes and their subtrees.
 * Modernized for JDK 21.
 * @author Nathan
 */
@Service
public class NodeService {

    private final NodeRepository repo;

    public NodeService(NodeRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new child node under the specified parent node.
     *
     * @param parentId ID of the parent node.
     * @param name Name of the new child node.
     * @return The newly created NodeEntity.
     */
    public NodeEntity createChild(Long parentId, String name) {
        NodeEntity node = new NodeEntity();
        node.setName(name);
        node.setParentId(parentId);
        return repo.save(node);
    }

    /**
     * Delete a node along with its entire subtree.
     * Descendants are deleted first, followed by the root node itself.
     *
     * @param nodeId ID of the node to delete.
     */
    @Transactional
    public void deleteSubtree(Long nodeId) {
        List<Long> descendantIds = repo.findDescendantsWithDepth(nodeId).stream()
                .map(r -> ((Number) r[0]).longValue())
                .toList();

        if (!descendantIds.isEmpty()) {
            repo.deleteAllByIdIn(descendantIds); // batch delete
        }
        repo.deleteById(nodeId);
    }

    /**
     * Move a node to a new parent node.
     * Ensures the node is not moved to itself or its descendants (avoiding cycles).
     *
     * @param nodeId ID of the node to move.
     * @param req MoveNodeRequest containing the new parent ID (nullable for root).
     * @return The updated NodeEntity after moving.
     * @throws IllegalArgumentException if node not found or invalid move.
     */
    @Transactional
    public NodeEntity moveNode(Long nodeId, MoveNodeRequest req) {
        NodeEntity node = repo.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        Long newParent = req.getNewParentId();

        if (newParent != null) {
            if (newParent.equals(nodeId)) {
                throw new IllegalArgumentException("Cannot move node to itself");
            }
            Set<Long> descendantIds = repo.findDescendantsWithDepth(nodeId).stream()
                    .map(r -> ((Number) r[0]).longValue())
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
            if (descendantIds.contains(newParent)) {
                throw new IllegalArgumentException("Cannot move node to its descendant");
            }
        }

        node.setParentId(newParent);
        return repo.save(node);
    }

    /**
     * Retrieve all descendants of a node, including their depth relative to the root node.
     *
     * @param rootId ID of the root node.
     * @return List of NodeDto representing all descendants with depth information.
     */
    public List<NodeDto> getDescendants(Long rootId) {
        return repo.findDescendantsWithDepth(rootId).stream()
                .map(r -> new NodeDto(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[3]).intValue()
                ))
                .toList();
    }
}
