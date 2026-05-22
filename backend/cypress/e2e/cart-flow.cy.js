describe('Cart flow', () => {
  it('should open shopping cart after API login', () => {
    cy.visit('/');
    cy.loginByApi('admin', 'admin');

    cy.visit('/shopping-cart');
    cy.contains(/shopping cart/i).should('be.visible');
  });
});
