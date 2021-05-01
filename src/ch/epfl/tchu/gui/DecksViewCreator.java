package ch.epfl.tchu.gui;

import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 
 */
class DecksViewCreator {
  /**
   * The class is non-instanciable
   */
  private DecksViewCreator() {}
  
  /**
   * 
   * 
   * @param observableGameState
   * 
   * @return
   */
  public static HBox createHandView(ObservableGameState observableGameState) {
    return null;
  }

  /**
   * 
   * 
   * @param observableGameState
   * 
   * @param drawTicketsHandlerProperty
   * 
   * @param drawCardHandlerProperty
   * 
   * @return
   */
  public static VBox createCardsView(ObservableGameState observableGameState, 
    ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty,
    ObjectProperty<DrawCardHandler> drawCardHandlerProperty) {
      return null;
  }
}
