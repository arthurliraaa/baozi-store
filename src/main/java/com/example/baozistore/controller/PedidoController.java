package com.example.baozistore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.baozistore.model.Cliente;
import com.example.baozistore.model.Pedido;
import com.example.baozistore.model.Produto;
import com.example.baozistore.repository.ClienteRepository;
import com.example.baozistore.repository.PedidoRepository;
import com.example.baozistore.repository.ProdutoRepository;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private final PedidoRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;

	public PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
			ProdutoRepository produtoRepository) {
		this.pedidoRepository = pedidoRepository;
		this.clienteRepository = clienteRepository;
		this.produtoRepository = produtoRepository;
	}

	// Corpo esperado: {"cliente": {"id": 1}, "produto": {"id": 1}, "quantidade": 3}
	@PostMapping
	public ResponseEntity<Pedido> criar(@RequestBody Pedido pedido) {
		if (pedido.getCliente() == null || pedido.getProduto() == null) {
			return ResponseEntity.badRequest().build();
		}
		// busca o cliente e o produto no banco para devolver o pedido completo
		Cliente cliente = clienteRepository.findById(pedido.getCliente().getId()).orElse(null);
		Produto produto = produtoRepository.findById(pedido.getProduto().getId()).orElse(null);
		if (cliente == null || produto == null) {
			return ResponseEntity.badRequest().build();
		}
		pedido.setId(null);
		pedido.setCliente(cliente);
		pedido.setProduto(produto);
		return ResponseEntity.status(201).body(pedidoRepository.save(pedido));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
		return pedidoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public List<Pedido> listar() {
		return pedidoRepository.findAll();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> apagar(@PathVariable Long id) {
		if (!pedidoRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		pedidoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
