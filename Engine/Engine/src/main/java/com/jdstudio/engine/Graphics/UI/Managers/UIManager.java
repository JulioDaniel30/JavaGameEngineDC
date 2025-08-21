package com.jdstudio.engine.Graphics.UI.Managers;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;

/**
 * Gerencia uma coleção de elementos de UI ({@link UIElement}).
 * <p>
 * Esta classe atua como um contêiner para todos os elementos da interface do usuário,
 * simplificando o processo de renderizá-los e gerenciá-los em conjunto.
 *
 * @author JDStudio
 * @since 1.0
 */
public class UIManager {

    /** A lista que armazena todos os elementos de UI gerenciados. */
    private List<UIElement> elements;

    /**
     * Construtor padrão que inicializa o gerenciador de UI.
     * Cria uma nova lista vazia para armazenar os elementos.
     */
    public UIManager() {
        elements = new ArrayList<>();
    }

    /**
     * Adiciona um novo elemento de UI para ser gerenciado e renderizado.
     *
     * @param element O {@link UIElement} a ser adicionado. Não pode ser nulo.
     */
    public void addElement(UIElement element) {
        elements.add(element);
        RenderManager.getInstance().register(element);
    }

    /**
     * Remove um elemento de UI da lista de gerenciamento.
     * O elemento não será mais renderizado.
     *
     * @param element O {@link UIElement} a ser removido.
     */
    public void removeElement(UIElement element) {
        elements.remove(element);
        RenderManager.getInstance().unregister(element);
    }
    
    /**
     * Desregista todos os elementos de UI do RenderManager e limpa a lista interna.
     * Essencial para ser chamado quando um estado de menu é fechado.
     */
    public void unregisterAllElements() {
        for (UIElement element : elements) {
            RenderManager.getInstance().unregister(element);
        }
        elements.clear();
    }
    
    /**
     * Atualiza a lógica de todos os elementos de UI gerenciados.
     * Agora, itera sobre uma cópia da lista para evitar ConcurrentModificationException.
     */
    public void tick() {
        // --- A CORREÇÃO ESTÁ AQUI ---
        // Cria uma cópia da lista de elementos para iterar sobre ela.
        // A lista original (this.elements) pode agora ser modificada com segurança.
        for (UIElement element : new ArrayList<>(this.elements)) {
            if (element.isVisible()) {
                element.tick();
            }
        }
    }

    /**
     * Renderiza todos os elementos de UI visíveis na tela.
     * <p>
     * Este método itera sobre todos os elementos e chama o método {@code render(g)}
     * de cada um que estiver marcado como visível.
     *
     * @param g O contexto {@link Graphics} onde os elementos serão desenhados.
     */
    public void render(Graphics g) {
        for (UIElement element : elements) {
            if (element.isVisible()) {
                element.render(g);
            }
        }
    }
}