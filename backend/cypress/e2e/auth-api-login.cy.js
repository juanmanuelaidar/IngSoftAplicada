describe('API login', () => {
  it('should login with /api/authenticate and store token', () => {
    cy.visit('/');
    cy.loginByApi('admin', 'admin');

    cy.window().then((win) => {
      const savedToken = win.localStorage.getItem('jhi-authenticationToken');
      expect(savedToken).to.be.a('string').and.not.be.empty;
    });

    cy.get('@jwtToken').should('be.a', 'string');
  });
});
