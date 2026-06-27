describe('API login', () => {
  // Verifica que el login contra /api/authenticate devuelve un JWT valido
  // y que la app lo guarda en localStorage para mantener la sesion.
  it('should login with /api/authenticate and store token', () => {
    cy.visit('/');
    cy.loginByApi('admin', 'admin');

    cy.window().then(win => {
      const savedToken = win.localStorage.getItem('jhi-authenticationToken');
      // eslint-disable-next-line @typescript-eslint/no-unused-expressions
      expect(savedToken).to.be.a('string').and.not.be.empty;
    });

    cy.get('@jwtToken').should('be.a', 'string');
  });
});
