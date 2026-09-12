package com.mesh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mesh.entity.MeshNode;
import com.mesh.entity.MeshNode.NodeRole;

public interface MeshNodeRepository
        extends JpaRepository<MeshNode, Long> {

    List<MeshNode> findByRole(NodeRole role);
}
