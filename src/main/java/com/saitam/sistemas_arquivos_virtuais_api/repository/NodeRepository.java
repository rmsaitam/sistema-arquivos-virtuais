package com.saitam.sistemas_arquivos_virtuais_api.repository;

import com.saitam.sistemas_arquivos_virtuais_api.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Long> {
        List<Node> findByParentId(Long parentId);
    }
