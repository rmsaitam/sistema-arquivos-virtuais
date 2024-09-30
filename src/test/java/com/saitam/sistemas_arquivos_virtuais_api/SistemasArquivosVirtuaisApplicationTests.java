package com.saitam.sistemas_arquivos_virtuais_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saitam.sistemas_arquivos_virtuais_api.model.Node;
import com.saitam.sistemas_arquivos_virtuais_api.model.NodeType;
import com.saitam.sistemas_arquivos_virtuais_api.service.NodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SistemasArquivosVirtuaisApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private NodeService nodeService;

	@Autowired
	private ObjectMapper objectMapper;



	@Test
	void contextLoads() {
	}

	@Test
	public void testCreatDirectory() throws  Exception {
		Node directoryNode = new Node();

		directoryNode.setId(1L);
		directoryNode.setName("newDir");
		directoryNode.setType(NodeType.DIRECTORY);

		when(nodeService.createNode(directoryNode)).thenReturn(directoryNode);

		String newDirectoryJson = objectMapper.writeValueAsString(directoryNode);

		mockMvc.perform(post("/api/nodes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(newDirectoryJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("newDir"))
				.andExpect(jsonPath("$.type").value("DIRECTORY"));


	}

	@Test
	public void testCreateFileInDirectory() throws Exception {
		Node parentDirectory = new Node();
		parentDirectory.setId(1L);
		parentDirectory.setName("parentDir");
		parentDirectory.setType(NodeType.DIRECTORY);

		Node fileNode = new Node();
		fileNode.setId(2L);
		fileNode.setName("file1.txt");
		fileNode.setType(NodeType.FILE);
		fileNode.setParent(parentDirectory);

		when(nodeService.createNode(any(Node.class))).thenAnswer(invocation -> {
			Node node = invocation.getArgument(0);
			if (node.getName().equals("parentDir")) {
				node.setId(1L);
			} else if (node.getName().equals("file1.txt")) {
				node.setId(2L);
				node.setParent(parentDirectory);
			}
			return node;
		});

		String parentDirectoryJson = objectMapper.writeValueAsString(parentDirectory);
		String responseDirectory = mockMvc.perform(post("/api/nodes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(parentDirectoryJson))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		String parentId = objectMapper.readTree(responseDirectory).get("id").asText();

		String newFile = "{ \"name\": \"file1.txt\", \"type\": \"FILE\", \"parent\": { \"id\": " + parentId + " } }";
		mockMvc.perform(post("/api/nodes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(newFile))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("file1.txt"))
				.andExpect(jsonPath("$.type").value("FILE"))
				.andExpect(jsonPath("$.parent.id").value(parentId));
	}

	@Test
	public void testGetNodes() throws Exception {
		mockMvc.perform(get("/api/nodes")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	public void testUpdateNode() throws Exception {
		Node existingNode = new Node();
		existingNode.setId(1L);
		existingNode.setName("OldName");
		existingNode.setType(NodeType.DIRECTORY);

		Node updatedNode = new Node();
		updatedNode.setId(1L);
		updatedNode.setName("UpdatedName");
		updatedNode.setType(NodeType.DIRECTORY);

		when(nodeService.updateNode(eq(1L), any(Node.class))).thenReturn(updatedNode);

		String updateNodeJson = objectMapper.writeValueAsString(updatedNode);

		mockMvc.perform(put("/api/nodes/1")  // ID do nó existente
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateNodeJson))
				.andExpect(status().isOk())  // Verifica se o status é 200 OK
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("UpdatedName"))
				.andExpect(jsonPath("$.type").value("DIRECTORY"));
	}

	@Test
	public void testUpdateNodeNotFound() throws Exception {
		Node nodeDetails = new Node();
		nodeDetails.setName("UpdatedName");
		nodeDetails.setType(NodeType.DIRECTORY);

		when(nodeService.updateNode(eq(999L), any(Node.class))).thenReturn(null);

		String updateNodeJson = objectMapper.writeValueAsString(nodeDetails);

		mockMvc.perform(put("/api/nodes/999")  // ID de nó inexistente
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateNodeJson))
				.andExpect(status().isNotFound());
	}


	@Test
	public void testDeleteNodeSuccess() throws Exception {
		Long existingNodeId = 1L;

		when(nodeService.deleteNode(existingNodeId)).thenReturn(true);

		mockMvc.perform(delete("/api/nodes/{id}", existingNodeId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

	}

	@Test
	public void testDeleteNodeNotFound() throws Exception {
		Long nonExistentNodeId = 999L;

		when(nodeService.deleteNode(nonExistentNodeId)).thenReturn(false);

		mockMvc.perform(delete("/api/nodes/{id}", nonExistentNodeId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());


	}

}
