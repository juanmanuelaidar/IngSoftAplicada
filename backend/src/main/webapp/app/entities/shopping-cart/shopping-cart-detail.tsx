import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './shopping-cart.reducer';

export const ShoppingCartDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const shoppingCartEntity = useAppSelector(state => state.shoppingCart.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="shoppingCartDetailsHeading">
          <Translate contentKey="storeApp.shoppingCart.detail.title">ShoppingCart</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{shoppingCartEntity.id}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="storeApp.shoppingCart.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {shoppingCartEntity.createdDate ? (
              <TextFormat value={shoppingCartEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="storeApp.shoppingCart.status">Status</Translate>
            </span>
          </dt>
          <dd>{shoppingCartEntity.status}</dd>
          <dt>
            <Translate contentKey="storeApp.shoppingCart.customer">Customer</Translate>
          </dt>
          <dd>{shoppingCartEntity.customer ? shoppingCartEntity.customer.address : ''}</dd>
        </dl>
        <Button tag={Link} to="/shopping-cart" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/shopping-cart/${shoppingCartEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ShoppingCartDetail;
