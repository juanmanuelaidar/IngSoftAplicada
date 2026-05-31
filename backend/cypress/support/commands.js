Cypress.Commands.add('loginByApi', (username = 'admin', password = 'admin') => {
  cy.request({
    method: 'POST',
    url: '/api/authenticate',
    body: { username, password },
    failOnStatusCode: false,
  }).then((response) => {
    expect(response.status, 'auth status').to.eq(200);
    expect(response.body.id_token, 'JWT token').to.be.a('string').and.not.be.empty;

    const token = response.body.id_token;
    cy.window().then((win) => {
      win.localStorage.setItem('jhi-authenticationToken', token);
    });

    return cy.wrap(token, { log: false }).as('jwtToken');
  });
});
