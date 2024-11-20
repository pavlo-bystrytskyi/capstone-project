import {useEffect, useState} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import ItemIdContainer from "../../../../../type/ItemIdContainer.tsx";
import ItemStatus from "../../../../../type/ItemStatus.tsx";
import RegistryConfig from "../../../../../type/RegistryConfig.tsx";
import {emptyItem} from "../../../../../type/EmptyItem.tsx";
import ItemRestricted from "../../../../../type/ItemRestricted.tsx";
import itemFormSchema from "../../../../../schema/ItemFormSchema.tsx";
import {Button, Col, Form, Row} from "react-bootstrap";

export default function EditItemComponent(
    {
        config,
        itemId,
        removeItemId,
    }: {
        readonly config: RegistryConfig;
        readonly itemId: ItemIdContainer;
        readonly removeItemId: (itemId: ItemIdContainer) => void;
    }
) {
    const {t} = useTranslation();
    const [productLinkRequired, setProductLinkRequired] = useState<boolean>(true)
    const {
        register,
        handleSubmit,
        setValue,
        trigger,
        watch,
        formState: {errors},
    } = useForm<ItemRestricted>({
        resolver: yupResolver(itemFormSchema),
        defaultValues: emptyItem,
    });

    const onProductLinkChange = () => {
        trigger("product.link").then((result) => {
            setProductLinkRequired(!result);
        }).catch(() => {
            setProductLinkRequired(true);
        });
    }
    const linkValue = watch("product.link");

    const loadItem = () => {
        axios
            .get<ItemRestricted>(`${config.item.url}/${itemId.privateId}`)
            .then((result) => {
                const item = result.data;
                Object.entries(item).forEach(([key, value]) => {
                    setValue(key as keyof ItemRestricted, value);
                });
                onProductLinkChange();
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }
    const removeItem = () => {
        axios
            .delete(`${config.item.url}/${itemId.privateId}`)
            .then(() => {
                removeItemId(itemId);
            })
            .catch((error) => {
                console.error("Error deleting item:", error);
            });
    };
    const saveItem: SubmitHandler<ItemRestricted> = (data) => {
        axios
            .put(`${config.item.url}/${itemId.privateId}`, data)
            .then(() => {
                console.log("ItemRestricted saved successfully");
            })
            .catch((error) => {
                console.error("Error saving item:", error);
            });
    };

    useEffect(loadItem, [itemId, setValue, config]);
    useEffect(onProductLinkChange, [linkValue, errors.product?.link?.message]);

    return (
        <Form className="edit-item-form" onSubmit={handleSubmit(saveItem)}>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_link")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="text"
                        {...register("product.link")}
                        isInvalid={!!errors.product?.link?.message}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.product?.link?.message && t(errors.product.link.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_name")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="text"
                        {...register("product.title")}
                        isInvalid={!!errors.product?.title?.message}
                        disabled={productLinkRequired}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.product?.title?.message && t(errors.product.title.message)}
                    </Form.Control.Feedback>
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
                        {...register("product.description")}
                        isInvalid={!!errors.product?.description?.message}
                        disabled={productLinkRequired}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.product?.description?.message && t(errors.product.description.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_quantity")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="number"
                        {...register("quantity")}
                        isInvalid={!!errors.quantity?.message}
                        disabled={productLinkRequired}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.quantity?.message && t(errors.quantity.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_status")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Select
                        {...register("status")}
                        isInvalid={!!errors.status?.message}
                        disabled={productLinkRequired}
                    >
                        <option value={ItemStatus.AVAILABLE}>{t("status_available")}</option>
                        <option value={ItemStatus.RESERVED}>{t("status_reserved")}</option>
                        <option value={ItemStatus.PURCHASED}>{t("status_purchased")}</option>
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">
                        {errors.status?.message && t(errors.status.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3">
                <Col sm={{span: 8, offset: 4}}>
                    <Button variant="primary" type="submit" disabled={productLinkRequired}>
                        {t("item_save")}
                    </Button>
                    <Button variant="danger" type="button" className="ms-2"
                            onClick={removeItem}>
                        {t("item_remove")}
                    </Button>
                </Col>
            </Row>
        </Form>
    );
}
