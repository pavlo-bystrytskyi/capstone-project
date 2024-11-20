import ItemRestricted from "../../../../type/ItemRestricted.tsx";
import {useTranslation} from "react-i18next";
import ItemStatus from "../../../../type/ItemStatus.tsx";
import RegistryConfig from "../../../../type/RegistryConfig.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import axios from "axios";
import ItemIdContainer from "../../../../type/ItemIdContainer.tsx";
import {Col, Form, Row} from "react-bootstrap";
import useToast from "../../../../context/toast/UseToast.tsx";
import ToastVariant from "../../../../context/toast/ToastVariant.tsx";

export default function ItemComponent(
    {
        itemIdContainer,
        config
    }: {
        readonly itemIdContainer: ItemIdContainer,
        readonly config: RegistryConfig
    }
) {
    const {t} = useTranslation();
    const {addToast} = useToast();
    const editStatusAllowed: boolean = config.access.item.status.edit.allowed;
    const [item, setItem] = useState<ItemRestricted>()
    const loadItem = function () {
        axios.get<ItemRestricted>(
            `${config.item.url}/${itemIdContainer[config.item.idField as keyof ItemIdContainer]}`
        ).then(
            (result) => {
                setItem(result.data);
            }
        ).catch(
            (error) => {
                console.error('Error fetching data:', error);
                addToast(t("toast_item_load_failed"), ToastVariant.ERROR);
            }
        );
    }
    useEffect(
        loadItem, [itemIdContainer]
    );
    const handleStatusChange = (event: ChangeEvent<HTMLSelectElement>) => {
        if (!item) return;
        const {value} = event.target;
        item.status = value as ItemStatus;
        axios.put<ItemRestricted>(`${config.item.url}/${item[config.item.idField as keyof ItemRestricted]}`, item).then(
            (response) => {
                setItem(response.data);
                addToast(t("toast_item_save_successful"), ToastVariant.SUCCESS);
            }
        ).catch(
            (error) => {
                console.error('Error fetching data:', error);
                addToast(t("toast_item_save_failed"), ToastVariant.ERROR);
            }
        );
    };

    return (
        <Form className="edit-item-form">
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_link")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="text"
                        value={item?.product.link}
                        disabled
                    />
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_name")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="text"
                        value={item?.product.title}
                        disabled
                    />
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_description")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        value={item?.product.description}
                        disabled
                    />
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_quantity")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="number"
                        value={item?.quantity}
                        disabled
                    />
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_status")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Select
                        disabled={!editStatusAllowed}
                        onChange={handleStatusChange}
                        value={item?.status}
                    >
                        <option value={ItemStatus.AVAILABLE}>{t("status_available")}</option>
                        <option value={ItemStatus.RESERVED}>{t("status_reserved")}</option>
                        <option value={ItemStatus.PURCHASED}>{t("status_purchased")}</option>
                    </Form.Select>
                </Col>
            </Row>
        </Form>
    );
}
