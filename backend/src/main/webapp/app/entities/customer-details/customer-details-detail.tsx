import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './customer-details.reducer';

export const CustomerDetailsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const customerDetailsEntity = useAppSelector(state => state.customerDetails.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customerDetailsDetailsHeading">
          <Translate contentKey="storeApp.customerDetails.detail.title">CustomerDetails</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{customerDetailsEntity.id}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="storeApp.customerDetails.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{customerDetailsEntity.phone}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="storeApp.customerDetails.address">Address</Translate>
            </span>
          </dt>
          <dd>{customerDetailsEntity.address}</dd>
          <dt>
            <Translate contentKey="storeApp.customerDetails.user">User</Translate>
          </dt>
          <dd>{customerDetailsEntity.user ? customerDetailsEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/customer-details" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/customer-details/${customerDetailsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CustomerDetailsDetail;
