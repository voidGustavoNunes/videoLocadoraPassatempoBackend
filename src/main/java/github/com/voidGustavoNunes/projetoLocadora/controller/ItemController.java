package github.com.voidGustavoNunes.projetoLocadora.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import github.com.voidGustavoNunes.projetoLocadora.model.Classe;
import github.com.voidGustavoNunes.projetoLocadora.model.Cliente;
import github.com.voidGustavoNunes.projetoLocadora.model.Item;
import github.com.voidGustavoNunes.projetoLocadora.model.Titulo;
import github.com.voidGustavoNunes.projetoLocadora.model.dto.ItemDTO;
import github.com.voidGustavoNunes.projetoLocadora.repository.TituloRepository;
import github.com.voidGustavoNunes.projetoLocadora.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequestMapping("/itens")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private TituloRepository tituloRepository;

    @GetMapping
    public List<ItemDTO> listar() {
        List<Item> itens = itemService.listar();
        List<ItemDTO> itensDto = new ArrayList();

        for(Item item: itens){
            itensDto.add(mapToDTO(item));
        }
        return itensDto;
    }

    @GetMapping("/{id}")
    public ItemDTO buscarPorId(@PathVariable Long id) {
        ItemDTO itemDto = new ItemDTO();
        
        Item item = itemService.buscarPorId(id);
        itemDto = mapToDTO(item);
        return itemDto;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Item criar(@RequestBody @Valid ItemDTO itemDTO) {
        Item item = mapToEntity(itemDTO);
        return itemService.criar(item);
    }

    @PutMapping("/{id}")
    public Item atualizar(@PathVariable Long id, @RequestBody @Valid ItemDTO itemDTO) {
        Item item = mapToEntity(itemDTO);
        return itemService.atualizar(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        itemService.excluir(id);
    }


    private Item mapToEntity(ItemDTO itemDTO) {
        Item item = new Item();
        item.setId(itemDTO.getId());
        item.setNumeroSerie(itemDTO.getNumeroSerie());
        item.setDataAquisicao(itemDTO.getDataAquisicao());

        Titulo titulo = tituloRepository.findById(itemDTO.getTituloId())
                .orElseThrow(() -> new IllegalArgumentException("Título não encontrado para o ID fornecido."));
        item.setTitulo(titulo);

        item.setTipo(itemDTO.getTipo());
        return item;
    }

    private ItemDTO mapToDTO(Item item){
        ItemDTO itemDto = new ItemDTO();
        itemDto.setId(item.getId());
        itemDto.setNumeroSerie(item.getNumeroSerie());
        itemDto.setDataAquisicao(item.getDataAquisicao());
        itemDto.setTipo(item.getTipo());
        itemDto.setTituloId(item.getTitulo().getId());

        return itemDto;
    }

    @Operation(summary = "Todos os itens", description = "Método que gera uma lista de itens")
    @GetMapping("/itens")
    public List<Item> getItens() {
        return itemService.getAllItems();
    }

    // Endpoint para obter o título associado a um item pelo ID do título
    @GetMapping("/{id}/titulo")
    public Titulo getTituloByItemId(@PathVariable Long id) {
        return itemService.getTituloByItemId(id);
    }

    // Endpoint para obter a classe associada ao título pelo ID da classe
    @GetMapping("/titulos/{id}/classe")
    public Classe getClasseByTituloId(@PathVariable Long id) {
        return itemService.getClasseByTituloId(id);
    }

    // Endpoint para obter a classe associada a um item diretamente
    @GetMapping("/{id}/classe")
    public Classe getClasseByItemId(@PathVariable Long id) {
        return itemService.getClasseByItemId(id);
    }

    // @GetMapping("/itens/count")
    // public Long getItemCount(@RequestParam Long tituloId) {
    //     return itemService.getItemCountByTituloId(tituloId);
    // }
}
