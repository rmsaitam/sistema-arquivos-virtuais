package com.saitam.sistemas_arquivos_virtuais_api.controller;

import com.saitam.sistemas_arquivos_virtuais_api.model.Node;
import com.saitam.sistemas_arquivos_virtuais_api.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {
    @Autowired
    private NodeService nodeService;

    @GetMapping
    public List<Node> getAllNodes() {
        List<Node> nodes =  nodeService.getAllNodes();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(nodes).getBody();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNodeById(@PathVariable Long id) {
        Optional<Node> node = nodeService.getNodeById(id);

        if(node.isPresent()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(node.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"Node not found\"}");
    }

    @GetMapping("/parent/{parentId}")
    public List<Node> getNodesByParentId(@PathVariable Long parentId) {
        List<Node> nodes = nodeService.getNodesByParentId(parentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(nodes).getBody();
    }

    @PostMapping
    public ResponseEntity<Node> createNode(@RequestBody Node node) {
        Node createNode = nodeService.createNode(node);
        if(createNode.getId() == null) {
            throw new RuntimeException("ID node not generate");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createNode);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Node> updateNode(@PathVariable Long id, @RequestBody Node nodeDetails) {
        Node updatedNode = nodeService.updateNode(id, nodeDetails);
        return updatedNode != null ? ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedNode)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        boolean isDeleted = nodeService.deleteNode(id);

        if(isDeleted) {
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }


}
