package com.saitam.sistemas_arquivos_virtuais_api.service;

import com.saitam.sistemas_arquivos_virtuais_api.model.Node;
import com.saitam.sistemas_arquivos_virtuais_api.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NodeService {
    @Autowired
    private NodeRepository nodeRepository;

    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    public Optional<Node> getNodeById(Long id) {
        return nodeRepository.findById(id);
    }

    public List<Node> getNodesByParentId(Long parentId) {
        return nodeRepository.findByParentId(parentId);
    }

    public Node createNode(Node node) {
        return nodeRepository.save(node);
    }

    public Node updateNode(Long id, Node nodeDetails) {
        Node node = nodeRepository.findById(id).orElseThrow(() -> new RuntimeException("Node not found"));

        node.setName(nodeDetails.getName());
        node.setType(nodeDetails.getType());
        node.setSize(nodeDetails.getSize());
        node.setParent(nodeDetails.getParent());

        return nodeRepository.save(node);
    }

    public boolean deleteNode(Long id) {
        nodeRepository.deleteById(id);
        return false;
    }
}
