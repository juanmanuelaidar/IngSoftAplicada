import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Alert, Button, Col, Row } from 'reactstrap';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <div className="store-home">
      <Row className="store-hero align-items-center">
        <Col lg="7">
          <span className="store-kicker">Corralon online</span>
          <h1>Materiales de construccion para gestionar tu obra de punta a punta.</h1>
          <p className="store-lead">
            Catalogo de cementos, ladrillos, aridos, hierros, impermeabilizantes y accesorios para controlar stock, carritos y pedidos.
          </p>
          <div className="store-actions">
            {account?.login ? (
              <>
                <Button tag={Link} to="/product" color="primary">
                  Ver materiales
                </Button>
                <Button tag={Link} to="/shopping-cart" color="secondary" outline>
                  Ver carritos
                </Button>
              </>
            ) : (
              <>
                <Button tag={Link} to="/login" color="primary">
                  Iniciar sesion
                </Button>
                <Button tag={Link} to="/account/register" color="secondary" outline>
                  Crear cuenta
                </Button>
              </>
            )}
          </div>
        </Col>
        <Col lg="5">
          <div className="store-summary" aria-label="Modulos del corralon">
            <div>
              <strong>Materiales</strong>
              <span>Catalogo, precios y stock</span>
            </div>
            <div>
              <strong>Carritos</strong>
              <span>Pedidos en preparacion</span>
            </div>
            <div>
              <strong>Ordenes</strong>
              <span>Seguimiento de compras</span>
            </div>
          </div>
        </Col>
      </Row>

      {account?.login ? (
        <Alert color="success" className="store-alert">
          Sesion iniciada como <strong>{account.login}</strong>. Usa el menu de entidades o los accesos rapidos para administrar el
          corralon.
        </Alert>
      ) : (
        <Alert color="info" className="store-alert">
          Inicia sesion con los usuarios demo para administrar materiales de construccion: admin/admin o user/user.
        </Alert>
      )}

      <Row className="store-shortcuts">
        <Col md="3" sm="6">
          <Link to="/product" className="store-shortcut">
            <strong>Materiales</strong>
            <span>Crea y actualiza productos de obra.</span>
          </Link>
        </Col>
        <Col md="3" sm="6">
          <Link to="/product-category" className="store-shortcut">
            <strong>Rubros</strong>
            <span>Organiza cementos, aridos, hierros y mas.</span>
          </Link>
        </Col>
        <Col md="3" sm="6">
          <Link to="/shopping-cart" className="store-shortcut">
            <strong>Carritos</strong>
            <span>Revisa compras de clientes y obras.</span>
          </Link>
        </Col>
        <Col md="3" sm="6">
          <Link to="/product-order" className="store-shortcut">
            <strong>Ordenes</strong>
            <span>Controla cantidades y entregas.</span>
          </Link>
        </Col>
      </Row>
    </div>
  );
};

export default Home;
