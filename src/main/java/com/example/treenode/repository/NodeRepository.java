package com.example.treenode.repository;

import com.example.treenode.entity.NodeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * @author Nathan
 */
public interface NodeRepository extends JpaRepository<NodeEntity, Long> {

    List<NodeEntity> findByParentId(Long parentId);

    // Recursive CTE: Returns id, name, parent_id, depth (excluding root where depth = 0)
    @Query(value =
            "WITH RECURSIVE descendants AS ( " +
                    "  SELECT id, name, parent_id, 0 as depth FROM t_node WHERE id = :rootId " +
                    "  UNION ALL " +
                    "  SELECT c.id, c.name, c.parent_id, d.depth + 1 FROM t_node c JOIN descendants d ON c.parent_id = d.id " +
                    ") SELECT id, name, parent_id, depth FROM descendants WHERE depth > 0 ORDER BY depth, id",
            nativeQuery = true)
    List<Object[]> findDescendantsWithDepth(@Param("rootId") Long rootId);

    @Modifying
    @Transactional
    @Query("DELETE FROM NodeEntity n WHERE n.id IN :ids")
    void deleteAllByIdIn(List<Long> ids);
}
