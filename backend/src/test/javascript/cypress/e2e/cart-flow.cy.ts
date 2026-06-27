describe('Cart flow', () => {
  // Verifica que un usuario autenticado puede entrar a la pantalla de carritos.
  // La prueba confirma la ruta final y que el encabezado de ShoppingCart se renderiza.
  it('should open shopping cart after API login', () => {
    cy.loginByApi('admin', 'admin');

    cy.visit('/shopping-cart');
    cy.url().should('match', /\/shopping-cart(\?.*)?$/);
    cy.getEntityHeading('ShoppingCart').should('be.visible');
  });
});
